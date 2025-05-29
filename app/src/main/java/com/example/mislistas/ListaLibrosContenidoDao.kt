package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import com.example.mislistas.ListaLibrosContenido

@Dao
interface ListaLibrosContenidoDao {
    @Insert
    suspend fun insertar(contenido: ListaLibrosContenido): Long

    @Delete
    suspend fun borrar(contenido: ListaLibrosContenido)

    @Query("SELECT * FROM Lista_Libros_Contenido WHERE id_lista_libro = :idLista")
    suspend fun obtenerPorLista(idLista: Int): List<ListaLibrosContenido>

    @Query("DELETE FROM Lista_Libros_Contenido WHERE id_lista_libro = :idLista")
    suspend fun borrarPorLista(idLista: Int)
}
