package com.example.mislistas

import ListasAmigoAdapter
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilAmigoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_amigo)

        val nombre = intent.getStringExtra("nombre") ?: ""
        findViewById<TextView>(R.id.tvNombreAmigoPerfil).text = nombre
        // Mostrar foto de perfil del amigo si existe
        val ivFotoPerfilAmigo = findViewById<ImageView>(R.id.ivFotoPerfilAmigo)
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idAmigo = intent.getLongExtra("idAmigo", -1L).toInt()
        val profileKey = if (idAmigo != -1) "profile_image_path_$idAmigo" else null
        val savedPath = profileKey?.let { prefs.getString(it, null) }
        if (!savedPath.isNullOrEmpty()) {
            val file = File(savedPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                ivFotoPerfilAmigo.setImageBitmap(bitmap)
            }
        }
        // Unificar estilo de botones
        val btnPelis = findViewById<android.widget.Button>(R.id.btnVerPelisAmigo)
        val btnSeries = findViewById<android.widget.Button>(R.id.btnVerSeriesAmigo)
        val btnLibros = findViewById<android.widget.Button>(R.id.btnVerLibrosAmigo)
        val buttonColor = android.graphics.Color.parseColor("#6cf3d5")
        val textColor = android.graphics.Color.parseColor("#000000")
        btnPelis.setBackgroundColor(buttonColor)
        btnPelis.setTextColor(textColor)
        btnSeries.setBackgroundColor(buttonColor)
        btnSeries.setTextColor(textColor)
        btnLibros.setBackgroundColor(buttonColor)
        btnLibros.setTextColor(textColor)

        val rvListas = findViewById<RecyclerView>(R.id.rvListasAmigo)
        rvListas.layoutManager = LinearLayoutManager(this)
        val db = androidx.room.Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
        val adapter = ListasAmigoAdapter(listOf()) { item: String ->
            val (tipo, idLista, nombreLista) = item.split("|", limit = 3)
            val intent = android.content.Intent(this, ContenidoListaAmigoActivity::class.java)
            intent.putExtra("tipo", tipo)
            intent.putExtra("idLista", idLista.toInt())
            intent.putExtra("nombre", nombreLista)
            startActivity(intent)
        }
        rvListas.adapter = adapter
        btnPelis.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val listas = db.listaPeliculasDao().obtenerPorUsuario(idAmigo).map { "pelis|${it.id}|${it.nombre}" to it.nombre }
                withContext(Dispatchers.Main) { adapter.updateList(listas) }
            }
        }
        btnSeries.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val listas = db.listaSeriesDao().obtenerPorUsuario(idAmigo).map { "series|${it.id}|${it.nombre}" to it.nombre }
                withContext(Dispatchers.Main) { adapter.updateList(listas) }
            }
        }
        btnLibros.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val listas = db.listaLibrosDao().obtenerPorUsuario(idAmigo).map { "libros|${it.id}|${it.nombre}" to it.nombre }
                withContext(Dispatchers.Main) { adapter.updateList(listas) }
            }
        }
        // Por defecto mostrar pelis
        btnPelis.performClick()
    }
}
