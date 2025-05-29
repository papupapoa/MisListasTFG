package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mislistas.adapter.ListasPeliculasAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ListasPeliculasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listas_peliculas)

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
        btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        val rvListasPeliculas = findViewById<RecyclerView>(R.id.rvListasPeliculas)
        rvListasPeliculas.layoutManager = LinearLayoutManager(this)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        val prefsUser = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val userId = prefsUser.getInt("idUsuario", -1)
        CoroutineScope(Dispatchers.IO).launch {
            val listasPeliculas = db.listaPeliculasDao().obtenerPorUsuario(userId)
            runOnUiThread {
                rvListasPeliculas.adapter = com.example.mislistas.adapter.ListasPeliculasAdapter(listasPeliculas) { lista ->
                    val intent = Intent(this@ListasPeliculasActivity, CrearListaPeliculasActivity::class.java)
                    intent.putExtra("idListaPeliculas", lista.id)
                    startActivity(intent)
                }
            }
        }
    }
}
