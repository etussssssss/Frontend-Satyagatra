package com.example.projekconsultant.mymainactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.Login
import com.example.projekconsultant.R
import com.google.firebase.auth.FirebaseAuth


//AppCompatActivity
class Pengaturan : BaseActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Membantu Responsiv Layar EnableEdge dan ViewCompat.SetOnAppluWindow
        enableEdgeToEdge()
        setContentView(R.layout.activity_pengaturan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
            Toast.makeText(this, "LOGOUT???", Toast.LENGTH_SHORT).show()
            dialog.show() // Tampilkan dialog ketika layout diklik

            dialog.findViewById<Button>(R.id.btn_no)?.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this, "Ga Jadi HEHE", Toast.LENGTH_SHORT).show()
            }

            dialog.findViewById<Button>(R.id.btn_yes)?.setOnClickListener {
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()

                FirebaseAuth.getInstance().signOut()
                // Hapus shared preferences atau data lokal lainnya di sini
                // (COBA CLEAR DATA LOKAL)
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear() // Hapus semua data
                editor.apply()

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
    }
}
