package com.example.mislistas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AmigosAdapter(
    private var amigos: List<AmigoItem>,
    private val onClick: (AmigoItem) -> Unit,
    private val onDelete: (AmigoItem) -> Unit // Se agrega onDelete
) : RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder>() {

    inner class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreAmigo)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarAmigo)
        init {
            itemView.setOnClickListener {
                val amigo = amigos[adapterPosition]
                onClick(amigo)
            }
            btnEliminar.setOnClickListener {
                val amigo = amigos[adapterPosition]
                onDelete(amigo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amigo, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val amigo = amigos[position]
        holder.tvNombre.text = amigo.nombre
    }

    override fun getItemCount(): Int = amigos.size

    fun updateList(nuevaLista: List<AmigoItem>) {
        amigos = nuevaLista
        notifyDataSetChanged()
    }
}
