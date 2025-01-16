package com.example.projekconsultant.sideactivity

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.adapter.SelesaiDataAdapter
import com.google.android.flexbox.FlexboxLayout

class SemuaReviewSaya : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semua_review_saya)

        val nama: TextView = findViewById(R.id.nama)
        val total: TextView = findViewById(R.id.sum)
        val namaKonselor: TextView = findViewById(R.id.sum2)
        val stars: List<ImageView> = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )
        val desk: TextView = findViewById(R.id.desk)
        val tags: FlexboxLayout = findViewById(R.id.tag_layout)

        val namaUser = intent.getStringExtra("NAMA") ?: ""
        val tanggalUser = intent.getStringExtra("TANGGAL") ?: ""
        val onOffUser = intent.getStringExtra("ONOFF") ?: ""

        val konselorUser = intent.getStringExtra("KONSELOR") ?: ""

        val tagKepuasanUser = intent.getStringExtra("TAG") ?: ""
        val rateUser = intent.getIntExtra("RATE", 0)
        val pengalamanUser = intent.getStringExtra("PENGALAMAN") ?: ""

        nama.text = namaUser
        total.text = "${tanggalUser} | ${onOffUser}"
        namaKonselor.text = "Konselor: ${konselorUser}"
        desk.text = "Pengalaman: ${pengalamanUser}"
        stars.forEachIndexed { index, star ->
            if (index < rateUser) {
                star.setImageResource(R.drawable.a_review_star_select) // Bintang penuh
            } else {
                star.setImageResource(R.drawable.a_review_star_unselect) // Bintang kosong
            }
        }

        tags.removeAllViews()
        for (text in tagKepuasanUser.split(", ")) {
            val textView = TextView(this).apply {

                val layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(8, 8, 8, 8)
                this.layoutParams = layoutParams
                setText(text)
                textSize = 12f
                background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
                setPadding(16, 8, 16, 8)
                gravity = Gravity.CENTER
            }
            tags.addView(textView)
        }

        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            finish()
        }
    }
}




//val recy = findViewById<RecyclerView>(R.id.recyclerViewReview1)
//recy.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


//val adapter = AdapterDetailReviewUser(data)
//recy.adapter = adapter

//val data = listOf(
//    SelesaiDetailReviewUser("S-123", "Kak Nikie","Wily Ahmad", "Via Chat","27 Desember 2024","Sangat Baik", "Online",5, "Profesional, Bersih"),
//)