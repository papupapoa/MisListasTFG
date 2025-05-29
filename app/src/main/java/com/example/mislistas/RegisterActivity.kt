package com.example.mislistas

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.mislistas.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#1E1E1E")

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        usuarioDao = db.usuarioDao()

        binding.btnRegistrarse.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!email.matches(emailRegex)) {
                Toast.makeText(this, "Introduce un email válido (formato x@x.com)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nombre.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registrar(nombre, email, password)
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrar(nombre: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val usuarioPorNombre = usuarioDao.obtenerPorNombre(nombre)
            if (usuarioPorNombre != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Ese nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            val usuarioPorEmail = usuarioDao.obtenerPorEmail(email)
            if (usuarioPorEmail != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Ese correo ya está en uso", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            val nuevoUsuario = Usuario(
                nombre = nombre,
                email = email,
                contraseña = password,
                fecha_registro = System.currentTimeMillis()
            )
            usuarioDao.insertar(nuevoUsuario)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                finish() // Vuelve a LoginActivity
            }
        }
    }
}
