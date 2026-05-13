package com.senac.travelapp.data.repository

import com.senac.travelapp.data.local.dao.TravelDao
import com.senac.travelapp.data.local.entity.TravelEntity
import kotlinx.coroutines.flow.Flow

class TravelRepository(private val travelDao: TravelDao) {

    fun getTravelsByUser(userId: Int): Flow<List<TravelEntity>> =
        travelDao.getTravelsByUser(userId)

    suspend fun insertTravel(travel: TravelEntity) =
        travelDao.insertTravel(travel)

    suspend fun updateTravel(travel: TravelEntity) =
        travelDao.updateTravel(travel)

    suspend fun deleteTravel(travel: TravelEntity) =
        travelDao.deleteTravel(travel)
}