package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mislistas.adapter.PeliculaSeleccionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CrearListaPeliculasActivity : AppCompatActivity() {
    private lateinit var adapter: PeliculaSeleccionAdapter
    private var peliculasSeleccionadas = mutableListOf<Pelicula>()
    private var todasPeliculas = listOf<Pelicula>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_lista_peliculas)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()

        val rvPeliculas = findViewById<RecyclerView>(R.id.rvPeliculas)
        rvPeliculas.layoutManager = LinearLayoutManager(this)
        adapter = PeliculaSeleccionAdapter(emptyList(), peliculasSeleccionadas) { pelicula, checked ->
            if (checked) {
                if (!peliculasSeleccionadas.contains(pelicula)) peliculasSeleccionadas.add(pelicula)
            } else {
                peliculasSeleccionadas.remove(pelicula)
            }
            actualizarSeleccionadas()
        }
        rvPeliculas.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val lista = db.peliculaDao().getAll()
            runOnUiThread {
                todasPeliculas = lista
                adapter.updatePeliculas(lista)
            }
        }

        val etBuscar = findViewById<EditText>(R.id.etBuscarPelicula)
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtradas = todasPeliculas.filter { it.nombre.contains(s.toString(), ignoreCase = true) }
                adapter.updatePeliculas(filtradas)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<Button>(R.id.btnGuardarLista).setOnClickListener {
            guardarLista()
        }

        // Si recibimos un idListaPeliculas, cargamos la lista y su contenido para edición
        val idLista = intent.getIntExtra("idListaPeliculas", -1)
        if (idLista != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val dbRoom = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
                val lista = dbRoom.listaPeliculasDao().obtenerPorId(idLista)
                val contenidos = dbRoom.listaPeliculasContenidoDao().obtenerPorLista(idLista)
                val todas = dbRoom.peliculaDao().getAll()
                val peliculas = todas.filter { p -> contenidos.any { it.id_pelicula == p.id } }
                runOnUiThread {
                    findViewById<EditText>(R.id.etNombreLista).setText(lista?.nombre ?: "")
                    peliculasSeleccionadas.clear()
                    peliculasSeleccionadas.addAll(peliculas)
                    adapter.updatePeliculas(todas)
                    actualizarSeleccionadas()
                }
            }
        }

        // El layout ya no tiene btnPerfil, así que eliminamos la referencia para evitar el crash.
        val logo = findViewById<ImageButton>(R.id.logoCrearListaPeliculas)
        logo.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun actualizarSeleccionadas() {
        val layout = findViewById<LinearLayout>(R.id.layoutSeleccionadas)
        layout.removeAllViews()
        peliculasSeleccionadas.forEach { pelicula ->
            val view = layoutInflater.inflate(R.layout.item_pelicula_seleccionada, layout, false)
            view.findViewById<TextView>(R.id.tvNombrePelicula).text = pelicula.nombre
            view.findViewById<ImageButton>(R.id.btnQuitarPelicula).setOnClickListener {
                peliculasSeleccionadas.remove(pelicula)
                adapter.notifyDataSetChanged()
                actualizarSeleccionadas()
            }
            layout.addView(view)
        }
    }

    fun guardarLista(view: View? = null) {
        val nombreLista = findViewById<EditText>(R.id.etNombreLista).text.toString().trim()
        if (nombreLista.isEmpty()) {
            Toast.makeText(this, "Ponle un nombre a la lista", Toast.LENGTH_SHORT).show()
            return
        }
        if (peliculasSeleccionadas.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos una película", Toast.LENGTH_SHORT).show()
            return
        }
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val dbRoom = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
        val idLista = intent.getIntExtra("idListaPeliculas", -1)
        CoroutineScope(Dispatchers.IO).launch {
            val idUsuario = prefs.getInt("idUsuario", -1)
            if (idUsuario == -1) return@launch
            if (idLista != -1) {
                val lista = dbRoom.listaPeliculasDao().obtenerPorId(idLista)
                if (lista != null) {
                    val listaActualizada = lista.copy(nombre = nombreLista)
                    dbRoom.listaPeliculasDao().actualizar(listaActualizada)
                    dbRoom.listaPeliculasContenidoDao().borrarPorLista(idLista)
                    peliculasSeleccionadas.forEach {
                        dbRoom.listaPeliculasContenidoDao().insertar(
                            ListaPeliculasContenido(id_lista_pelicula = idLista, id_pelicula = it.id, valoracion = null)
                        )
                    }
                    dbRoom.peliculaDao().actualizarPopularidadTodas()
                    runOnUiThread {
                        Toast.makeText(this@CrearListaPeliculasActivity, "Lista actualizada correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CrearListaPeliculasActivity, BibliotecaActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val idListaNueva = dbRoom.listaPeliculasDao().insertar(
                    ListaPeliculas(nombre = nombreLista, id_usuario = idUsuario, valoracion = null)
                ).toInt()
                peliculasSeleccionadas.forEach {
                    dbRoom.listaPeliculasContenidoDao().insertar(
                        ListaPeliculasContenido(id_lista_pelicula = idListaNueva, id_pelicula = it.id, valoracion = null)
                    )
                }
                dbRoom.peliculaDao().actualizarPopularidadTodas()
                runOnUiThread {
                    Toast.makeText(this@CrearListaPeliculasActivity, "Lista creada correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CrearListaPeliculasActivity, BibliotecaActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Eliminado btnPerfil porque no existe en el layout
    }
}
