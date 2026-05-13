package com.senac.travelapp.data.local.dao

import androidx.room.*
import com.senac.travelapp.data.local.entity.TravelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {

    @Query("SELECT * FROM travels WHERE userId = :userId ORDER BY id DESC")
    fun getTravelsByUser(userId: Int): Flow<List<TravelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravel(travel: TravelEntity)

    @Update
    suspend fun updateTravel(travel: TravelEntity)

    @Delete
    suspend fun deleteTravel(travel: TravelEntity)
}