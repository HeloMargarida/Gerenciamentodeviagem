
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
    val userId: Int,
    val gastos: Double = 0.0   // NOVO: total de gastos (por enquanto sempre 0)
)

