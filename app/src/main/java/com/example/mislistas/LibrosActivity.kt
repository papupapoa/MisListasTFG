package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.adapter.LibrosPopularesAdapter
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class LibrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libros)
        window.statusBarColor = Color.parseColor("#2E3234")

        val logoLibros = findViewById<ImageButton>(R.id.logoLibros)
        logoLibros.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val rvPopulares = findViewById<RecyclerView>(R.id.rvLibrosPopulares)
        rvPopulares.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            db.libroDao().actualizarPopularidadTodas()
            val populares = db.libroDao().obtenerTop5Populares()
            runOnUiThread {
                rvPopulares.adapter = LibrosPopularesAdapter(populares)
            }
        }

        val btnCrearLista = findViewById<Button>(R.id.btnCrearListaLibros)
        btnCrearLista.setOnClickListener {
            val intent = Intent(this, CrearListaLibrosActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
