package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BibliotecaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca)
        window.statusBarColor = Color.parseColor("#2E3234")

        val logo = findViewById<ImageButton>(R.id.logoBiblioteca)
        logo.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val btnListasPeliculas = findViewById<android.widget.Button>(R.id.btnListasPeliculas)
        val btnListasSeries = findViewById<android.widget.Button>(R.id.btnListasSeries)
        val btnListasLibros = findViewById<android.widget.Button>(R.id.btnListasLibros)

        btnListasPeliculas.setOnClickListener {
            startActivity(Intent(this, ListasPeliculasActivity::class.java))
        }
        btnListasSeries.setOnClickListener {
            startActivity(Intent(this, ListasSeriesActivity::class.java))
        }
        btnListasLibros.setOnClickListener {
            startActivity(Intent(this, ListasLibrosActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val savedPath = prefs.getString("profile_image_path", null)
        if (!savedPath.isNullOrEmpty()) {
            val file = File(savedPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                btnPerfil.setImageBitmap(bitmap)
            }
        }
    }
}
