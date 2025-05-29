package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.Pelicula
import com.example.mislistas.R

class PeliculasPopularesAdapter(private val peliculas: List<Pelicula>) : RecyclerView.Adapter<PeliculasPopularesAdapter.PeliculaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pelicula_popular, parent, false)
        return PeliculaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        holder.bind(peliculas[position])
    }

    override fun getItemCount() = peliculas.size

    class PeliculaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre = itemView.findViewById<TextView>(R.id.tvNombrePelicula)
        private val tvPopularidad = itemView.findViewById<TextView>(R.id.tvPopularidad)
        private val ivIcono = itemView.findViewById<ImageView>(R.id.ivIconoPelicula)
        fun bind(pelicula: Pelicula) {
            tvNombre.text = pelicula.nombre
            tvPopularidad.text = "Popularidad: ${pelicula.popularidad}"
            ivIcono.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
