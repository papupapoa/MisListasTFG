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
import com.example.mislistas.adapter.SeriesPopularesAdapter
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SeriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)
        window.statusBarColor = Color.parseColor("#2E3234")

        val logoSeries = findViewById<ImageButton>(R.id.logoSeries)
        logoSeries.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val rvPopulares = findViewById<RecyclerView>(R.id.rvSeriesPopulares)
        rvPopulares.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            db.serieDao().actualizarPopularidadTodas()
            val populares = db.serieDao().obtenerTop5Populares()
            runOnUiThread {
                rvPopulares.adapter = SeriesPopularesAdapter(populares)
            }
        }

        val btnCrearLista = findViewById<Button>(R.id.btnCrearListaSeries)
        btnCrearLista.setOnClickListener {
            val intent = Intent(this, CrearListaSeriesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
