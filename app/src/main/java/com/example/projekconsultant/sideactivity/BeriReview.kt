package com.example.projekconsultant.sideactivity

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BeriReview : BaseActivity() {
    private var selectedStars = 0
    private var strTopik:String = "none"
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beri_review)

        db = FirebaseFirestore.getInstance()
        val t1 = findViewById<TextView>(R.id.t112)
        val text1 = t1.text.toString()

        val t2 = findViewById<TextView>(R.id.t114)
        val text2 = t2.text.toString()
        lastTextRed(t2, text2)
        lastTextRed(t1, text1)

        val pengalamanR = findViewById<EditText>(R.id.etDescription)

        val stars = listOf(
            findViewById<ImageView>(R.id.star1),
            findViewById<ImageView>(R.id.star2),
            findViewById<ImageView>(R.id.star3),
            findViewById<ImageView>(R.id.star4),
            findViewById<ImageView>(R.id.star5)
        )

        val tags = listOf(
            findViewById<TextView>(R.id.responsif),
            findViewById<TextView>(R.id.bersih),
            findViewById<TextView>(R.id.goodlistener),
            findViewById<TextView>(R.id.privasi),
            findViewById<TextView>(R.id.nonjudge),
            findViewById<TextView>(R.id.nyaman),
            findViewById<TextView>(R.id.mudah),
            findViewById<TextView>(R.id.waktucukup),
            findViewById<TextView>(R.id.membantu),
            findViewById<TextView>(R.id.fasilitas),
            findViewById<TextView>(R.id.konselingpuas),
        )

        for (a in tags){
            select(a)
        }

        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                // Logika untuk toggle pada bintang pertama
                if (index == 0) {
                    if (selectedStars == 1) {
                        // Jika star1 sudah terpilih, maka unselect star1
                        selectedStars = 0
                    } else {
                        // Jika star1 belum terpilih, maka pilih star1
                        selectedStars = 1
                    }
                } else {
                    // Jika bintang lain yang dipilih, pilih hingga bintang yang diklik
                    selectedStars = index + 1
                }

                // Update tampilan bintang
                updateStars(stars, selectedStars)
            }
        }

        val inisial = intent.getStringExtra("INISIAL").toString()
        val nomorPendaftaran = intent.getStringExtra("NOMORPENDAFTARAN")
        findViewById<Button>(R.id.Enter).setOnClickListener {
            strTopik = "${selectTopik.joinToString(", ")}"

            if(selectTopik.isNotEmpty() && pengalamanR.text.trim().isNotEmpty() && nomorPendaftaran != null){
                addReview(inisial, nomorPendaftaran, strTopik, selectedStars, "${pengalamanR.text.trim()}")
                finish()
            }
        }

        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            finish()
        }
    }

    private fun addReview(inisial:String, nomorPendaftaran:String, stringTags:String, ratingStars:Int, komentar:String){
        val data = hashMapOf(
            "inisial" to "${inisial}",
            "tag" to "${stringTags}",
            "star" to ratingStars,
            "pengalaman" to "${komentar}",
        )

        db.collection("review").document(nomorPendaftaran).set(data).addOnSuccessListener {
                Log.d("Firestore", "Data berhasil disimpan ke dokumen $nomorPendaftaran")
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Gagal menyimpan data", exception)
        }
    }

    private fun updateStars(stars: List<ImageView>, selectedStars: Int) {
        stars.forEachIndexed { index, imageView ->
            if (index < selectedStars) {
                // Bintang terpilih
                imageView.setImageResource(R.drawable.a_review_star_select)
            } else {
                // Bintang tidak terpilih
                imageView.setImageResource(R.drawable.a_review_star_unselect)
            }
        }
    }

    private fun lastTextRed(t1:TextView, text :String){
        if (text.isNotEmpty()) {
            val spannable = SpannableString(text)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.red500)), // Memberikan warna merah
                text.length - 1,                // Indeks huruf terakhir
                text.length,                    // Akhir teks
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            t1.text = spannable // Set ulang teks dengan efek
        }
    }

    private var select3: Int = 0
    private var selectTopik = ArrayList<String>()
    fun select(btn: TextView) {
        btn.setOnClickListener {
            if (btn.isSelected) {

                selectTopik.remove(btn.text.toString())
                select3--
                btn.isSelected = false // Deselect
            } else if (select3 < 3) {

                selectTopik.add(btn.text.toString())
                select3++
                btn.isSelected = true // Select
            }
            // Log untuk memeriksa status
            Log.d("MSG", "${btn.isSelected}, ${btn.text}")
        }
    }
}