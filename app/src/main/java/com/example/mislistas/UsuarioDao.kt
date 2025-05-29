package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsuarioDao {
    @Insert suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM Usuarios WHERE email = :input OR nombre = :input LIMIT 1")
    suspend fun obtenerPorEmailONombre(input: String): Usuario?

    @Query("SELECT * FROM Usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerPorNombre(nombre: String): Usuario?

    @Query("SELECT * FROM Usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): Usuario?

    @Query("SELECT * FROM Usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Usuario?

    @Update
    suspend fun actualizar(usuario: Usuario)
}
