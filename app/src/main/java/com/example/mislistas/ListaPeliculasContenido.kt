package com.example.mislistas

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "Lista_Peliculas_Contenido",
    primaryKeys = ["id_lista_pelicula", "id_pelicula"],
    foreignKeys = [
        ForeignKey(entity = ListaPeliculas::class, parentColumns = ["id"], childColumns = ["id_lista_pelicula"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Pelicula::class, parentColumns = ["id"], childColumns = ["id_pelicula"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ListaPeliculasContenido(
    val id_lista_pelicula: Int,
    val id_pelicula: Int,
    val valoracion: Int?
)
