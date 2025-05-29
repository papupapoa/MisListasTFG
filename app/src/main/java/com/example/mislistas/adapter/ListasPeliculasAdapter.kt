package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.ListaPeliculas
import com.example.mislistas.R

class ListasPeliculasAdapter(
    private val listas: List<ListaPeliculas>,
    private val onItemClick: (ListaPeliculas) -> Unit
) : RecyclerView.Adapter<ListasPeliculasAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_peliculas, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lista = listas[position]
        holder.tvNombreLista.text = lista.nombre
        holder.itemView.setOnClickListener { onItemClick(lista) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreLista: TextView = view.findViewById(R.id.tvNombreLista)
    }
}
