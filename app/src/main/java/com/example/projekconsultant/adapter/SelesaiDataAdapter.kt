package com.example.projekconsultant.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.data.UserSelesai
import com.example.projekconsultant.sideactivity.BeriReview
import com.example.projekconsultant.sideactivity.SemuaReviewSaya

//class SelesaiDataAdapter(private val selesaiDataUser: List<SelesaiData>, private val thDataDetail: List<SelesaiDetailReviewUser>) : RecyclerView.Adapter<SelesaiDataAdapter.SelesaiViewHolder>() {
class SelesaiDataAdapter(private var selesaiDataUser: List<UserSelesai>) : RecyclerView.Adapter<SelesaiDataAdapter.SelesaiViewHolder>() {
    inner class SelesaiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urname: TextView = itemView.findViewById(R.id.nama)
        val topiq: TextView = itemView.findViewById(R.id.topik)
        val offoron:TextView = itemView.findViewById(R.id.offoron)
        val deskripsitopik:TextView = itemView.findViewById(R.id.deskripsTopik)

        val descriptionTextView:TextView = itemView.findViewById(R.id.deskripsTopik)
        val toggleTextView:TextView = itemView.findViewById(R.id.toggleTextView)

        val btnmyReview:View = itemView.findViewById(R.id.myReview)
    }

    // Fungsi untuk memperbarui data
    fun updateData(newData: List<UserSelesai>) {
        selesaiDataUser = newData
        notifyDataSetChanged() // Memperbarui tampilan setelah data diubah
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelesaiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_selesai, parent, false)
        return SelesaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelesaiViewHolder, position: Int) {
        val data = selesaiDataUser[position]
        holder.urname.text = data.selesaidata.name
        holder.topiq.text = data.selesaidata.topik
        holder.offoron.text = data.selesaidata.offOrOn
        holder.deskripsitopik.text = data.selesaidata.isiCurhatan

        if (data.selesaidetailreviewuser != null ) {
            holder.btnmyReview.setBackgroundResource(R.drawable.riwayat_ic_reviewsaya)
            holder.btnmyReview.setOnClickListener {
                val intent = Intent(holder.itemView.context, SemuaReviewSaya::class.java)
                intent.putExtra("NAMA","${data.selesaidata.name}")
                intent.putExtra("TANGGAL","${data.selesaidata.tanggal}")
                intent.putExtra("ONOFF","${data.selesaidata.offOrOn}")

                intent.putExtra("TAG", "${data.selesaidetailreviewuser.tag}")
                intent.putExtra("RATE", data.selesaidetailreviewuser.rateStar)
                intent.putExtra("PENGALAMAN","${data.selesaidetailreviewuser.pengalaman}")

                intent.putExtra("KONSELOR","${data.selesaidata.konselor}")
                intent.putExtra("NOMORPENDAFTARAN", data.selesaidata.nomorPendaftaran)
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.btnmyReview.setBackgroundResource(R.drawable.riwayat_ic_berireview)

            holder.btnmyReview.setOnClickListener {
                val intent = Intent(holder.itemView.context, BeriReview::class.java)
                intent.putExtra("NOMORPENDAFTARAN", data.selesaidata.nomorPendaftaran)
                intent.putExtra("INISIAL", "${data.selesaidata.name.first()}${data.selesaidata.name.trim().last()}")
                holder.itemView.context.startActivity(intent)
            }
        }

        holder.toggleTextView.setOnClickListener {
            if (holder.descriptionTextView.maxLines == 2) {
                // Tampilkan seluruh teks
                holder.descriptionTextView.maxLines = Int.MAX_VALUE
                holder.toggleTextView.text = "Lihat lebih sedikit"
            } else {
                // Kembali ke teks pendek
                holder.descriptionTextView.maxLines = 2
                holder.toggleTextView.text = "Lihat lebih banyak"
            }
        }
    }

    override fun getItemCount(): Int {
        return  selesaiDataUser.size
    }
}






//     intent.putExtra("PENGALAMAN","${detailMap["${data.nomorPendaftaran}"]?.pengalaman}")
