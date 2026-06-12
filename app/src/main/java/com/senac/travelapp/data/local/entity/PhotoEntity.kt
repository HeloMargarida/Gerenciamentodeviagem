package com.senac.travelapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = TravelEntity::class,
        parentColumns = ["id"],
        childColumns = ["travelId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val travelId: Int,
    val uri: String,
    val dataCriacao: Long = System.currentTimeMillis()
)