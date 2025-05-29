package com.example.mislistas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleListAdapter(private val items: List<String>) : RecyclerView.Adapter<SimpleListAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return ViewHolder(tv)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
    }
    override fun getItemCount() = items.size
}
