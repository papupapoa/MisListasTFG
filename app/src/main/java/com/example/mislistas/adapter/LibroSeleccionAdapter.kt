package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.Libro
import com.example.mislistas.R

class LibroSeleccionAdapter(
    private var libros: List<Libro>,
    private val seleccionados: List<Libro>,
    private val onCheckedChange: (Libro, Boolean) -> Unit
) : RecyclerView.Adapter<LibroSeleccionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = libros.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val libro = libros[position]
        holder.tvNombre.text = libro.nombre
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = seleccionados.contains(libro)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(libro, isChecked)
        }
    }

    fun updateLibros(nuevos: List<Libro>) {
        libros = nuevos
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreLibro)
        val checkBox: CheckBox = view.findViewById(R.id.cbSeleccionar)
    }
}
