package com.example.projekconsultant.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R


class JadwalTanggalAdapter(private val data: List<JadwalTanggal> , private val onItemClick: (JadwalTanggal?) -> Unit) : RecyclerView.Adapter<JadwalTanggalAdapter.TanngalViewHolder>() {
    private var selectedPosition: Int = -1
    inner class TanngalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: LinearLayout = itemView.findViewById(R.id.date)
        val hariJ: TextView = itemView.findViewById(R.id.hariT)
        val tanggalJ: TextView = itemView.findViewById(R.id.angkatT)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: JadwalTanggal) {

            // Periksa apakah item ini adalah item yang dipilih
            if (position == selectedPosition) {
                itemView.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.ijo3)
            } else {
                itemView.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.ijofigmapudar)
            }

            itemView.setOnClickListener {
                // Jika item yang sama diklik lagi, batalkan pemilihan
                if (selectedPosition == position) {
                    selectedPosition = -1  // Batalkan pemilihan
                    onItemClick(null)
                } else {
                    selectedPosition = position  // Pilih item yang baru
                    onItemClick(item)
                }
                notifyDataSetChanged()  // Update tampilan RecyclerView
//                onItemClick(item)  // Panggil aksi
//                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TanngalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tanggal_jadwal, parent, false)


        return TanngalViewHolder(view)
    }

    override fun onBindViewHolder(holder: TanngalViewHolder, position: Int) {
        val item = data[position]

        holder.bind(item)

        holder.hariJ.setText( if(position == 0) "Besok" else item.namaHari )
        holder.tanggalJ.setText( extractShortDate(item.tanngalHari) )


    }

    override fun getItemCount(): Int {
        return  data.size
    }

    fun extractShortDate(input: String): String {
        val parts = input.split(" ")
        if (parts.size == 3) {
            val day = parts[0] // Ambil angka tanggal
            val month = parts[1].substring(0, 3).capitalize() // Ambil 3 huruf pertama dari bulan
            return "$day $month"
        }
        return input // Kembalikan input asli jika format tidak sesuai
    }


}