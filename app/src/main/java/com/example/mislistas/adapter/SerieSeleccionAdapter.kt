package com.example.mislistas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mislistas.Serie
import com.example.mislistas.R

class SerieSeleccionAdapter(
    private var series: List<Serie>,
    private val seleccionadas: List<Serie>,
    private val onCheckedChange: (Serie, Boolean) -> Unit
) : RecyclerView.Adapter<SerieSeleccionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_serie_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = series.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serie = series[position]
        holder.tvNombre.text = serie.nombre
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = seleccionadas.contains(serie)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(serie, isChecked)
        }
    }

    fun updateSeries(nuevas: List<Serie>) {
        series = nuevas
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreSerie)
        val checkBox: CheckBox = view.findViewById(R.id.cbSeleccionar)
    }
}
