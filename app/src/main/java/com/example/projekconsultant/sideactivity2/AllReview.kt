package com.example.projekconsultant.sideactivity2

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.ReviewAllAdapter
import com.example.projekconsultant.adapter.ReviewItem

class AllReview : BaseActivity() {
    private lateinit var recyclerView3:RecyclerView
    private lateinit var empty:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)

        // Ambil data yang sudah di bundling dari Review Item Adapter Last -> Class All Review (Class ini).
        val receivedData = intent.getParcelableArrayListExtra<ReviewItem>("data")

        recyclerView3 = findViewById(R.id.recyclerViewReview1)
        empty = findViewById(R.id.empty)

        // Pengecekan jika data dari variable ReceivedData kosong atau tidak ada sama sekali maka akan menampilakan tampilan none di layar user.
        // Tetapi jika ada data maka akan menampilkan semua data Review Semua User.
        // Data tersebut akan di manipulasi di ReviewAllAdapter ->
        if (!receivedData.isNullOrEmpty()) {
            recyclerView3.visibility = View.VISIBLE
            empty.visibility = View.GONE

            recyclerView3.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            // -> Review All Adapter
            val adapter3 = ReviewAllAdapter(receivedData)
            recyclerView3.adapter = adapter3
        } else {
            recyclerView3.visibility = View.GONE
            empty.visibility = View.VISIBLE
        }


        // Tombol Kembali
        findViewById<ImageView>(R.id.backRev).setOnClickListener {
            finish()
        }
    }
}




//if (receivedData != null) {
//    for (review in receivedData) {
//        Log.d("ReviewItem", "Name: ${review.nama}, Rating: ${review.rate}, Tags: ${review.tag}")
//    }
//}