package com.example.mislistas

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Amistades",
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["id_usuario"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["id_amigo"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Amistad(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_usuario: Int,
    val id_amigo: Int
)
