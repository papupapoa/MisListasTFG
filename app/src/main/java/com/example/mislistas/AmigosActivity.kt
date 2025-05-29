package com.example.mislistas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AmigosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amigos)

        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerAmigos)
        val btnAgregar = findViewById<android.widget.Button>(R.id.btnAgregarAmigo)
        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        val prefs = getSharedPreferences("perfil_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val db = androidx.room.Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db").fallbackToDestructiveMigration(true).build()

        val adapter = AmigosAdapter(listOf(),
            onClick = { amigo ->
                val intent = Intent(this, PerfilAmigoActivity::class.java)
                intent.putExtra("idAmigo", amigo.id)
                intent.putExtra("nombre", amigo.nombre)
                startActivity(intent)
            },
            onDelete = { amigo ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.amistadDao().borrarAmistad(idUsuario, amigo.id.toInt())
                    val amigos = db.amistadDao().obtenerAmigos(idUsuario)
                    val items = amigos.map { AmigoItem(it.id.toLong(), it.nombre) }
                    withContext(Dispatchers.Main) {
                        recycler.adapter = recycler.adapter // No-op, just to avoid unresolved reference error
                        (recycler.adapter as? AmigosAdapter)?.updateList(items)
                        android.widget.Toast.makeText(this@AmigosActivity, "Amigo eliminado", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        recycler.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val amigos = db.amistadDao().obtenerAmigos(idUsuario)
            val items = amigos.map { AmigoItem(it.id.toLong(), it.nombre, it.email) }
            withContext(Dispatchers.Main) {
                adapter.updateList(items)
            }
        }

        btnAgregar.setOnClickListener {
            val input = android.widget.EditText(this)
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("Agregar amigo")
                .setMessage("Introduce el nombre exacto del usuario a agregar:")
                .setView(input)
                .setPositiveButton("Buscar y agregar") { _, _ ->
                    val nombreBuscado = input.text.toString().trim()
                    if (nombreBuscado.isEmpty()) {
                        android.widget.Toast.makeText(this, "Introduce un nombre", android.widget.Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        val usuarioBuscado = db.usuarioDao().obtenerPorNombre(nombreBuscado)
                        if (usuarioBuscado == null) {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(this@AmigosActivity, "Usuario no encontrado", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else if (usuarioBuscado.id == idUsuario) {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(this@AmigosActivity, "No puedes agregarte a ti mismo", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val yaAmigo = db.amistadDao().obtenerAmistad(idUsuario, usuarioBuscado.id)
                            if (yaAmigo != null) {
                                withContext(Dispatchers.Main) {
                                    android.widget.Toast.makeText(this@AmigosActivity, "Ya es tu amigo", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                db.amistadDao().insertar(Amistad(id_usuario = idUsuario, id_amigo = usuarioBuscado.id))
                                val amigos = db.amistadDao().obtenerAmigos(idUsuario)
                                val items = amigos.map { AmigoItem(it.id.toLong(), it.nombre, it.email) }
                                withContext(Dispatchers.Main) {
                                    adapter.updateList(items)
                                    android.widget.Toast.makeText(this@AmigosActivity, "Amigo agregado", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.show()
        }

        val logoAmigos = findViewById<android.widget.ImageButton>(R.id.logoAmigos)
        logoAmigos.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
