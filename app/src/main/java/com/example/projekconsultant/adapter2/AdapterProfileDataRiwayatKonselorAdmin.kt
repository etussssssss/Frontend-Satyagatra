package com.example.projekconsultant.adapter2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.admin.DataRiwayatKonselor
import com.example.projekconsultant.admin.DataRiwayatSelesaiAdmin

class AdapterProfileDataRiwayatKonselorAdmin(private var data: List<DataRiwayatKonselor>) : RecyclerView.Adapter<AdapterProfileDataRiwayatKonselorAdmin.DataRiwayat>() {

    inner class DataRiwayat(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val main: LinearLayout = itemView.findViewById(R.id.main)

        val nama: TextView = itemView.findViewById(R.id.nama)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRiwayat {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_profile_riwayat_konsultasi_2, parent, false)

        return DataRiwayat(view)
    }


    override fun onBindViewHolder(holder: DataRiwayat, position: Int) {
        val datas = data[position]

        holder.nama.text = "${datas.namaKonselor}"
    }


    override fun getItemCount(): Int {
        return  data.size
    }

    // Fungsi untuk memperbarui data
    fun updateData(newItems: List<DataRiwayatKonselor>) {
        data = newItems
        notifyDataSetChanged()
    }
}