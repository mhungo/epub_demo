package epub_core.ui.adapter

import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {
    fun onItemClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder, position: Int, id: Long
    )
}