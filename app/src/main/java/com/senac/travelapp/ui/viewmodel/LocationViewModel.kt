package com.senac.travelapp.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.senac.travelapp.data.local.AppDatabase
import com.senac.travelapp.data.local.entity.TravelEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class LocationUiState {
    object Idle : LocationUiState()
    object Loading : LocationUiState()
    data class CityFound(val cidade: String) : LocationUiState()
    data class TravelFound(val viagem: TravelEntity, val cidade: String) : LocationUiState()
    data class NoTravel(val cidade: String) : LocationUiState()
    data class Error(val message: String) : LocationUiState()
}

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val travelDao =
        AppDatabase.getInstance(application).travelDao()

    private val _uiState = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchLocationAndSearch(userId: Int) {
        viewModelScope.launch {
            _uiState.value = LocationUiState.Loading
            try {
                // 🔧 TEMPORÁRIO: cidade fixa para teste
                val cidade = "Blumenau"

                val hoje = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                val viagem = travelDao.getTravelByCity(
                    userId = userId,
                    cidade = cidade,
                    hoje = hoje
                )

                _uiState.value = if (viagem != null) {
                    LocationUiState.TravelFound(viagem, cidade)
                } else {
                    LocationUiState.NoTravel(cidade)
                }

            } catch (e: Exception) {
                _uiState.value = LocationUiState.Error("Erro: ${e.localizedMessage}")
            }
        }
    }
}