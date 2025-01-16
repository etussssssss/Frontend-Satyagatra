package com.example.projekconsultant

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.projekconsultant.adapter.AdapterGetStarted
import com.example.projekconsultant.fragmentgetstarted.NextPageOne
import com.example.projekconsultant.fragmentgetstarted.NextPageThree
import com.example.projekconsultant.fragmentgetstarted.NextPageTwo
import com.example.projekconsultant.methodsNService.Animate
import com.example.projekconsultant.methodsNService.CustomJS
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet


class GetStarted : BaseActivity() {
    // Variable dots dan pengecekan internet.
    private lateinit var dotsLayout: LinearLayout
    private var networkChangeReceiver: NetworkChangeReceiver? = null

    // Method yang akan di arahkan jika ada internet atau tidak ada internet.
    private fun checkNetworkAndProceed() {
        // Initialize NetworkChangeReceiver if not already initialized
        if (networkChangeReceiver == null) {
            networkChangeReceiver = NetworkChangeReceiver { isConnected ->
                if (!isConnected) {
                    // If there's no network, navigate to NoInternet activity
                    val intent = Intent(this, NoInternet::class.java)
                    startActivity(intent)
                } else {
                    // If there's network, proceed to Login
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish() // Finish the GetStarted activity
                }
            }
        }

        // Trigger network check immediately
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }
    // Pengecekan real time lewat onResume
    override fun onResume() {
        super.onResume()
        // Register BroadcastReceiver hanya jika sudah diinisialisasi
        networkChangeReceiver?.let {
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(it, intentFilter)
        }
    }
    // Pengecekan real time lewat onPause
    override fun onPause() {
        super.onPause()
        // Unregister BroadcastReceiver hanya jika sudah diinisialisasi
        networkChangeReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        dotsLayout = findViewById(R.id.dotsLayout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val fragments = listOf(
            NextPageOne(),
            NextPageTwo(),
            NextPageThree()
        )

        val adapter = AdapterGetStarted(this, fragments)
        viewPager.adapter = adapter
        //Btn Skip
        findViewById<TextView>(R.id.btnSkip).setOnClickListener {
            checkNetworkAndProceed()
        }

        var i = 0
        findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1 // Next page
            }
            i++;
            Log.d("KLIK", "${viewPager.currentItem}, ${i}")
            if (i == 3){
                checkNetworkAndProceed()
            }
        }

        viewPager.setPageTransformer(CustomJS())
        createDots(AdapterGetStarted(this, fragments).itemCount)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })
    }

    // Method inisialisasi layout Bulatan.
    private fun createDots(count: Int) {
        dotsLayout.removeAllViews() // Menghapus semua dots yang ada sebelumnya
        for (i in 0 until count) {
            val dot = View(this) // Ganti ukuran jika perlu
            val params = LinearLayout.LayoutParams(32, 32)

            params.setMargins(4, 0, 4, 0) // Mengatur margin antara dots
            dot.layoutParams = params
            dot.setBackgroundResource(R.drawable.dot_unactive) // Menetapkan drawable untuk dot
            dotsLayout.addView(dot) // Menambahkan dot baru ke layout
        }
        updateDots(0) // Memastikan titik pertama diatur sebagai aktif
    }

    // Method manipulasi tampilan bulatan hijau ketika di klik atau di geser.
    private fun updateDots(position: Int) {
        var animate = Animate()

        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i)
            val params = dot.layoutParams as LinearLayout.LayoutParams

            if (i == position) {
                dot.setBackgroundResource(R.drawable.dot_active) // Set active drawable
                params.width = 250  // Set width for active dot
                animate.CustomJSDots(dot, true) // Animasi untuk dot aktif
            } else {
                dot.setBackgroundResource(R.drawable.dot_unactive) // Set inactive drawable
                params.width = 40 // Set width for inactive dot (default size)
                animate.CustomJSDots(dot, false)
            }
            dot.layoutParams = params // Update the layout params for the view
        }
    }
}