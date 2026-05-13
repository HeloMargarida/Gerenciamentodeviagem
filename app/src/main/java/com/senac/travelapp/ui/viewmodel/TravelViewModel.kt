package com.senac.travelapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.senac.travelapp.data.local.AppDatabase
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.data.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TravelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TravelRepository(
        AppDatabase.getInstance(application).travelDao()
    )

    fun getTravelsByUser(userId: Int): Flow<List<TravelEntity>> =
        repository.getTravelsByUser(userId)

    fun insertTravel(travel: TravelEntity) {
        viewModelScope.launch { repository.insertTravel(travel) }
    }

    fun updateTravel(travel: TravelEntity) {
        viewModelScope.launch { repository.updateTravel(travel) }
    }

    fun deleteTravel(travel: TravelEntity) {
        viewModelScope.launch { repository.deleteTravel(travel) }
    }
}
