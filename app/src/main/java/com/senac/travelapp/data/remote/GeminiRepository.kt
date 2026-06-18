package com.senac.travelapp.data.remote

import com.senac.travelapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Repositório responsável por chamar a API do Google Gemini para gerar
 * um roteiro de viagem personalizado.
 *
 * O professor pediu que a chamada fosse feita "no estilo curl", ou seja,
 * uma requisição HTTP simples e direta (mesma URL, mesmos headers, mesmo
 * corpo JSON que se usaria no terminal com o comando curl), sem depender
 * de bibliotecas externas como OkHttp/Retrofit.
 *
 * Por isso aqui usamos HttpURLConnection, que já vem nativo no Android/Java.
 * Em Android NÃO é viável (e nem seguro) executar o binário curl de fato via
 * ProcessBuilder/Runtime.exec, pois o sistema não garante esse binário no
 * dispositivo. HttpURLConnection produz exatamente a mesma requisição que
 * um curl faria, só que de forma nativa e confiável em qualquer aparelho.
 *
 * Equivalente em curl, para referência/depuração manual no terminal:
 *
 * curl --silent --show-error --max-time 60 \
 *   -X POST \
 *   "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=SUA_CHAVE" \
 *   -H "Content-Type: application/json" \
 *   -d '{ "contents": [ { "parts": [ { "text": "..." } ] } ] }'
 */
object GeminiRepository {

    private const val MODEL = "gemini-2.5-flash"
    private const val BASE_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    /**
     * Gera o roteiro de viagem chamando a API do Gemini.
     *
     * @param prompt texto completo já formatado com destino, datas, orçamento,
     *               preferências e clima.
     * @return texto do roteiro gerado pela IA.
     * @throws Exception em caso de erro de rede, chave inválida, timeout, etc.
     */
    suspend fun gerarRoteiro(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "Chave da API do Gemini não configurada. " +
                        "Adicione GEMINI_API_KEY=sua_chave no arquivo local.properties."
            )
        }

        val url = URL("$BASE_URL?key=$apiKey")
        val bodyJson = montarCorpoRequisicao(prompt)

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doOutput = true
            connectTimeout = 60_000
            readTimeout = 60_000
        }

        try {
            // Envia o corpo da requisição (equivalente ao "-d" do curl)
            val bytes = bodyJson.toByteArray(StandardCharsets.UTF_8)
            connection.setFixedLengthStreamingMode(bytes.size)
            val outputStream: OutputStream = connection.outputStream
            outputStream.use { it.write(bytes) }

            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val resposta = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                reader.readText()
            }

            if (statusCode !in 200..299) {
                throw Exception("Erro na API do Gemini (HTTP $statusCode): $resposta")
            }

            extrairTextoResposta(resposta)
        } finally {
            connection.disconnect()
        }
    }

    /** Monta o JSON do corpo da requisição no formato esperado pela API do Gemini. */
    private fun montarCorpoRequisicao(prompt: String): String {
        val parts = JSONArray().put(JSONObject().put("text", prompt))
        val content = JSONObject().put("parts", parts)
        val contents = JSONArray().put(content)

        // Configurações de geração (opcional, ajuda a controlar o tamanho/consistência da resposta)
        val generationConfig = JSONObject()
            .put("temperature", 0.7)
            .put("maxOutputTokens", 2048)

        return JSONObject()
            .put("contents", contents)
            .put("generationConfig", generationConfig)
            .toString()
    }

    /** Extrai o texto gerado da resposta JSON da API do Gemini. */
    private fun extrairTextoResposta(json: String): String {
        val raiz = JSONObject(json)
        val candidates = raiz.optJSONArray("candidates")
            ?: throw Exception("Resposta da IA sem candidatos. JSON recebido: $json")

        if (candidates.length() == 0) {
            throw Exception("Resposta da IA vazia. JSON recebido: $json")
        }

        val primeiroCandidato = candidates.getJSONObject(0)
        val content = primeiroCandidato.getJSONObject("content")
        val parts = content.getJSONArray("parts")

        val textoCompleto = StringBuilder()
        for (i in 0 until parts.length()) {
            textoCompleto.append(parts.getJSONObject(i).optString("text", ""))
        }

        return textoCompleto.toString().trim()
    }
}