package com.example.projekconsultant.methodsNService

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CustomJS : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {
                // Halaman di luar kiri layar
                page.alpha = 0f // Tidak terlihat
            }
            position <= 1 -> {
                // Halaman berada di tengah layar atau sedang dalam proses sliding
                page.alpha = 1 - Math.abs(position) // Transparansi berubah saat berpindah

                // Efek slide
                page.translationX = -position * page.width // Geser horizontal halaman untuk slide

                // Efek getaran
                val shakeFactor = 20 // Jumlah piksel untuk getaran
                if (position < 0) {
                    // Jika posisi negatif (halaman ke kiri)
                    page.translationX += shakeFactor * Math.abs(position) // Menggeser ke kanan
                } else {
                    // Jika posisi positif (halaman ke kanan)
                    page.translationX -= shakeFactor * Math.abs(position) // Menggeser ke kiri
                }

                // Efek skala (zoom in/out effect)
                val scaleFactor = 0.85f + (1 - Math.abs(position)) * 0.15f
                page.scaleX = scaleFactor // Skala horizontal
                page.scaleY = scaleFactor // Skala vertikal
            }
            else -> {
                // Halaman di luar kanan layar
                page.alpha = 0f // Tidak terlihat
            }
        }
    }



}
