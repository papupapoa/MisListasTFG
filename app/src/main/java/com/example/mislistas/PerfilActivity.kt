package com.example.mislistas

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class PerfilActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var db: AppDatabase
    private lateinit var usuarioDao: UsuarioDao
    private var usuarioActual: Usuario? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        window.statusBarColor = Color.parseColor("#2E3234")

        val ivFotoPerfil = findViewById<ImageView>(R.id.ivFotoPerfil)
        // Cargar imagen guardada
        val prefs = getSharedPreferences("perfil_prefs", Context.MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
        val savedPath = prefs.getString(profileKey, null)
        if (!savedPath.isNullOrEmpty()) {
            val file = File(savedPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                ivFotoPerfil.setImageBitmap(bitmap)
            }
        }

        // Obtener nombre de usuario actual por intent
        val nombreUsuario = intent.getStringExtra("nombre_usuario")
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "mislistas-db")
            .fallbackToDestructiveMigration(true)
            .build()
        usuarioDao = db.usuarioDao()
        if (nombreUsuario != null) {
            CoroutineScope(Dispatchers.IO).launch {
                usuarioActual = usuarioDao.obtenerPorEmailONombre(nombreUsuario)
            }
        }

        val btnCambiarFoto = findViewById<Button>(R.id.btnCambiarFoto)
        btnCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        // Los otros botones se configuran después
        findViewById<Button>(R.id.btnCambiarNombre).setOnClickListener {
            mostrarDialogoCambio("nombre")
        }
        findViewById<Button>(R.id.btnCambiarCorreo).setOnClickListener {
            mostrarDialogoCambio("email")
        }
        findViewById<Button>(R.id.btnCambiarPassword).setOnClickListener {
            mostrarDialogoCambio("contraseña")
        }
        // Botón cerrar sesión
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            val prefsLogout = getSharedPreferences("perfil_prefs", Context.MODE_PRIVATE)
            prefsLogout.edit().remove("idUsuario").apply() // Solo borra el usuario logueado, no las fotos
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val logoPerfil = findViewById<ImageView>(R.id.logoPerfil)
        logoPerfil.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                // Corregir rotación
                val rotatedBitmap = imageUri.let { uri ->
                    val path = getRealPathFromURI(uri)
                    if (path != null) {
                        val exif = ExifInterface(path)
                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                        when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                            else -> bitmap
                        }
                    } else bitmap
                }
                // Actualizar la imagen en pantalla inmediatamente
                val ivFotoPerfil = findViewById<ImageView>(R.id.ivFotoPerfil)
                ivFotoPerfil.setImageBitmap(rotatedBitmap)
                // Guardar la imagen
                imagePath = saveImageToInternalStorage(rotatedBitmap)
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path
    }

    private fun rotateBitmap(bitmap: android.graphics.Bitmap, degrees: Float): android.graphics.Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees)
        return android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveImageToInternalStorage(bitmap: android.graphics.Bitmap): String? {
        val filename = "perfil_${System.currentTimeMillis()}.png"
        val file = File(filesDir, filename)
        try {
            val fos = openFileOutput(filename, Context.MODE_PRIVATE)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            // Guardar ruta en SharedPreferences
            val prefs = getSharedPreferences("perfil_prefs", Context.MODE_PRIVATE)
            val idUsuario = prefs.getInt("idUsuario", -1)
            val profileKey = if (idUsuario != -1) "profile_image_path_$idUsuario" else "profile_image_path"
            prefs.edit().putString(profileKey, file.absolutePath).apply()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun mostrarDialogoCambio(tipo: String) {
        val editText = EditText(this)
        val titulo = when (tipo) {
            "nombre" -> "Nuevo nombre de usuario"
            "email" -> "Nuevo correo"
            "contraseña" -> "Nueva contraseña"
            else -> ""
        }
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoValor = editText.text.toString().trim()
                if (nuevoValor.isNotEmpty()) {
                    actualizarUsuarioEnBD(tipo, nuevoValor)
                } else {
                    Toast.makeText(this, "No puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarUsuarioEnBD(tipo: String, nuevoValor: String) {
        CoroutineScope(Dispatchers.IO).launch {
            usuarioActual?.let { usuario ->
                val usuarioActualizado = when (tipo) {
                    "nombre" -> usuario.copy(nombre = nuevoValor)
                    "email" -> usuario.copy(email = nuevoValor)
                    "contraseña" -> usuario.copy(contraseña = nuevoValor)
                    else -> usuario
                }
                db.usuarioDao().actualizar(usuarioActualizado)
                usuarioActual = usuarioActualizado
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                    if (tipo == "nombre") {
                        val resultIntent = Intent()
                        resultIntent.putExtra("nuevo_nombre_usuario", nuevoValor)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("profile_image_path", imagePath)
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}
