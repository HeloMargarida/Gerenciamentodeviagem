package com.senac.travelapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.senac.travelapp.data.local.AppDatabase
import com.senac.travelapp.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val photoDao = AppDatabase.getInstance(application).photoDao()

    fun getPhotosByTravel(travelId: Int): Flow<List<PhotoEntity>> =
        photoDao.getPhotosByTravel(travelId)

    fun addPhoto(travelId: Int, uri: String) {
        viewModelScope.launch {
            photoDao.insertPhoto(PhotoEntity(travelId = travelId, uri = uri))
        }
    }

    fun deletePhoto(photo: PhotoEntity) {
        viewModelScope.launch { photoDao.deletePhoto(photo) }
    }
}