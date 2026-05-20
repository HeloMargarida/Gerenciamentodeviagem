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

    /**
     * Busca a primeira viagem do usuário cujo destino contenha [cidade]
     * (case-insensitive) e cuja data atual esteja dentro do intervalo.
     * [hoje] deve ser passado no formato "dd/MM/yyyy".
     */
    @Query("""
        SELECT * FROM travels
        WHERE userId = :userId
          AND LOWER(destino) LIKE '%' || LOWER(:cidade) || '%'
          AND substr(dataInicio, 7, 4) || substr(dataInicio, 4, 2) || substr(dataInicio, 1, 2)
              <= substr(:hoje, 7, 4) || substr(:hoje, 4, 2) || substr(:hoje, 1, 2)
          AND substr(dataFim, 7, 4) || substr(dataFim, 4, 2) || substr(dataFim, 1, 2)
              >= substr(:hoje, 7, 4) || substr(:hoje, 4, 2) || substr(:hoje, 1, 2)
        LIMIT 1
    """)
    suspend fun getTravelByCity(userId: Int, cidade: String, hoje: String): TravelEntity?
}
