package com.senac.travelapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.ui.viewmodel.AuthViewModel
import com.senac.travelapp.ui.viewmodel.LocationUiState
import com.senac.travelapp.ui.viewmodel.LocationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

// Abas da Bottom Bar
private enum class HomeTab { ROTEIRO, FOTOS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    locationViewModel: LocationViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val loggedUserId by authViewModel.loggedUserId.collectAsStateWithLifecycle()
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(HomeTab.ROTEIRO) }

    // Viagem ativa (null quando nao ha viagem em andamento)
    val viagemAtiva: TravelEntity? =
        (locationState as? LocationUiState.TravelFound)?.viagem

    // Inicializa OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // Permissao de localizacao
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) locationViewModel.fetchLocationAndSearch(loggedUserId)
    }

    LaunchedEffect(loggedUserId) {
        if (loggedUserId != 0) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            (context as? androidx.activity.ComponentActivity)?.finish()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                    label = { Text("Nova Viagem") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("nova_viagem")
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Luggage, contentDescription = null) },
                    label = { Text("Minhas Viagens") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("viagens")
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Sobre") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("sobre")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Travel App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menu")
                        }
                    }
                )
            },
            // Bottom Bar: aparece apenas quando ha viagem ativa
            bottomBar = {
                if (viagemAtiva != null) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == HomeTab.ROTEIRO,
                            onClick = {
                                selectedTab = HomeTab.ROTEIRO
                                val destinoCod = Uri.encode(viagemAtiva.destino)
                                val tipoCod = Uri.encode(viagemAtiva.tipo)
                                // dataInicio/dataFim estao no formato dd/MM/yyyy.
                                // Uri.encode nao escapa "/" por padrao, entao
                                // forcamos a codificacao dessa barra para nao
                                // quebrar os segmentos da rota de navegacao.
                                val inicioCod = Uri.encode(viagemAtiva.dataInicio, "").replace("/", "%2F")
                                val fimCod = Uri.encode(viagemAtiva.dataFim, "").replace("/", "%2F")
                                navController.navigate(
                                    "roteiro_ia/${viagemAtiva.id}/$destinoCod/$tipoCod/$inicioCod/$fimCod/${viagemAtiva.orcamento}/${viagemAtiva.userId}"
                                )
                            },
                            icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                            label = { Text("Roteiro") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == HomeTab.FOTOS,
                            onClick = {
                                selectedTab = HomeTab.FOTOS
                                navController.navigate(
                                    "fotos/${viagemAtiva.id}/${viagemAtiva.destino}"
                                )
                            },
                            icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                            label = { Text("Fotos") }
                        )
                    }
                }
            }
        ) { paddingValues ->
            // Conteudo principal: mapa ocupa toda a tela quando ha viagem ativa,
            // senao exibe apenas o card de status
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (viagemAtiva != null) {
                    // VIAGEM ATIVA: mapa em tela cheia + card sobreposto no topo
                    MapaViagemAtiva(
                        state = locationState,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Card de informacoes da viagem sobreposto no topo do mapa
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        TravelActiveCard(viagem = viagemAtiva, cidade = (locationState as LocationUiState.TravelFound).cidade)
                    }
                } else {
                    // SEM VIAGEM ATIVA: exibe card de status centralizado
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LocationCard(state = locationState)
                    }
                }
            }
        }
    }
}

// Mapa que ocupa espaco passado pelo modifier (usado quando ha viagem ativa)
@Composable
private fun MapaViagemAtiva(
    state: LocationUiState,
    modifier: Modifier = Modifier
) {
    val cidade = when (state) {
        is LocationUiState.TravelFound -> state.cidade
        else -> null
    } ?: return

    val context = LocalContext.current
    var geoPoint by remember(cidade) { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(cidade) {
        geoPoint = geocodeCidade(context, cidade)
    }

    val ponto = geoPoint

    if (ponto == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)
                controller.setCenter(ponto)
                val marker = Marker(this).apply {
                    position = ponto
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = cidade
                }
                overlays.add(marker)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            mapView.controller.animateTo(ponto)
            val marker = Marker(mapView).apply {
                position = ponto
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = cidade
            }
            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    )
}

private suspend fun geocodeCidade(context: Context, cidade: String): GeoPoint? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = android.location.Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val resultados = geocoder.getFromLocationName(cidade, 1)
            if (!resultados.isNullOrEmpty()) {
                GeoPoint(resultados[0].latitude, resultados[0].longitude)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

// Card de status (sem viagem ativa)
@Composable
private fun LocationCard(state: LocationUiState) {
    when (state) {
        is LocationUiState.Idle -> Unit

        is LocationUiState.Loading -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text("Obtendo sua localizacao...")
                }
            }
        }

        is LocationUiState.CityFound -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Cidade: ${state.cidade}")
                }
            }
        }

        is LocationUiState.NoTravel -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(state.cidade, style = MaterialTheme.typography.titleSmall)
                    }
                    Text(
                        "Nenhuma viagem ativa para esta cidade.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is LocationUiState.TravelFound -> {
            // Tratado no bloco principal (mapa + card sobrepostos)
        }

        is LocationUiState.Error -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = state.message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Card de viagem ativa (sobreposto ao mapa)
@Composable
private fun TravelActiveCard(viagem: TravelEntity, cidade: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Viagem ativa: ${viagem.destino}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                "${viagem.dataInicio} ate ${viagem.dataFim}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}
