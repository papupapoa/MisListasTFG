package com.example.mislistas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Libros")
data class Libro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val genero: String?,
    val popularidad: Int = 0 // Nuevo campo para popularidad
)
