package com.example.mislistas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val contraseña: String,
    val fecha_registro: Long = System.currentTimeMillis()
)
