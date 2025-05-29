package com.example.mislistas

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert

@Dao
interface SerieDao {
    @Query("SELECT * FROM Series ORDER BY popularidad DESC LIMIT 5")
    suspend fun obtenerTop5Populares(): List<Serie>

    @Update
    suspend fun actualizar(serie: Serie)

    @Insert
    suspend fun insertar(serie: Serie): Long

    @Insert
    suspend fun insertarVarios(vararg series: Serie)

    @Query("""
        UPDATE Series SET popularidad = (
            SELECT COUNT(*) FROM Lista_Series_Contenido WHERE id_serie = Series.id
        )
    """)
    suspend fun actualizarPopularidadTodas()

    @Query("SELECT * FROM Series")
    suspend fun getAll(): List<Serie>
}
