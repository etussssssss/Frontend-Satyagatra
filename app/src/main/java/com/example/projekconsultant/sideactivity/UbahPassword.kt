package com.example.projekconsultant.sideactivity

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.auth.FirebaseAuth

class UbahPassword : BaseActivity() {
    private lateinit var auth: FirebaseAuth

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
        setContentView(R.layout.activity_ubah_password)

        auth = FirebaseAuth.getInstance()

        //Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        val newPasswordEditText = findViewById<EditText>(R.id.UbahPassW)
        val confNewPasswordEditText = findViewById<EditText>(R.id.ConfUbahPassW)
        val resetPasswordButton = findViewById<Button>(R.id.Enter)

        // Mendapatkan data dari Intent
        val intentData = intent.data
        val oobCode = intentData?.getQueryParameter("oobCode") // Ambil token dari URL

        resetPasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confNewPasswordEditText = confNewPasswordEditText.text.toString()

            // Pastikan token tidak null
            if (oobCode != null) {

                if(newPassword == confNewPasswordEditText){

                    auth.confirmPasswordReset(oobCode, newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password berhasil direset!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Gagal mereset password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }else {
                    Toast.makeText(this, "Password Belum Sama", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Token tidak valid.", Toast.LENGTH_SHORT).show()
            }
        }


    }
}