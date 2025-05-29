package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mislistas.adapter.ListasSeriesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ListasSeriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listas_series)

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

        val rvListasSeries = findViewById<RecyclerView>(R.id.rvListasSeries)
        rvListasSeries.layoutManager = LinearLayoutManager(this)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        val prefsUser = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val userId = prefsUser.getInt("idUsuario", -1)
        CoroutineScope(Dispatchers.IO).launch {
            val listasSeries = db.listaSeriesDao().obtenerPorUsuario(userId)
            runOnUiThread {
                rvListasSeries.adapter = ListasSeriesAdapter(listasSeries) { lista ->
                    val intent = Intent(this@ListasSeriesActivity, CrearListaSeriesActivity::class.java)
                    intent.putExtra("idListaSeries", lista.id)
                    startActivity(intent)
                }
            }
        }
    }
}
