package com.example.projekconsultant.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R

class RiwayatItemAdapter(private val items: List<RiwayatItem>, private val listener: (RiwayatItem) -> Unit) : RecyclerView.Adapter<RiwayatItemAdapter.MenuViewHolder>() {
    private var selectedPosition = 0
    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.menuTitle)
        val line: View = view.findViewById(R.id.line)

        init {
            view.setOnClickListener {
                // Simpan posisi sebelumnya dan yang baru
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                // Update item sebelumnya dan item yang dipilih
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                // Jalankan listener untuk fragment
                listener(items[selectedPosition])
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
       if (position == selectedPosition) {
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.ijo3))
            holder.line.visibility = View.VISIBLE // Garis hijau ditampilkan
        } else {
            holder.title.setTextColor(holder.itemView.context.getColor(R.color.black))
            holder.line.visibility = View.GONE // Garis hijau disembunyikan
        }
    }

    override fun getItemCount() = items.size

    fun setDefaultSelection() {
        selectedPosition = 0 // Pilih item pertama
        notifyItemChanged(selectedPosition) // Update item pertama
    }
}




//        holder.itemView.setOnClickListener {
//            // Menyimpan posisi item yang dipilih
//            val previousPosition = selectedPosition
//            selectedPosition = iss
//            notifyItemChanged(previousPosition) // Mengupdate item sebelumnya
//            notifyItemChanged(position) // Mengupdate item yang baru dipilih
//            listener(item) // Menjalankan aksi klik
//        }