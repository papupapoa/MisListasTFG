package com.example.mislistas

import android.app.Activity
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
import com.example.mislistas.adapter.LibroSeleccionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CrearListaLibrosActivity : AppCompatActivity() {
    private lateinit var adapter: LibroSeleccionAdapter
    private var librosSeleccionados = mutableListOf<Libro>()
    private var todosLibros = listOf<Libro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_lista_libros)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()

        val rvLibros = findViewById<RecyclerView>(R.id.rvLibros)
        rvLibros.layoutManager = LinearLayoutManager(this)
        adapter = LibroSeleccionAdapter(emptyList(), librosSeleccionados) { libro, checked ->
            if (checked) {
                if (!librosSeleccionados.contains(libro)) librosSeleccionados.add(libro)
            } else {
                librosSeleccionados.remove(libro)
            }
            actualizarSeleccionados()
        }
        rvLibros.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val lista = db.libroDao().getAll()
            runOnUiThread {
                todosLibros = lista
                adapter.updateLibros(lista)
            }
        }

        val etBuscar = findViewById<EditText>(R.id.etBuscarLibro)
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtrados = todosLibros.filter { it.nombre.contains(s.toString(), ignoreCase = true) }
                adapter.updateLibros(filtrados)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<Button>(R.id.btnGuardarLista).setOnClickListener {
            guardarLista()
        }

        // Si recibimos un idListaLibros, cargamos la lista y su contenido para edición
        val idLista = intent.getIntExtra("idListaLibros", -1)
        if (idLista != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val dbRoom = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
                val lista = dbRoom.listaLibrosDao().obtenerPorId(idLista)
                val contenidos = dbRoom.listaLibrosContenidoDao().obtenerPorLista(idLista)
                val todos = dbRoom.libroDao().getAll()
                val libros = todos.filter { l -> contenidos.any { it.id_libro == l.id } }
                runOnUiThread {
                    findViewById<EditText>(R.id.etNombreLista).setText(lista?.nombre ?: "")
                    librosSeleccionados.clear()
                    librosSeleccionados.addAll(libros)
                    adapter.updateLibros(todos)
                    actualizarSeleccionados()
                }
            }
        }

        val logo = findViewById<ImageButton>(R.id.logoCrearListaLibros)
        logo.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    fun guardarLista(view: View? = null) {
        val nombreLista = findViewById<EditText>(R.id.etNombreLista).text.toString().trim()
        if (nombreLista.isEmpty()) {
            Toast.makeText(this, "Ponle un nombre a la lista", Toast.LENGTH_SHORT).show()
            return
        }
        if (librosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un libro", Toast.LENGTH_SHORT).show()
            return
        }
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val dbRoom = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
        val idLista = intent.getIntExtra("idListaLibros", -1)
        CoroutineScope(Dispatchers.IO).launch {
            val idUsuario = prefs.getInt("idUsuario", -1)
            if (idUsuario == -1) return@launch
            if (idLista != -1) {
                val lista = dbRoom.listaLibrosDao().obtenerPorId(idLista)
                if (lista != null) {
                    val listaActualizada = lista.copy(nombre = nombreLista)
                    dbRoom.listaLibrosDao().actualizar(listaActualizada)
                    dbRoom.listaLibrosContenidoDao().borrarPorLista(idLista)
                    librosSeleccionados.forEach {
                        dbRoom.listaLibrosContenidoDao().insertar(
                            ListaLibrosContenido(id_lista_libro = idLista, id_libro = it.id, valoracion = null)
                        )
                    }
                    dbRoom.libroDao().actualizarPopularidadTodas()
                    runOnUiThread {
                        Toast.makeText(this@CrearListaLibrosActivity, "Lista actualizada correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CrearListaLibrosActivity, BibliotecaActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val idListaNueva = dbRoom.listaLibrosDao().insertar(
                    ListaLibros(nombre = nombreLista, id_usuario = idUsuario, valoracion = null)
                ).toInt()
                librosSeleccionados.forEach {
                    dbRoom.listaLibrosContenidoDao().insertar(
                        ListaLibrosContenido(id_lista_libro = idListaNueva, id_libro = it.id, valoracion = null)
                    )
                }
                dbRoom.libroDao().actualizarPopularidadTodas()
                runOnUiThread {
                    Toast.makeText(this@CrearListaLibrosActivity, "Lista creada correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CrearListaLibrosActivity, BibliotecaActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun actualizarSeleccionados() {
        val layout = findViewById<LinearLayout>(R.id.layoutSeleccionados)
        layout.removeAllViews()
        librosSeleccionados.forEach { libro ->
            val view = layoutInflater.inflate(R.layout.item_libro_seleccionado, layout, false)
            view.findViewById<TextView>(R.id.tvNombreLibro).text = libro.nombre
            view.findViewById<ImageButton>(R.id.btnQuitarLibro).setOnClickListener {
                librosSeleccionados.remove(libro)
                adapter.notifyDataSetChanged()
                actualizarSeleccionados()
            }
            layout.addView(view)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
