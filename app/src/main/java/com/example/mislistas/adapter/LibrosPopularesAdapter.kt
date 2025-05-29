package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.R
import com.example.mislistas.Libro

class LibrosPopularesAdapter(private val libros: List<Libro>) : RecyclerView.Adapter<LibrosPopularesAdapter.LibroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro_popular, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        holder.bind(libros[position])
    }

    override fun getItemCount() = libros.size

    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre = itemView.findViewById<TextView>(R.id.tvNombreLibro)
        private val tvPopularidad = itemView.findViewById<TextView>(R.id.tvPopularidadLibro)
        private val ivIcono = itemView.findViewById<ImageView>(R.id.ivIconoLibro)
        fun bind(libro: Libro) {
            tvNombre.text = libro.nombre
            tvPopularidad.text = "Popularidad: ${libro.popularidad}"
            ivIcono.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
