package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ListaLibrosDao {
    @Insert
    suspend fun insertar(lista: ListaLibros): Long

    @Query("SELECT * FROM Lista_Libros WHERE id_usuario = :idUsuario")
    suspend fun obtenerPorUsuario(idUsuario: Int): List<ListaLibros>

    @Query("SELECT * FROM Lista_Libros WHERE id = :idLista LIMIT 1")
    suspend fun obtenerPorId(idLista: Int): ListaLibros?

    @Update
    suspend fun actualizar(lista: ListaLibros)
}
