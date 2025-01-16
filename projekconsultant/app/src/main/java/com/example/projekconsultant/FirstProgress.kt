package com.example.projekconsultant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.admin.MainAdminActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FirstProgress : BaseActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_progress)

        db = Firebase.firestore

        Handler().postDelayed({
            var intent : Intent
            if (FirebaseAuth.getInstance().currentUser != null) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
//                checkUserRole(userId.toString())
            } else{
                FirebaseAuth.getInstance().signOut()
                intent = Intent(this, GetStarted::class.java)
                startActivity(intent)
                finish()
            }
        }, 5000)
    }

    fun checkUserRole(userId: String) {
        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val role = document.getString("typeroleuid")
                if (role != null) {
                    if (role == "admin") {
                        // User adalah admin
                        ActivityAdmin()
                    } else if (role == "user") {
                        // User adalah user biasa
                        ActivityUser()
                    }
                    finish()
                }
            } else {
                Toast.makeText(this, "User belum terdaftar Di database", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error mendapatkan data user: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun ActivityAdmin(){
        val intent = Intent(this@FirstProgress, MainAdminActivity::class.java)
        startActivity(intent)
    }

    private fun ActivityUser(){
        val intent = Intent(this@FirstProgress, MainActivity::class.java)
        startActivity(intent)
    }
}