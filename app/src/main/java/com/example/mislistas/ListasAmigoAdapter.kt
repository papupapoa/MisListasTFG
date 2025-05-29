import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListasAmigoAdapter(
    private var listas: List<Pair<String, String>>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ListasAmigoAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(android.R.id.text1)
        init {
            itemView.setOnClickListener {
                onClick(listas[adapterPosition].first)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNombre.text = listas[position].second
    }
    override fun getItemCount() = listas.size
    fun updateList(nuevaLista: List<Pair<String, String>>) {
        listas = nuevaLista
        notifyDataSetChanged()
    }
}