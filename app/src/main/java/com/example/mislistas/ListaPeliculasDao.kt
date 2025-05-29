package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ListaPeliculasDao {
    @Insert
    suspend fun insertar(lista: ListaPeliculas): Long

    @Query("SELECT * FROM Lista_Peliculas WHERE id_usuario = :idUsuario")
    suspend fun obtenerPorUsuario(idUsuario: Int): List<ListaPeliculas>

    @Query("SELECT * FROM Lista_Peliculas WHERE id = :idLista LIMIT 1")
    suspend fun obtenerPorId(idLista: Int): ListaPeliculas?

    @Update
    suspend fun actualizar(lista: ListaPeliculas)
}
