package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.ui.viewmodel.AuthViewModel
import com.senac.travelapp.ui.viewmodel.TravelViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTravelScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    travelViewModel: TravelViewModel = viewModel()
) {
    val loggedUserId by authViewModel.loggedUserId.collectAsState()

    var destino by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Lazer") }          // "Lazer" | "Negócios"
    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }
    var orcamento by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    // ── DatePicker states ────────────────────────────────────────────────
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFim by remember { mutableStateOf(false) }
    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFim = rememberDatePickerState()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun Long.toDateString(): String =
        Instant.ofEpochMilli(this)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
            .format(formatter)

    // ── DatePicker – Início ──────────────────────────────────────────────
    if (showDatePickerInicio) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateInicio.selectedDateMillis?.let {
                        dataInicio = it.toDateString()
                    }
                    showDatePickerInicio = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerInicio = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerStateInicio)
        }
    }

    // ── DatePicker – Fim ─────────────────────────────────────────────────
    if (showDatePickerFim) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerFim = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateFim.selectedDateMillis?.let {
                        dataFim = it.toDateString()
                    }
                    showDatePickerFim = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerFim = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerStateFim)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Viagem") },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ── Destino ──────────────────────────────────────────────────
            OutlinedTextField(
                value = destino,
                onValueChange = { destino = it; erro = "" },
                label = { Text("Destino") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Tipo ─────────────────────────────────────────────────────
            Text("Tipo de viagem", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("Lazer", "Negócios").forEach { opcao ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(
                            selected = tipo == opcao,
                            onClick = { tipo = opcao }
                        )
                        Text(opcao)
                    }
                }
            }

            // ── Data Início ──────────────────────────────────────────────
            OutlinedTextField(
                value = dataInicio,
                onValueChange = {},
                label = { Text("Data de início") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePickerInicio = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Escolher data")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Data Fim ─────────────────────────────────────────────────
            OutlinedTextField(
                value = dataFim,
                onValueChange = {},
                label = { Text("Data de fim") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePickerFim = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Escolher data")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Orçamento ────────────────────────────────────────────────
            OutlinedTextField(
                value = orcamento,
                onValueChange = { orcamento = it; erro = "" },
                label = { Text("Orçamento (R$)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // ── Erro ─────────────────────────────────────────────────────
            if (erro.isNotEmpty()) {
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ── Salvar ───────────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        destino.isBlank() -> erro = "Informe o destino."
                        dataInicio.isBlank() -> erro = "Informe a data de início."
                        dataFim.isBlank() -> erro = "Informe a data de fim."
                        orcamento.isBlank() -> erro = "Informe o orçamento."
                        orcamento.toDoubleOrNull() == null -> erro = "Orçamento inválido."
                        else -> {
                            travelViewModel.insertTravel(
                                TravelEntity(
                                    destino = destino.trim(),
                                    tipo = tipo,
                                    dataInicio = dataInicio,
                                    dataFim = dataFim,
                                    orcamento = orcamento.toDouble(),
                                    userId = loggedUserId
                                )
                            )
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Salvar Viagem")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
