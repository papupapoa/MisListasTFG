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
import com.example.mislistas.adapter.SerieSeleccionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CrearListaSeriesActivity : AppCompatActivity() {
    private lateinit var adapter: SerieSeleccionAdapter
    private var seriesSeleccionadas = mutableListOf<Serie>()
    private var todasSeries = listOf<Serie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_lista_series)

        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val savedPath = prefs.getString("profile_image_path", null)
        if (!savedPath.isNullOrEmpty()) {
            val file = File(savedPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                //btnPerfil.setImageBitmap(bitmap)
            }
        }

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()

        val rvSeries = findViewById<RecyclerView>(R.id.rvSeries)
        rvSeries.layoutManager = LinearLayoutManager(this)
        adapter = SerieSeleccionAdapter(emptyList(), seriesSeleccionadas) { serie, checked ->
            if (checked) {
                if (!seriesSeleccionadas.contains(serie)) seriesSeleccionadas.add(serie)
            } else {
                seriesSeleccionadas.remove(serie)
            }
            actualizarSeleccionadas()
        }
        rvSeries.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val lista = db.serieDao().getAll()
            runOnUiThread {
                todasSeries = lista
                adapter.updateSeries(lista)
            }
        }

        val etBuscar = findViewById<EditText>(R.id.etBuscarSerie)
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtradas = todasSeries.filter { it.nombre.contains(s.toString(), ignoreCase = true) }
                adapter.updateSeries(filtradas)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<Button>(R.id.btnGuardarLista).setOnClickListener {
            guardarLista()
        }

        // Corrige la obtención y edición de la lista
        val idLista = intent.getIntExtra("idListaSeries", -1)
        if (idLista != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val dbRoom = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
                val lista = dbRoom.listaSeriesDao().obtenerPorId(idLista)
                val contenidos = dbRoom.listaSeriesContenidoDao().obtenerPorLista(idLista)
                val todas = dbRoom.serieDao().getAll()
                val series = todas.filter { s -> contenidos.any { it.id_serie == s.id } }
                runOnUiThread {
                    findViewById<EditText>(R.id.etNombreLista).setText(lista?.nombre ?: "")
                    seriesSeleccionadas.clear()
                    seriesSeleccionadas.addAll(series)
                    adapter.updateSeries(todas)
                    actualizarSeleccionadas()
                }
            }
        }

        // El layout ya no tiene btnPerfil, así que eliminamos la referencia para evitar el crash.
        val logo = findViewById<ImageButton>(R.id.logoCrearListaSeries)
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
        if (seriesSeleccionadas.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos una serie", Toast.LENGTH_SHORT).show()
            return
        }
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val dbRoom = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()
        val idLista = intent.getIntExtra("idListaSeries", -1)
        CoroutineScope(Dispatchers.IO).launch {
            val idUsuario = prefs.getInt("idUsuario", -1)
            if (idUsuario == -1) return@launch
            if (idLista != -1) {
                val lista = dbRoom.listaSeriesDao().obtenerPorId(idLista)
                if (lista != null) {
                    val listaActualizada = lista.copy(nombre = nombreLista)
                    dbRoom.listaSeriesDao().actualizar(listaActualizada)
                    dbRoom.listaSeriesContenidoDao().borrarPorLista(idLista)
                    seriesSeleccionadas.forEach {
                        dbRoom.listaSeriesContenidoDao().insertar(
                            ListaSeriesContenido(id_lista_serie = idLista, id_serie = it.id, valoracion = null)
                        )
                    }
                    dbRoom.serieDao().actualizarPopularidadTodas()
                    runOnUiThread {
                        Toast.makeText(this@CrearListaSeriesActivity, "Lista actualizada correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CrearListaSeriesActivity, BibliotecaActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val idListaNueva = dbRoom.listaSeriesDao().insertar(
                    ListaSeries(nombre = nombreLista, id_usuario = idUsuario, valoracion = null)
                ).toInt()
                seriesSeleccionadas.forEach {
                    dbRoom.listaSeriesContenidoDao().insertar(
                        ListaSeriesContenido(id_lista_serie = idListaNueva, id_serie = it.id, valoracion = null)
                    )
                }
                dbRoom.serieDao().actualizarPopularidadTodas()
                runOnUiThread {
                    Toast.makeText(this@CrearListaSeriesActivity, "Lista creada correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CrearListaSeriesActivity, BibliotecaActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun actualizarSeleccionadas() {
        val layout = findViewById<LinearLayout>(R.id.layoutSeleccionadas)
        layout.removeAllViews()
        seriesSeleccionadas.forEach { serie ->
            val view = layoutInflater.inflate(R.layout.item_serie_seleccionada, layout, false)
            view.findViewById<TextView>(R.id.tvNombreSerie).text = serie.nombre
            view.findViewById<ImageButton>(R.id.btnQuitarSerie).setOnClickListener {
                seriesSeleccionadas.remove(serie)
                adapter.notifyDataSetChanged()
                actualizarSeleccionadas()
            }
            layout.addView(view)
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val savedPath = prefs.getString("profile_image_path", null)
        if (!savedPath.isNullOrEmpty()) {
            val file = File(savedPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                //btnPerfil.setImageBitmap(bitmap)
            }
        }
    }
}
