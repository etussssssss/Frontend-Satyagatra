package com.example.projekconsultant.methodsNService

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R

class NoInternet : BaseActivity() {
    private var count:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        val retryButton = findViewById<Button>(R.id.btnRetry)
        findViewById<Button>(R.id.btnRetry).setOnClickListener {
            if (NetworkUtils.isInternetAvailable(this)) {
                finish()
            } else {
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onBackPressed() {
        count++
        if(count == 3){
            Toast.makeText(this, "Tekan sekali lagi Jika ingin keluar", Toast.LENGTH_SHORT).show()
        }else if(count == 4){
            super.onBackPressed()
            finishAffinity()
        }
    }
}