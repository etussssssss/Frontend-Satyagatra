package com.example.projekconsultant.sideactivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class LupaPassword : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var warning:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lupa_password)

        auth = Firebase.auth
        db = Firebase.firestore
        warning = findViewById(R.id.textView20)

        if(intent.getStringExtra("ubahpwMainActivity") != null){
            findViewById<TextView>(R.id.textslupapwbold).text = intent.getStringExtra("ubahpwMainActivity")
            findViewById<TextView>(R.id.textView3).text = intent.getStringExtra("ubahpwMainActivity")
        }

        findViewById<Button>(R.id.verifLupapw).setOnClickListener {
            warning.visibility = View.GONE
            val email = findViewById<EditText>(R.id.emailLupaPassW).text.toString().trim()

            if (email.isEmpty()) {
                val dialogEmailNotFound = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Tolong Isi Dengan Benar"

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogEmailNotFound.show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val dialogFormatEmailNotValid = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Format email tidak valid"

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogFormatEmailNotValid.show()
            } else {
                // Bergasil
                sendPasswordResetEmail(email)
            }
        }

        findViewById<ImageView>(R.id.backpengaturan).setOnClickListener {
            finish()
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findViewById<EditText>(R.id.emailLupaPassW).text.clear()
                warning.visibility = View.VISIBLE
            } else {
                val dialogEmailNotFound = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Coba Lagi Nanti yaa, mohon di jeda"

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogEmailNotFound.show()

                Log.e("ERROR", task.exception?.message ?: "Gagal mengirim email reset password")
            }
        }
    }

    fun showCustomDialog(context: Context, layoutId: Int, backgroundDrawableId: Int,
                         onDialogViewReady: (view: View, dialog: AlertDialog) -> Unit): AlertDialog {
        // Membuat builder dialog
        val builder = AlertDialog.Builder(context)
        // Inflasi layout dialog
        val view = LayoutInflater.from(context).inflate(layoutId, null)
        // Pasang layout ke dialog
        builder.setView(view)

        // Buat dialog
        val dialog = builder.create()

        // Mengatur ukuran wrap_content untuk dialog
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Set background untuk dialog
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, backgroundDrawableId))
        dialog.setCancelable(false) // Dialog dapat ditutup dengan back atau klik luar

        // Callback untuk memberikan akses ke view dialog dan dialog itu sendiri
        onDialogViewReady(view, dialog)

        return dialog
    }
}