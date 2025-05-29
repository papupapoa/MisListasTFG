package com.example.mislistas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.mislistas.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#1E1E1E") // RAL 7021 aproximado

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        usuarioDao = db.usuarioDao()

        // Mantener sesión iniciada: si hay idUsuario en prefs, ir directo a Home
        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        if (idUsuario != -1) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {
            val input = binding.etUsuario.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (input.isNotEmpty() && password.isNotEmpty()) {
                login(input, password)
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnIrARegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun login(input: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioDao.obtenerPorEmailONombre(input)
            withContext(Dispatchers.Main) {
                if (usuario != null && usuario.contraseña == password) {
                    // Guardar idUsuario en SharedPreferences
                    val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
                    prefs.edit().putInt("idUsuario", usuario.id).apply()
                    Toast.makeText(this@LoginActivity, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("nombre_usuario", usuario.nombre)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
