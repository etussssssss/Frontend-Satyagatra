package com.example.projekconsultant.adapter

import android.content.Intent
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
import com.example.projekconsultant.sideactivity2.AllReview
import com.google.android.flexbox.FlexboxLayout

class ReviewItemAdapterLast(private var items: List<ReviewItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // ViewHolder untuk item terakhir (tombol)
    class LastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonLast: Button = view.findViewById(R.id.btnallreview)
    }

    // ViewHolder untuk item normal (ulasan)
    class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Variable Nama.
        val nama: TextView = view.findViewById(R.id.nama) // Referensi TextView untuk menampilkan nama pengguna
        // Variable Bintang.
        val stars: List<ImageView> = listOf( // Daftar referensi ImageView untuk menampilkan bintang rating
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )
        // Referensi FlexboxLayout untuk menampilkan tag ulasan
        val tags: FlexboxLayout = view.findViewById(R.id.tag_layout)
        val feedback:TextView = view.findViewById(R.id.textfeedback)
    }

    // Metode untuk memperbarui data
    fun updateData(newReviews: List<ReviewItem>) {
        this.items = newReviews
        notifyDataSetChanged()
    }

    companion object {
        // Konstanta untuk tipe tampilan
        private const val VIEW_TYPE_NORMAL = 0 // Tipe tampilan untuk item normal
        private const val VIEW_TYPE_LAST = 1 // Tipe tampilan untuk item terakhir (tombol)
    }

    // Membuat ViewHolder berdasarkan tipe tampilan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LAST -> {
                // Inflate layout untuk item terakhir (tombol)
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_home_btnlast, parent, false)
                LastViewHolder(view)
            }
            else -> {
                // Inflate layout untuk item normal (ulasan)
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_home, parent, false)
                NormalViewHolder(view)
            }
        }
    }

    // Mengikat data ke ViewHolder berdasarkan tipe tampilan dan posisi
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_NORMAL -> {
                // Jika ViewHolder adalah tipe NormalViewHolder dan posisi valid
                if (holder is NormalViewHolder && position < items.size) {
                    val item = items[position] // Pastikan hanya mengakses data pada posisi valid
                    // Menampilkan nama dengan format: huruf pertama + bintang (*) + huruf terakhir
                    holder.nama.text = "${item.nama.first()}${"*".repeat(10 - 1)}${item.nama.last()}"
                    // Atur bintang berdasarkan rating
                    holder.stars.forEachIndexed { index, star ->
                        if (index < item.rateStar) {
                            // Bintang penuh untuk rating yang diberikan
                            star.setImageResource(R.drawable.a_review_star_select) // Bintang penuh
                        } else {
                            // Bintang kosong untuk sisa rating
                            star.setImageResource(R.drawable.a_review_star_unselect) // Bintang kosong
                        }
                    }
                    // Atur tag
                    // Menampilkan tag ulasan
                    holder.tags.removeAllViews() // Hapus semua view sebelumnya dari FlexboxLayout
                    for (text in item.tag) {
                        val textView = TextView(holder.tags.context).apply {
                            // Konfigurasi layout untuk tag
                            val layoutParams = FlexboxLayout.LayoutParams(
                                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                FlexboxLayout.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.setMargins(8, 8, 8, 8) // Margin untuk setiap tag
                            this.layoutParams = layoutParams
                            setText(text) // Menampilkan teks tag
                            textSize = 12f // Ukuran teks
                            background = ContextCompat.getDrawable(context, R.drawable.border_radius_2) // Background dengan radius
                            backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar) // Warna background
                            setPadding(16, 8, 16, 8) // Padding untuk teks
                            gravity = Gravity.CENTER // Teks di tengah
                        }
                        holder.tags.addView(textView)
                    }
                    holder.feedback.text = "${item.pengalaman}"
                }
            }
            //Tombol Ke Menampilkan semua review user.
            VIEW_TYPE_LAST -> {
                if (holder is LastViewHolder) {
                    // Mengatur klik tombol untuk membuka halaman AllReview
                    holder.buttonLast.setOnClickListener {
                        val intent = Intent(holder.itemView.context, AllReview::class.java)
                        // Mengirim data (list ulasan) ke activity AllReview
                        intent.putParcelableArrayListExtra("data", ArrayList(this.items))
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }

    // Mengembalikan jumlah item yang akan ditampilkan
    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 // Jika kosong, hanya menampilkan tombol terakhir
        else if (items.size > 3) 4 // Maks 3 item + tombol terakhir
        else items.size + 1 // Tambahkan 1 untuk tombol terakhir
    }

    // Menentukan tipe tampilan berdasarkan posisi
    override fun getItemViewType(position: Int): Int {
        // Pastikan tombol terakhir berada pada posisi items.size
        return if (position == (if (items.size > 3) 3 else items.size)) VIEW_TYPE_LAST
        else VIEW_TYPE_NORMAL
    }
}










//layoutParams.setMargins(8, 8, 8, 8)
//this.layoutParams = layoutParams
//setText(text)
//textSize = 12f
//background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
//backgroundTintList =
//ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
//setPadding(16, 8, 16, 8)
//gravity = Gravity.CENTER




//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = items[position]
//
//        when (holder) {
//            is NormalViewHolder -> {
//
//                holder.nama.text = item.nama
//                holder.stars.forEachIndexed { index, star  ->
//                    if (index < item.rate) {
//                        star.setImageResource(R.drawable.a_review_star_select) // Bintang penuh
//                    } else {
//                        star.setImageResource(R.drawable.a_review_star_unselect) // Bintang kosong
//                    }
//                }
//
//                holder.tags.removeAllViews()
//                // Mengecek apakah hanya ada satu tag
//
//                for (text in item.tag) {
//                    val textView = TextView(holder.tags.context).apply {
//
//                        val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
//                        layoutParams.setMargins(8, 8, 8, 8)
//                        this.layoutParams = layoutParams
//                        setText(text)
//                        textSize = 12f
//                        background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
//                        backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
//                        setPadding(16, 8, 16, 8)
//                        gravity = Gravity.CENTER
//                    }
//                    holder.tags.addView(textView)
//                }
//
//            }
//            is LastViewHolder -> {
//                holder.buttonLast.setOnClickListener {
//                    // Aksi untuk tombol pada item terakhir // Ga Di Pake Cause
//                    // val intent = Intent(holder.itemView.context, ReviewAllAdmin::class.java)
//
//                    val intent = Intent(holder.itemView.context, AllReview::class.java)
//                    intent.putParcelableArrayListExtra("data", ArrayList(this.items))
//                    holder.itemView.context.startActivity(intent)
//                }
//            }
//        }
//    }



//override fun getItemCount(): Int {
//    return if (items.size > 3) 4 else items.size
//}







//                    intent.putStringArrayListExtra("data", ArrayList(items.map { it.toString() }))







//    override fun getItemCount(): Int = items.size




//    override fun getItemViewType(position: Int): Int {
////        return if (position == items.size - 1) VIEW_TYPE_LAST else VIEW_TYPE_NORMAL
//        return if (position == 3) VIEW_TYPE_LAST else VIEW_TYPE_NORMAL
//
//    }