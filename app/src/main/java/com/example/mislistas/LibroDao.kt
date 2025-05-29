package com.example.mislistas

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert

@Dao
interface LibroDao {
    @Query("SELECT * FROM Libros ORDER BY popularidad DESC LIMIT 5")
    suspend fun obtenerTop5Populares(): List<Libro>

    @Update
    suspend fun actualizar(libro: Libro)

    @Insert
    suspend fun insertar(libro: Libro): Long

    @Insert
    suspend fun insertarVarios(vararg libros: Libro)

    @Query("""
        UPDATE Libros SET popularidad = (
            SELECT COUNT(*) FROM Lista_Libros_Contenido WHERE id_libro = Libros.id
        )
    """)
    suspend fun actualizarPopularidadTodas()

    @Query("SELECT * FROM Libros")
    suspend fun getAll(): List<Libro>
}
