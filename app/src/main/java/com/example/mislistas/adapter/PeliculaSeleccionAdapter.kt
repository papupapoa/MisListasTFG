package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.Pelicula
import com.example.mislistas.R

class PeliculaSeleccionAdapter(
    private var peliculas: List<Pelicula>,
    private val seleccionadas: List<Pelicula>,
    private val onCheckedChange: (Pelicula, Boolean) -> Unit
) : RecyclerView.Adapter<PeliculaSeleccionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pelicula_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = peliculas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelicula = peliculas[position]
        holder.tvNombre.text = pelicula.nombre
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = seleccionadas.contains(pelicula)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(pelicula, isChecked)
        }
    }

    fun updatePeliculas(nuevas: List<Pelicula>) {
        peliculas = nuevas
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombrePelicula)
        val checkBox: CheckBox = view.findViewById(R.id.cbSeleccionar)
    }
}
