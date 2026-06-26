package com.example.trip.data.remote.gemini

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit service for the Gemini REST API.
 *
 * The model `gemini-2.5-flash` is targeted via the `generateContent` method,
 * authenticated through the `x-goog-api-key` header.
 */
interface GeminiApiService {

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

