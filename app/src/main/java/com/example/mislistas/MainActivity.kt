package com.example.mislistas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mostrar splash durante 5 segundos y luego ir a LoginActivity
        android.os.Handler().postDelayed({
            val db = androidx.room.Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
                .fallbackToDestructiveMigration(true)
                .build()
            lifecycleScope.launch {
                if (db.peliculaDao().getAll().isEmpty()) {
                    db.peliculaDao().insertarVarios(
                        Pelicula(nombre = "El Padrino", duracion = 175, genero = "Drama", popularidad = 0),
                        Pelicula(nombre = "Matrix", duracion = 136, genero = "Ciencia Ficción", popularidad = 0)
                    )
                }
                if (db.serieDao().getAll().isEmpty()) {
                    db.serieDao().insertarVarios(
                        Serie(nombre = "Breaking Bad", duracion = 49, genero = "Crimen", popularidad = 0),
                        Serie(nombre = "Stranger Things", duracion = 50, genero = "Ciencia Ficción", popularidad = 0)
                    )
                }
                if (db.libroDao().getAll().isEmpty()) {
                    db.libroDao().insertarVarios(
                        Libro(nombre = "Cien años de soledad", genero = "Realismo mágico", popularidad = 0),
                        Libro(nombre = "1984", genero = "Distopía", popularidad = 0)
                    )
                }
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}
