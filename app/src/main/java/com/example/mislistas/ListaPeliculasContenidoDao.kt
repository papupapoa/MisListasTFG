package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.mislistas.ListaPeliculasContenido

@Dao
interface ListaPeliculasContenidoDao {
    @Insert
    suspend fun insertar(contenido: ListaPeliculasContenido)

    @Delete
    suspend fun borrar(contenido: ListaPeliculasContenido)

    @Query("DELETE FROM Lista_Peliculas_Contenido WHERE id_lista_pelicula = :idLista")
    suspend fun borrarPorLista(idLista: Int)

    @Query("SELECT * FROM Lista_Peliculas_Contenido WHERE id_lista_pelicula = :idLista")
    suspend fun obtenerPorLista(idLista: Int): List<ListaPeliculasContenido>
}
