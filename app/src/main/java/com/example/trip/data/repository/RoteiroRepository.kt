package com.example.trip.data.repository

import com.example.trip.data.remote.gemini.Content
import com.example.trip.data.remote.gemini.GeminiApiService
import com.example.trip.data.remote.gemini.GeminiRequest
import com.example.trip.data.remote.gemini.GeminiResponse
import com.example.trip.data.remote.gemini.GenerationConfig
import com.example.trip.data.remote.gemini.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository responsible for generating tourist itineraries through the Gemini LLM.
 *
 * It builds a Portuguese prompt from the trip data informed by the user, calls the
 * Gemini API and extracts the generated text from the response.
 */
class RoteiroRepository(
    private val geminiApiService: GeminiApiService,
    private val apiKey: String
) {

    sealed class RoteiroResult {
        data class Success(val itinerary: String) : RoteiroResult()
        data class Error(val message: String) : RoteiroResult()
    }

    suspend fun generateItinerary(
        destination: String,
        startDate: Long,
        endDate: Long,
        interests: String,
        tripType: String? = null
    ): RoteiroResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext RoteiroResult.Error(
                "Chave de API do Gemini não configurada. Adicione GEMINI_API_KEY ao local.properties."
            )
        }

        val prompt = buildPrompt(destination, startDate, endDate, interests, tripType)
        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)), role = "user")
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 2048
            )
        )

        var lastError = "Erro ao gerar o roteiro"
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                val response = geminiApiService.generateContent(apiKey = apiKey, request = request)
                return@withContext parseResponse(response)
            } catch (e: HttpException) {
                val code = e.code()
                lastError = messageForHttpCode(code)
                if (code !in RETRYABLE_HTTP_CODES || attempt == MAX_ATTEMPTS - 1) {
                    return@withContext RoteiroResult.Error(lastError)
                }
            } catch (e: IOException) {
                lastError = "Falha de conexão. Verifique sua internet e tente novamente."
                if (attempt == MAX_ATTEMPTS - 1) {
                    return@withContext RoteiroResult.Error(lastError)
                }
            } catch (e: Exception) {
                return@withContext RoteiroResult.Error(e.message ?: "Erro ao gerar o roteiro")
            }
            // Exponential backoff before the next attempt: 1.5s, 3s, ...
            delay(INITIAL_BACKOFF_MS shl attempt)
        }
        RoteiroResult.Error(lastError)
    }

    private fun parseResponse(response: GeminiResponse): RoteiroResult {
        response.error?.let {
            return RoteiroResult.Error(it.message ?: "Erro retornado pela API do Gemini")
        }

        val blockReason = response.promptFeedback?.blockReason
        if (blockReason != null) {
            return RoteiroResult.Error("Não foi possível gerar o roteiro (motivo: $blockReason).")
        }

        val text = response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.joinToString(separator = "\n") { it.text }
            ?.trim()

        return if (text.isNullOrBlank()) {
            RoteiroResult.Error("A IA não retornou um roteiro. Tente novamente.")
        } else {
            RoteiroResult.Success(text)
        }
    }

    private fun messageForHttpCode(code: Int): String = when (code) {
        503 -> "O serviço de IA está sobrecarregado no momento. Tente novamente em instantes."
        429 -> "Muitas solicitações em pouco tempo. Aguarde um momento e tente novamente."
        in 500..599 -> "O serviço de IA está temporariamente indisponível (HTTP $code). Tente novamente."
        else -> "Erro na comunicação com o Gemini (HTTP $code)."
    }

    private fun buildPrompt(
        destination: String,
        startDate: Long,
        endDate: Long,
        interests: String,
        tripType: String?
    ): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val start = formatter.format(Date(startDate))
        val end = formatter.format(Date(endDate))
        val days = ((endDate - startDate) / MILLIS_PER_DAY + 1).coerceAtLeast(1)

        return buildString {
            appendLine("Você é um especialista em turismo e planejamento de viagens.")
            appendLine("Crie um roteiro turístico detalhado e personalizado em português do Brasil.")
            appendLine()
            appendLine("Dados da viagem:")
            appendLine("- Destino: $destination")
            appendLine("- Período: de $start a $end (aproximadamente $days dia(s))")
            if (!tripType.isNullOrBlank()) {
                appendLine("- Tipo de viagem: $tripType")
            }
            appendLine("- Interesses do viajante: $interests")
            appendLine()
            appendLine("Instruções para o roteiro:")
            appendLine("- Organize o roteiro dia a dia (Dia 1, Dia 2, ...).")
            appendLine("- Para cada dia, sugira atividades para manhã, tarde e noite.")
            appendLine("- Inclua sugestões de pontos turísticos, gastronomia local e dicas práticas.")
            appendLine("- Leve em conta os interesses informados.")
            appendLine("- Use uma linguagem amigável e objetiva, formatada de forma legível.")
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

        /** Maximum number of attempts (1 initial call + retries) for transient failures. */
        const val MAX_ATTEMPTS = 3

        /** Base backoff delay in ms; doubles on each retry (1.5s, 3s, ...). */
        const val INITIAL_BACKOFF_MS = 1500L

        /** HTTP status codes treated as transient and worth retrying. */
        val RETRYABLE_HTTP_CODES = setOf(429, 500, 502, 503, 504)
    }
}

