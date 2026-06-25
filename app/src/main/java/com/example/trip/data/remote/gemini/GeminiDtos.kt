package com.example.trip.data.remote.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request/response data classes for the Gemini `generateContent` endpoint.
 * See: https://ai.google.dev/api/generate-content
 */

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double? = null,
    @SerialName("maxOutputTokens")
    val maxOutputTokens: Int? = null
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    @SerialName("promptFeedback")
    val promptFeedback: PromptFeedback? = null,
    val error: GeminiError? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    @SerialName("finishReason")
    val finishReason: String? = null
)

@Serializable
data class PromptFeedback(
    @SerialName("blockReason")
    val blockReason: String? = null
)

@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

