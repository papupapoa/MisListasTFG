package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.adapter.PeliculasPopularesAdapter
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import android.widget.LinearLayout

class PeliculasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peliculas)
        window.statusBarColor = Color.parseColor("#2E3234")

        val btnCrearLista = findViewById<Button>(R.id.btnCrearListaPeliculas)
        btnCrearLista.setOnClickListener {
            val intent = Intent(this, CrearListaPeliculasActivity::class.java)
            startActivity(intent)
        }

        val rvPopulares = findViewById<RecyclerView>(R.id.rvPeliculasPopulares)
        rvPopulares.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            db.peliculaDao().actualizarPopularidadTodas()
            val populares = db.peliculaDao().obtenerTop5Populares()
            runOnUiThread {
                rvPopulares.adapter = PeliculasPopularesAdapter(populares)
            }
        }

        val logoPeliculas = findViewById<android.widget.ImageButton>(R.id.logoPeliculas)
        logoPeliculas.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // El layout ya no tiene btnPerfil, así que eliminamos la referencia y el código asociado
        // Si se requiere acceso al perfil, se debe hacer desde el Home o desde el menú principal
    }

    private fun actualizarPopularidadPeliculas(db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            db.peliculaDao().actualizarPopularidadTodas()
        }
    }
}
