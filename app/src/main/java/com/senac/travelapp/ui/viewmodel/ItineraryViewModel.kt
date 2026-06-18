package com.senac.travelapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.data.remote.GeminiRepository
import com.senac.travelapp.data.remote.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado da tela de geração de roteiro com IA. */
data class ItineraryUiState(
    val carregando: Boolean = false,
    val roteiro: String? = null,
    val erro: String? = null
)

class ItineraryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ItineraryUiState())
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()

    /**
     * Gera um roteiro de viagem com IA considerando:
     * - destino, datas e orçamento da viagem (TravelEntity já cadastrada)
     * - preferências extras informadas pelo usuário na tela (hotéis, pontos
     *   turísticos pagos/gratuitos, restaurantes)
     * - clima atual do destino (buscado automaticamente via Open-Meteo)
     */
    fun gerarRoteiro(viagem: TravelEntity, preferenciasExtras: String) {
        viewModelScope.launch {
            _uiState.value = ItineraryUiState(carregando = true)

            try {
                val resumoClima = WeatherRepository.obterResumoClima(viagem.destino)
                val prompt = montarPrompt(viagem, preferenciasExtras, resumoClima)
                val roteiro = GeminiRepository.gerarRoteiro(prompt)

                _uiState.value = ItineraryUiState(carregando = false, roteiro = roteiro)
            } catch (e: Exception) {
                _uiState.value = ItineraryUiState(
                    carregando = false,
                    erro = e.message ?: "Erro desconhecido ao gerar roteiro."
                )
            }
        }
    }

    fun limparEstado() {
        _uiState.value = ItineraryUiState()
    }

    private fun montarPrompt(
        viagem: TravelEntity,
        preferenciasExtras: String,
        resumoClima: String?
    ): String {
        val climaTexto = resumoClima?.let { "Clima previsto no destino: $it." }
            ?: "Não foi possível obter o clima do destino agora; considere um clima típico para a região e a época do ano."

        return """
            Você é um assistente especialista em planejamento de viagens.
            Monte um roteiro de viagem detalhado, dia a dia, com base nas informações abaixo.

            Destino: ${viagem.destino}
            Tipo de viagem: ${viagem.tipo}
            Data de início: ${viagem.dataInicio}
            Data de término: ${viagem.dataFim}
            Orçamento total disponível: R$ ${"%.2f".format(viagem.orcamento)}
            $climaTexto

            Preferências adicionais informadas pelo viajante:
            ${preferenciasExtras.ifBlank { "Nenhuma preferência adicional informada." }}

            O roteiro deve incluir obrigatoriamente:
            1. Sugestões de hospedagem (hotéis/pousadas) que respeitem o orçamento informado, indicando uma faixa de preço aproximada por noite.
            2. Pontos turísticos pagos e gratuitos, separados claramente em duas listas.
            3. Dicas de restaurantes (variando entre opções econômicas e mais sofisticadas).
            4. Um roteiro dia a dia (manhã, tarde e noite) considerando o clima informado — por exemplo, priorizando atividades ao ar livre em dias de sol e atividades indoor em dias de chuva.
            5. Uma estimativa de gastos aproximada por categoria (hospedagem, alimentação, passeios, transporte), garantindo que a soma fique dentro do orçamento informado.

            Responda em português do Brasil, em formato de texto organizado com títulos e listas, sem usar markdown de tabelas.
        """.trimIndent()
    }
}