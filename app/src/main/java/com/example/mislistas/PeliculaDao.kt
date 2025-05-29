package com.example.mislistas

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert

@Dao
interface PeliculaDao {
    @Query("SELECT * FROM Peliculas ORDER BY popularidad DESC LIMIT 5")
    suspend fun obtenerTop5Populares(): List<Pelicula>

    @Update
    suspend fun actualizar(pelicula: Pelicula)

    @Insert
    suspend fun insertar(pelicula: Pelicula): Long

    @Insert
    suspend fun insertarVarios(vararg peliculas: Pelicula)

    @Query("""
        UPDATE Peliculas SET popularidad = (
            SELECT COUNT(*) FROM Lista_Peliculas_Contenido WHERE id_pelicula = Peliculas.id
        )
    """)
    suspend fun actualizarPopularidadTodas()

    @Query("SELECT * FROM Peliculas")
    suspend fun getAll(): List<Pelicula>
}
