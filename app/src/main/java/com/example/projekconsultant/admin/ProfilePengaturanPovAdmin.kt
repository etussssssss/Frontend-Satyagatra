package com.example.projekconsultant.admin

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.Login
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.example.projekconsultant.sideactivity.LupaPassword
import com.google.firebase.auth.FirebaseAuth

class ProfilePengaturanPovAdmin : BaseActivity() {

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    override fun onResume() {
        super.onResume()
        // Register BroadcastReceiver
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }
    override fun onPause() {
        super.onPause()
        // Unregister BroadcastReceiver
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_pengaturan_pov_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        val btnBack = findViewById<ImageView>(R.id.backpengaturan)
        btnBack.setOnClickListener {
            finish()
        }

        val threedot = findViewById<ImageButton>(R.id.dotslogout)
        val logoutLayout = findViewById<LinearLayout>(R.id.logout)

        threedot.setOnClickListener {
            logoutLayout.visibility = View.VISIBLE
            logoutLayout.alpha = 0f
            logoutLayout.animate().alpha(1f).setDuration(500).start()
            threedot.visibility = View.GONE
        }

        val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_logout)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
        dialog.setCancelable(true)

        // Menangani event ketika layout logout diklik
        logoutLayout.setOnClickListener {
            dialog.show() // Tampilkan dialog ketika layout diklik

            dialog.findViewById<Button>(R.id.btn_no)?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.findViewById<Button>(R.id.btn_yes)?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
        }


        val rootLayout = findViewById<ConstraintLayout>(R.id.main) // Sesuaikan dengan id root layout Anda
        rootLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Ketika layar disentuh di mana pun selain tombol
                    if (logoutLayout.visibility == View.VISIBLE) {
                        logoutLayout.visibility = View.GONE

                        threedot.alpha = 0f
                        threedot.animate().alpha(1f).setDuration(500).start()
                        threedot.visibility = View.VISIBLE
                    }
                }
            }
            true
        }

        findViewById<LinearLayout>(R.id.gantipassword).setOnClickListener {
            val intent = Intent(this@ProfilePengaturanPovAdmin, LupaPassword::class.java)
            intent.putExtra("ubahpwMainActivity", "Ganti Password")
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.editakun).setOnClickListener {
            val intent = Intent(this@ProfilePengaturanPovAdmin, ProfileEditAkunPovAdmin::class.java)
            intent.putExtra("lastChangeName", intent.getStringExtra("lastChangeName"))
            intent.putExtra("lastChangeDomisili", intent.getStringExtra("lastChangeDomisili"))
            intent.putExtra("lastChangeNomorTelepon", intent.getStringExtra("lastChangeNomorTelepon"))
            startActivity(intent)
        }
    }
}









// Hapus shared preferences atau data lokal lainnya di sini
// (COBA CLEAR DATA LOKAL)
//                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.clear() // Hapus semua data
//                editor.apply()