package com.example.projekconsultant.adapter2

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.admin.DataPendaftaran
import com.example.projekconsultant.admin.DataRiwayatSelesaiAdmin

class AdapterProfileDataRiwayatKonsultasiAdmin(private var data: List<DataRiwayatSelesaiAdmin>) : RecyclerView.Adapter<AdapterProfileDataRiwayatKonsultasiAdmin.DataRiwayat>() {

    inner class DataRiwayat(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val main:LinearLayout = itemView.findViewById(R.id.main)

        val nama: TextView = itemView.findViewById(R.id.nama)
        val topik: TextView = itemView.findViewById(R.id.topik)
        val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        val offorn: TextView = itemView.findViewById(R.id.offoron)
        val descriptionTextView: TextView = itemView.findViewById(R.id.deskripsTopik)
        
        val toggleTextView: TextView = itemView.findViewById(R.id.toggleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRiwayat {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_profile_riwayat_konsultasi_1, parent, false)

        return DataRiwayat(view)
    }


    override fun onBindViewHolder(holder: DataRiwayat, position: Int) {
        val datas = data[position]

        holder.nama.text = datas.namaUser
        holder.topik.text = datas.curhatanUser
        holder.tanggal.text = datas.tanggalSelesaiUser
        holder.offorn.text = datas.layananOnlineOrOfflineUser
        holder.descriptionTextView.text = datas.curhatanUser


        holder.toggleTextView.setOnClickListener {
            if (holder.descriptionTextView.maxLines == 3) {
                // Tampilkan seluruh teks
                holder.descriptionTextView.maxLines = Int.MAX_VALUE
                holder.toggleTextView.text = "Lihat lebih sedikit"
            } else {
                // Kembali ke teks pendek
                holder.descriptionTextView.maxLines = 3
                holder.toggleTextView.text = "Lihat lebih banyak"
            }
        }

        holder.main.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }

    // Fungsi untuk memperbarui data
    fun updateData(newItems: List<DataRiwayatSelesaiAdmin>) {
        data = newItems
        notifyDataSetChanged()
    }
}