package com.example.projekconsultant.adapter2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.AdapterAdminLayananRiwayatItem

class AdapterProfileRiwayatKonsultasiKonselor(private val items: List<AdapterAdminLayananRiwayatItem>,
                                              private val listener: (AdapterAdminLayananRiwayatItem) -> Unit // Listener dengan item, bukan adapter
    ) : RecyclerView.Adapter<AdapterProfileRiwayatKonsultasiKonselor.MenuViewHolder>() {

    private var selectedPosition = 0 // Awalnya tidak ada yang dipilih
    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.menuTitle)
        val line: View = view.findViewById(R.id.line)

        init {
            view.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) { // Cek validitas posisi
                    // Simpan posisi sebelumnya
                    val previousPosition = selectedPosition
                    selectedPosition = currentPosition

                    // Update hanya posisi yang berubah
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    // Jalankan listener dengan item yang dipilih
                    listener(items[selectedPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title

        // Highlight untuk item yang dipilih
        if (position == selectedPosition) {
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.ijo3))
            holder.line.visibility = View.VISIBLE
        } else {
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.black))
            holder.line.visibility = View.GONE
        }
    }

    override fun getItemCount() = items.size
}
