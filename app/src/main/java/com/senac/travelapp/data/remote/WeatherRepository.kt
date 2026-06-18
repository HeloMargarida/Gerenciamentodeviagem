package com.senac.travelapp.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Repositório de geocodificação + clima usando a API gratuita Open-Meteo
 * (não exige chave de API). Usado para descobrir o clima previsto do
 * destino e incluir essa informação no prompt enviado para a IA, conforme
 * pedido: "dicas de roteiros de acordo com o clima do destino".
 */
object WeatherRepository {

    /**
     * Busca um resumo simples do clima atual/previsto para o nome da cidade informado.
     * Retorna algo como "ensolarado, 28°C" ou null se não conseguir obter o dado
     * (nesse caso o roteiro é gerado normalmente, só sem a dica de clima).
     */
    suspend fun obterResumoClima(destino: String): String? = withContext(Dispatchers.IO) {
        try {
            val (lat, lon) = geocodificar(destino) ?: return@withContext null
            buscarClima(lat, lon)
        } catch (e: Exception) {
            null
        }
    }

    private fun geocodificar(destino: String): Pair<Double, Double>? {
        val nomeCodificado = java.net.URLEncoder.encode(destino, "UTF-8")
        val url = URL(
            "https://geocoding-api.open-meteo.com/v1/search?name=$nomeCodificado&count=1&language=pt"
        )
        val resposta = chamarGet(url) ?: return null

        val results = JSONObject(resposta).optJSONArray("results") ?: return null
        if (results.length() == 0) return null

        val primeiro = results.getJSONObject(0)
        return primeiro.getDouble("latitude") to primeiro.getDouble("longitude")
    }

    private fun buscarClima(lat: Double, lon: Double): String? {
        val url = URL(
            "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
                    "&current=temperature_2m,weather_code&timezone=auto"
        )
        val resposta = chamarGet(url) ?: return null

        val current = JSONObject(resposta).optJSONObject("current") ?: return null
        val temperatura = current.optDouble("temperature_2m", Double.NaN)
        val codigoClima = current.optInt("weather_code", -1)

        val descricao = descreverCodigoClima(codigoClima)
        return if (!temperatura.isNaN()) {
            "$descricao, aproximadamente ${temperatura.toInt()}°C"
        } else {
            descricao
        }
    }

    private fun chamarGet(url: URL): String? {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
        }
        return try {
            val statusCode = connection.responseCode
            if (statusCode !in 200..299) return null
            BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
                .use { it.readText() }
        } catch (e: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    /** Tradução simplificada dos códigos WMO de clima usados pela Open-Meteo. */
    private fun descreverCodigoClima(codigo: Int): String = when (codigo) {
        0 -> "céu limpo"
        1, 2, 3 -> "parcialmente nublado"
        45, 48 -> "neblina"
        51, 53, 55 -> "garoa leve"
        61, 63, 65 -> "chuva"
        71, 73, 75 -> "neve"
        80, 81, 82 -> "pancadas de chuva"
        95, 96, 99 -> "tempestade"
        else -> "clima variável"
    }
}