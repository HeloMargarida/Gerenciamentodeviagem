package com.senac.travelapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.ui.viewmodel.AuthViewModel
import com.senac.travelapp.ui.viewmodel.TravelViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyTravelsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    travelViewModel: TravelViewModel = viewModel()
) {
    val loggedUserId by authViewModel.loggedUserId.collectAsStateWithLifecycle()
    val viagens by travelViewModel
        .getTravelsByUser(loggedUserId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var viagemEditando by remember { mutableStateOf<TravelEntity?>(null) }

    viagemEditando?.let { viagem ->
        EditTravelDialog(
            viagem = viagem,
            onDismiss = { viagemEditando = null },
            onConfirm = { atualizada ->
                travelViewModel.updateTravel(atualizada)
                viagemEditando = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Viagens") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (viagens.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma viagem cadastrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                items(items = viagens, key = { it.id }) { viagem ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value != SwipeToDismissBoxValue.Settled) {
                                travelViewModel.deleteTravel(viagem)
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFD32F2F)
                                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFD32F2F)
                                    else -> Color.Transparent
                                },
                                label = "swipe_color"
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color, shape = MaterialTheme.shapes.medium)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir",
                                    tint = Color.White
                                )
                            }
                        }
                    ) {
                        TravelCard(
                            viagem = viagem,
                            onLongClick = { viagemEditando = viagem }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TravelCard(
    viagem: TravelEntity,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (viagem.tipo == "Lazer")
                    Icons.Default.BeachAccess
                else
                    Icons.Default.Work,
                contentDescription = viagem.tipo,
                modifier = Modifier.size(40.dp),
                tint = if (viagem.tipo == "Lazer")
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = viagem.destino,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = viagem.tipo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${viagem.dataInicio} → ${viagem.dataFim}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "R$ ${"%.2f".format(viagem.orcamento)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTravelDialog(
    viagem: TravelEntity,
    onDismiss: () -> Unit,
    onConfirm: (TravelEntity) -> Unit
) {
    var destino by remember { mutableStateOf(viagem.destino) }
    var tipo by remember { mutableStateOf(viagem.tipo) }
    var dataInicio by remember { mutableStateOf(viagem.dataInicio) }
    var dataFim by remember { mutableStateOf(viagem.dataFim) }
    var orcamento by remember { mutableStateOf(viagem.orcamento.toString()) }
    var erro by remember { mutableStateOf("") }

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
        ) { DatePicker(state = datePickerStateInicio) }
    }

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
        ) { DatePicker(state = datePickerStateFim) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Viagem") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = destino,
                    onValueChange = { destino = it; erro = "" },
                    label = { Text("Destino") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Lazer", "Negócios").forEach { opcao ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = tipo == opcao, onClick = { tipo = opcao })
                            Text(opcao)
                        }
                    }
                }

                OutlinedTextField(
                    value = dataInicio,
                    onValueChange = {},
                    label = { Text("Data início") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerInicio = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dataFim,
                    onValueChange = {},
                    label = { Text("Data fim") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerFim = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = orcamento,
                    onValueChange = { orcamento = it; erro = "" },
                    label = { Text("Orçamento (R$)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (erro.isNotEmpty()) {
                    Text(
                        text = erro,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                when {
                    destino.isBlank() -> erro = "Informe o destino."
                    dataInicio.isBlank() -> erro = "Informe a data de início."
                    dataFim.isBlank() -> erro = "Informe a data de fim."
                    orcamento.isBlank() -> erro = "Informe o orçamento."
                    orcamento.toDoubleOrNull() == null -> erro = "Orçamento inválido."
                    else -> onConfirm(
                        viagem.copy(
                            destino = destino.trim(),
                            tipo = tipo,
                            dataInicio = dataInicio,
                            dataFim = dataFim,
                            orcamento = orcamento.toDouble()
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
