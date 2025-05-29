package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import com.example.mislistas.ListaSeriesContenido

@Dao
interface ListaSeriesContenidoDao {
    @Insert
    suspend fun insertar(contenido: ListaSeriesContenido): Long

    @Delete
    suspend fun borrar(contenido: ListaSeriesContenido)

    @Query("SELECT * FROM Lista_Series_Contenido WHERE id_lista_serie = :idLista")
    suspend fun obtenerPorLista(idLista: Int): List<ListaSeriesContenido>

    @Query("DELETE FROM Lista_Series_Contenido WHERE id_lista_serie = :idLista")
    suspend fun borrarPorLista(idLista: Int)
}
