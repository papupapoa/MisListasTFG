package com.example.mislistas

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "Lista_Series_Contenido",
    primaryKeys = ["id_lista_serie", "id_serie"],
    foreignKeys = [
        ForeignKey(entity = ListaSeries::class, parentColumns = ["id"], childColumns = ["id_lista_serie"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Serie::class, parentColumns = ["id"], childColumns = ["id_serie"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ListaSeriesContenido(
    val id_lista_serie: Int,
    val id_serie: Int,
    val valoracion: Int?
)
