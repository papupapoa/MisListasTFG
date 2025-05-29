package com.example.mislistas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Lista_Libros",
    foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["id_usuario"], onDelete = ForeignKey.CASCADE)]
)
data class ListaLibros(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    @ColumnInfo(index = true) val id_usuario: Int,
    val valoracion: Int?
)
