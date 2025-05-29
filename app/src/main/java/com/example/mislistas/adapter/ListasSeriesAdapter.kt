package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.ListaSeries
import com.example.mislistas.R

class ListasSeriesAdapter(
    private val listas: List<ListaSeries>,
    private val onItemClick: (ListaSeries) -> Unit
) : RecyclerView.Adapter<ListasSeriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_series, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lista = listas[position]
        holder.tvNombreListaSerie.text = lista.nombre
        holder.itemView.setOnClickListener { onItemClick(lista) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreListaSerie: TextView = view.findViewById(R.id.tvNombreListaSerie)
    }
}
