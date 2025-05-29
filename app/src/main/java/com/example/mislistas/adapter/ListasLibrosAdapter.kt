package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.ListaLibros
import com.example.mislistas.R

class ListasLibrosAdapter(
    private val listas: List<ListaLibros>,
    private val onItemClick: (ListaLibros) -> Unit
) : RecyclerView.Adapter<ListasLibrosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_libros, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lista = listas[position]
        holder.tvNombreListaLibro.text = lista.nombre
        holder.itemView.setOnClickListener { onItemClick(lista) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreListaLibro: TextView = view.findViewById(R.id.tvNombreListaLibro)
    }
}
