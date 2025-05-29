package com.example.mislistas

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContenidoListaAmigoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contenido_lista_amigo)

        val tvTitulo = findViewById<TextView>(R.id.tvTituloContenidoListaAmigo)
        val rvContenido = findViewById<RecyclerView>(R.id.rvContenidoListaAmigo)
        rvContenido.layoutManager = LinearLayoutManager(this)

        val tipo = intent.getStringExtra("tipo") ?: ""
        val idLista = intent.getIntExtra("idLista", -1)
        tvTitulo.text = "Contenido de la lista"
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
        CoroutineScope(Dispatchers.IO).launch {
            val items = when (tipo) {
                "pelis" -> {
                    val contenidos = db.listaPeliculasContenidoDao().obtenerPorLista(idLista)
                    val peliculas = db.peliculaDao().getAll().associateBy { it.id }
                    contenidos.mapNotNull { peliculas[it.id_pelicula]?.nombre }
                }
                "series" -> {
                    val contenidos = db.listaSeriesContenidoDao().obtenerPorLista(idLista)
                    val series = db.serieDao().getAll().associateBy { it.id }
                    contenidos.mapNotNull { series[it.id_serie]?.nombre }
                }
                "libros" -> {
                    val contenidos = db.listaLibrosContenidoDao().obtenerPorLista(idLista)
                    val libros = db.libroDao().getAll().associateBy { it.id }
                    contenidos.mapNotNull { libros[it.id_libro]?.nombre }
                }
                else -> emptyList()
            }
            withContext(Dispatchers.Main) {
                rvContenido.adapter = SimpleListAdapter(items)
            }
        }
    }
}
