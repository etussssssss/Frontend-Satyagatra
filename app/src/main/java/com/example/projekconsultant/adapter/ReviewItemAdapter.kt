package com.example.projekconsultant.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.google.android.flexbox.FlexboxLayout

class ReviewItemAdapter(private val items: List<ReviewItem>) : RecyclerView.Adapter<ReviewItemAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nama: TextView = view.findViewById(R.id.nama)
        val stars = listOf(
            view.findViewById<ImageView>(R.id.star1),
            view.findViewById<ImageView>(R.id.star2),
            view.findViewById<ImageView>(R.id.star3),
            view.findViewById<ImageView>(R.id.star4),
            view.findViewById<ImageView>(R.id.star5),
        )
        val tags:FlexboxLayout = itemView.findViewById(R.id.tag_layout)
    }

    class LastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonLast: Button = view.findViewById(R.id.btnallreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_home, parent, false)


        return  ReviewViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = items[position]

        holder.nama.text = item.nama

        holder.stars.forEachIndexed { index, star ->
            if (index < item.rateStar) {
                star.setImageResource(R.drawable.a_review_star_select) // Bintang penuh
            } else {
                star.setImageResource(R.drawable.a_review_star_unselect) // Bintang kosong
            }
        }

        holder.tags.removeAllViews()
        // Mengecek apakah hanya ada satu tag
//        val isSingleTag = item.tag.size == 1

        for (text in item.tag) {
            val textView = TextView(holder.tags.context).apply {

                // Menentukan layoutParams berdasarkan jumlah tag
//                val layoutParams = if (isSingleTag) {
//                    // Setel lebar ke MATCH_PARENT jika hanya ada satu kata
//                    FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
//                } else {
//                    // Setel lebar ke WRAP_CONTENT jika ada lebih dari satu kata
//                    FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
//                }

                val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                // Atur margin antar item
                layoutParams.setMargins(8, 8, 8, 8)

                this.layoutParams = layoutParams
                setText(text)
                textSize = 12f
                background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
                setPadding(16, 8, 16, 8)
                gravity = Gravity.CENTER
            }

            // Menambahkan textView ke FlexboxLayout
            holder.tags.addView(textView)
        }
    }
}