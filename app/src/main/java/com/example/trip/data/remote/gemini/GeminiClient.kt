package com.example.trip.data.remote.gemini

import com.example.trip.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * Builds the [GeminiApiService] used to talk to the Gemini REST API.
 *
 * Base URL: https://generativelanguage.googleapis.com/
 * JSON (de)serialization is handled by kotlinx.serialization.
 */
object GeminiClient {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    /** The API key read from BuildConfig (sourced from local.properties). */
    val apiKey: String get() = BuildConfig.GEMINI_API_KEY

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApiService::class.java)
    }
}

