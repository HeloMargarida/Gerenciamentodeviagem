package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.ui.viewmodel.ItineraryViewModel

/**
 * Tela de geração de roteiro de viagem com IA (Google Gemini).
 *
 * O usuário informa preferências extras (opcional) e a tela monta um prompt
 * com destino, datas, orçamento e clima do destino (buscado automaticamente),
 * enviando tudo para a IA gerar um roteiro completo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    navController: NavController,
    viagem: TravelEntity,
    itineraryViewModel: ItineraryViewModel = viewModel()
) {
    val uiState by itineraryViewModel.uiState.collectAsStateWithLifecycle()
    var preferencias by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roteiro com IA — ${viagem.destino}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Destino: ${viagem.destino}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${viagem.dataInicio} → ${viagem.dataFim}  •  Orçamento: R$ ${"%.2f".format(viagem.orcamento)}",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = preferencias,
                onValueChange = { preferencias = it },
                label = { Text("Preferências (opcional)") },
                placeholder = {
                    Text("Ex: viajando em família, prefiro hotéis perto da praia, gosto de comida local...")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = false
                ),
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    itineraryViewModel.gerarRoteiro(viagem, preferencias)
                },
                enabled = !uiState.carregando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Text(
                    text = if (uiState.carregando) "Gerando roteiro..." else "Gerar roteiro com IA",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            when {
                uiState.carregando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(
                                text = "Consultando clima e gerando sugestões...",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                uiState.erro != null -> {
                    Text(
                        text = "Erro ao gerar roteiro: ${uiState.erro}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                uiState.roteiro != null -> {
                    Text(
                        text = uiState.roteiro ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}