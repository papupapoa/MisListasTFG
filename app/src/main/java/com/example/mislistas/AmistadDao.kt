package com.example.mislistas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AmistadDao {
    @Insert
    suspend fun insertar(amistad: Amistad): Long

    @Query("SELECT Usuarios.* FROM Usuarios INNER JOIN Amistades ON Usuarios.id = Amistades.id_amigo WHERE Amistades.id_usuario = :idUsuario")
    suspend fun obtenerAmigos(idUsuario: Int): List<Usuario>

    @Query("SELECT * FROM Amistades WHERE id_usuario = :idUsuario AND id_amigo = :idAmigo LIMIT 1")
    suspend fun obtenerAmistad(idUsuario: Int, idAmigo: Int): Amistad?

    @Query("DELETE FROM Amistades WHERE id_usuario = :idUsuario AND id_amigo = :idAmigo")
    suspend fun borrarAmistad(idUsuario: Int, idAmigo: Int)
}
