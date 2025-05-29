package com.example.mislistas

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.min

class HomeActivity : AppCompatActivity() {
    private val REQUEST_PERFIL = 100
    private var profileImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        profileImagePath = null
        window.statusBarColor = Color.parseColor("#2E3234")

        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        if (idUsuario != -1) {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
                .fallbackToDestructiveMigration(true)
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                val usuario = db.usuarioDao().obtenerPorId(idUsuario)
                val nombreUsuario = usuario?.nombre ?: "Usuario"
                runOnUiThread {
                    tvBienvenida.text = "¡Bienvenido, $nombreUsuario!"
                }
            }
        } else {
            tvBienvenida.text = "¡Bienvenido, Usuario!"
        }

        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivityForResult(intent, REQUEST_PERFIL)
        }
        val btnAmigos = findViewById<ImageButton>(R.id.btnAmigos)
        btnAmigos.setOnClickListener {
            val intent = Intent(this, AmigosActivity::class.java)
            startActivity(intent)
        }
        // Cargar imagen de perfil persistente
        if (profileImagePath == null) {
            val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
            val idUsuario = prefs.getInt("idUsuario", -1)
            val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
            val savedPath = prefs.getString(profileKey, null)
            if (!savedPath.isNullOrEmpty()) {
                profileImagePath = savedPath
                setProfileImage(btnPerfil, savedPath)
            }
        } else {
            setProfileImage(btnPerfil, profileImagePath!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERFIL && resultCode == RESULT_OK && data != null) {
            val nuevoNombre = data.getStringExtra("nuevo_nombre_usuario")
            if (!nuevoNombre.isNullOrEmpty()) {
                val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
                tvBienvenida.text = "¡Bienvenido, $nuevoNombre!"
            }
            val path = data.getStringExtra("profile_image_path")
            if (!path.isNullOrEmpty()) {
                profileImagePath = path
                val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
                val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
                val idUsuario = prefs.getInt("idUsuario", -1)
                val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
                prefs.edit().putString(profileKey, path).apply()
                setProfileImage(btnPerfil, path)
            }
        }
    }

    private fun setProfileImage(button: ImageButton, imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val circularBitmap = getCircularBitmap(bitmap)
            button.setImageBitmap(circularBitmap)
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()
        path.addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CCW)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, (size - bitmap.width) / 2f, (size - bitmap.height) / 2f, paint)
        return output
    }

    fun irAPeliculas(view: android.view.View) {
        val intent = Intent(this, PeliculasActivity::class.java)
        startActivity(intent)
    }

    fun irASeries(view: android.view.View) {
        val intent = Intent(this, SeriesActivity::class.java)
        startActivity(intent)
    }

    fun irALibros(view: android.view.View) {
        val intent = Intent(this, LibrosActivity::class.java)
        startActivity(intent)
    }

    fun irABiblioteca(view: android.view.View) {
        val intent = Intent(this, BibliotecaActivity::class.java)
        startActivity(intent)
    }
}
