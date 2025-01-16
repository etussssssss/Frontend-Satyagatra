package com.example.projekconsultant.sideactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.google.android.flexbox.FlexboxLayout

class DeskripsiKonselorHome : BaseActivity() {
    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deskripsi_konselor_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val foto = intent.getIntExtra("fotoKonselor", 1)
        val imageView = findViewById<ImageView>(R.id.imageViewProfileDes)

        Glide.with(this)
            .load(foto) // foto adalah URL atau path gambar
            .into(imageView)

        findViewById<TextView>(R.id.namaKonselorDeskDetail).text = intent.getStringExtra("namaKonselor")
        findViewById<TextView>(R.id.descSingkat).setText(intent.getIntExtra("descsingkatKonselor", 1))

        findViewById<ImageView>(R.id.backtoViewHome).setOnClickListener {
            finish()
        }

        val tags = findViewById<FlexboxLayout>(R.id.tag_layout)
        val tagsData = intent.getStringArrayListExtra("tags")

        tags.removeAllViews()
        if (tagsData != null) {
            for (text in tagsData) {
                val textView = TextView(tags.context).apply {

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
                tags.addView(textView)
            }
        }

        findViewById<Button>(R.id.tutupDeskKonselor).setOnClickListener {
            finish()
        }


    }

}