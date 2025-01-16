package com.example.projekconsultant.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.google.android.flexbox.FlexboxLayout

class ReviewAllAdapter(private val items: List<ReviewItem>) : RecyclerView.Adapter<ReviewAllAdapter.NormalViewHolder>() {

    // Pengumpulan Variable dari beberapa tampilan Layout itemReviewAll
    class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nama: TextView = view.findViewById(R.id.nama)
        val stars: List<ImageView> = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )
        val tags: FlexboxLayout = view.findViewById(R.id.tag_layout)
        val feedback:TextView = view.findViewById(R.id.textfeedback)
    }

    // Inisialisasi View tampilan Layout itemReviewALl
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_all, parent, false)
        return NormalViewHolder(view)
    }

    // Bind Tampilan
    override fun onBindViewHolder(holder: NormalViewHolder, position: Int) {
        val item = items[position]

        // Manipulasi nama agar lebih privasi.
        holder.nama.text = "${item.nama.first()}${"*".repeat(10 - 1)}${item.nama.last()}"

        // Berfungsi sebagai Rating bintang di tampilan dengan menggunakan hitungan angka integer.
        holder.stars.forEachIndexed { index, star ->
            if (index < item.rateStar) {
                star.setImageResource(R.drawable.a_review_star_select) // Bintang penuh
            } else {
                star.setImageResource(R.drawable.a_review_star_unselect) // Bintang kosong
            }
        }

        // Atur tag
        // Menampilkan tag ulasan
        holder.tags.removeAllViews() // Hapus semua view sebelumnya dari FlexboxLayout
        for (text in item.tag) {
            val textView = TextView(holder.tags.context).apply {
                // Konfigurasi layout untuk tag
                val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(8, 8, 8, 8)
                this.layoutParams = layoutParams
                setText(text)
                textSize = 12f
                background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
                setPadding(16, 8, 16, 8)
                gravity = Gravity.CENTER
            }
            holder.tags.addView(textView)
        }

        holder.feedback.text = "${item.pengalaman}"
    }

    override fun getItemCount(): Int = items.size
}