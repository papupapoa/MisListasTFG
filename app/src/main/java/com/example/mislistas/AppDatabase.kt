package com.example.mislistas

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Usuario::class,
        Pelicula::class,
        Serie::class,
        Libro::class,
        ListaPeliculas::class,
        ListaSeries::class,
        ListaLibros::class,
        ListaPeliculasContenido::class,
        ListaSeriesContenido::class,
        ListaLibrosContenido::class,
        Amistad::class
    ],
    version = 5 // Subido para forzar migración y evitar error de integridad
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun peliculaDao(): PeliculaDao
    abstract fun listaPeliculasContenidoDao(): ListaPeliculasContenidoDao
    abstract fun serieDao(): SerieDao
    abstract fun listaSeriesContenidoDao(): ListaSeriesContenidoDao
    abstract fun libroDao(): LibroDao
    abstract fun listaPeliculasDao(): ListaPeliculasDao
    abstract fun listaSeriesDao(): ListaSeriesDao
    abstract fun listaLibrosDao(): ListaLibrosDao
    abstract fun listaLibrosContenidoDao(): ListaLibrosContenidoDao
    abstract fun amistadDao(): AmistadDao

    // Si tienes datos previos, deberás crear una migración de Room para no perderlos.
}

// Recuerda: Debes usar .fallbackToDestructiveMigration() en cada Room.databaseBuilder en todas las Activities donde se cree la base de datos.
// Ejemplo:
// Room.databaseBuilder(context, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration().build()
