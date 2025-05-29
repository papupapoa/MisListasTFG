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

        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val savedPath = prefs.getString("profile_image_path", null)
        if (!savedPath.isNullOrEmpty()) {
            val file = java.io.File(savedPath)
            if (file.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(savedPath)
                btnPerfil.setImageBitmap(bitmap)
            }
        }
        btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
        val savedPath = prefs.getString(profileKey, null)
        if (!savedPath.isNullOrEmpty()) {
            val file = java.io.File(savedPath)
            if (file.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(savedPath)
                val size = kotlin.math.min(bitmap.width, bitmap.height)
                val output = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(output)
                val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
                val path = android.graphics.Path()
                path.addCircle(size / 2f, size / 2f, size / 2f, android.graphics.Path.Direction.CCW)
                canvas.clipPath(path)
                canvas.drawBitmap(bitmap, (size - bitmap.width) / 2f, (size - bitmap.height) / 2f, paint)
                btnPerfil.setImageBitmap(output)
            }
        }
    }
}
