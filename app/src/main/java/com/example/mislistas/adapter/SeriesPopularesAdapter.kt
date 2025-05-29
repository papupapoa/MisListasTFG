package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.R
import com.example.mislistas.Serie

class SeriesPopularesAdapter(private val series: List<Serie>) : RecyclerView.Adapter<SeriesPopularesAdapter.SerieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_serie_popular, parent, false)
        return SerieViewHolder(view)
    }

    override fun onBindViewHolder(holder: SerieViewHolder, position: Int) {
        holder.bind(series[position])
    }

    override fun getItemCount() = series.size

    class SerieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre = itemView.findViewById<TextView>(R.id.tvNombreSerie)
        private val tvPopularidad = itemView.findViewById<TextView>(R.id.tvPopularidadSerie)
        private val ivIcono = itemView.findViewById<ImageView>(R.id.ivIconoSerie)
        fun bind(serie: Serie) {
            tvNombre.text = serie.nombre
            tvPopularidad.text = "Popularidad: ${serie.popularidad}"
            ivIcono.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
