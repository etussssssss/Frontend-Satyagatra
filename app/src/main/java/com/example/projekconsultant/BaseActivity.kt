package com.example.projekconsultant

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

// Class Base Activity ini berfungsi berbagai fungsi sebenar nya.
// Tetapi kali ini base activity saya hanya di gunakan untuk mengatur tampilan fullscreen pada layar hp user masing-masing saja.
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Membuat status bar transparan sepenuhnya
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            // window.navigationBarColor = Color.TRANSPARENT

            // Mengatur ikon status bar menjadi gelap jika background terang
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }

            //Dark mode For Next Update
//            val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
//            if (isNightMode) {
//                window.navigationBarColor = ContextCompat.getColor(this, R.color.bottom_nav_background)
//            } else {
//                window.navigationBarColor = ContextCompat.getColor(this, R.color.bottom_nav_background)
//            }
        }
    }
}
