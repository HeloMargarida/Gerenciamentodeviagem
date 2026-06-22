package com.senac.travelapp.data.remote.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GeminiApiService {

    @POST("v1beta/models/{model}:generateContent")
    suspend fun gerarConteudo(
        @Path("model") model: String = "gemini-2.5-flash",
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}