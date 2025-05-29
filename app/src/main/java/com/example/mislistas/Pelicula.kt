package com.example.mislistas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Peliculas")
data class Pelicula(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val duracion: Int?,
    val genero: String?,
    val popularidad: Int = 0 // Nuevo campo para popularidad
)

// Si ya tienes datos, deberás migrar la base de datos para no perderlos.
