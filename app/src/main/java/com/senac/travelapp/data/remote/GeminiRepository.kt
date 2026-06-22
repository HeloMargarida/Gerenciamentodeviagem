package com.senac.travelapp.data.remote

import com.senac.travelapp.BuildConfig
import com.senac.travelapp.data.remote.api.Content
import com.senac.travelapp.data.remote.api.GeminiRequest
import com.senac.travelapp.data.remote.api.Part
import com.senac.travelapp.data.remote.api.RetrofitClient

object GeminiRepository {

    suspend fun gerarRoteiro(prompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "Chave da API do Gemini não configurada. " +
                        "Adicione GEMINI_API_KEY=sua_chave no arquivo local.properties."
            )
        }

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            )
        )

        val response = RetrofitClient.geminiApiService.gerarConteudo(
            apiKey = apiKey,
            request = request
        )

        val texto = response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.joinToString(separator = "") { it.text }

        return texto?.trim()
            ?: throw Exception("Resposta da IA vazia ou em formato inesperado.")
    }
}