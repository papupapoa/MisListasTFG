package com.example.mislistas

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "Lista_Libros_Contenido",
    primaryKeys = ["id_lista_libro", "id_libro"],
    foreignKeys = [
        ForeignKey(entity = ListaLibros::class, parentColumns = ["id"], childColumns = ["id_lista_libro"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Libro::class, parentColumns = ["id"], childColumns = ["id_libro"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ListaLibrosContenido(
    val id_lista_libro: Int,
    val id_libro: Int,
    val valoracion: Int?
)
