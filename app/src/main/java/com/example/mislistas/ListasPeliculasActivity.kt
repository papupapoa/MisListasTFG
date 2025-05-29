package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
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
        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
        val savedPath = prefs.getString(profileKey, null)
        if (!savedPath.isNullOrEmpty()) {
            setProfileImage(btnPerfil, savedPath)
        }
        val logoHome = findViewById<ImageView>(R.id.logoHome)
        logoHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
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

    private fun setProfileImage(button: ImageButton, imagePath: String) {
        val file = java.io.File(imagePath)
        if (file.exists()) {
            val bitmap = android.graphics.BitmapFactory.decodeFile(imagePath)
            val circularBitmap = getCircularBitmap(bitmap)
            button.setImageBitmap(circularBitmap)
        }
    }

    private fun getCircularBitmap(bitmap: android.graphics.Bitmap): android.graphics.Bitmap {
        val size = kotlin.math.min(bitmap.width, bitmap.height)
        val output = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        val path = android.graphics.Path()
        path.addCircle(size / 2f, size / 2f, size / 2f, android.graphics.Path.Direction.CCW)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, (size - bitmap.width) / 2f, (size - bitmap.height) / 2f, paint)
        return output
    }

    override fun onResume() {
        super.onResume()
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
        val savedPath = prefs.getString(profileKey, null)
        if (!savedPath.isNullOrEmpty()) {
            setProfileImage(btnPerfil, savedPath)
        }
    }
}
