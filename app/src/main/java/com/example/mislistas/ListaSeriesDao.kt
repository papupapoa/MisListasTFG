package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ListaSeriesDao {
    @Insert
    suspend fun insertar(lista: ListaSeries): Long

    @Query("SELECT * FROM Lista_Series WHERE id_usuario = :idUsuario")
    suspend fun obtenerPorUsuario(idUsuario: Int): List<ListaSeries>

    @Query("SELECT * FROM Lista_Series WHERE id = :idLista LIMIT 1")
    suspend fun obtenerPorId(idLista: Int): ListaSeries?

    @Update
    suspend fun actualizar(lista: ListaSeries)
}
