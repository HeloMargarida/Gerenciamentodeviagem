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

        val candidate = response.candidates?.firstOrNull()

        val texto = candidate
            ?.content
            ?.parts
            ?.joinToString(separator = "") { it.text }
            ?.trim()
            ?: throw Exception("Resposta da IA vazia ou em formato inesperado.")

        // Se a resposta foi cortada por limite de tamanho, avisa o usuario
        return if (candidate.finishReason == "MAX_TOKENS") {
            texto + "\n\n⚠️ O roteiro foi cortado por ser muito extenso para o período " +
                    "informado. Tente reduzir o número de dias da viagem ou peça um " +
                    "roteiro mais resumido nas preferências."
        } else {
            texto
        }
    }
}