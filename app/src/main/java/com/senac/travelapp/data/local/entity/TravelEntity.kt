package com.senac.travelapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travels")
data class TravelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destino: String,
    val tipo: String,           // "Lazer" ou "Negócios"
    val dataInicio: String,
    val dataFim: String,
    val orcamento: Double,
    val userId: Int
)
