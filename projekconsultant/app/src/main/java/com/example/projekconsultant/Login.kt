package com.example.projekconsultant

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.admin.MainAdminActivity
import com.example.projekconsultant.methods.ClassOfFunc
import com.example.projekconsultant.mymainactivity.LengkapiResgistrasi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Login : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        db = Firebase.firestore

        val enter: Button = findViewById(R.id.Enter)
        val username: EditText = findViewById(R.id.Email)
        val password: EditText = findViewById(R.id.Password)

        ClassOfFunc().setupPasswordToggle(password)
        enter.setOnClickListener {
            if (username.text.toString().isNotEmpty() &&  password.text.toString().isNotEmpty()) {
                loginUser(username.text.toString(), password.text.toString())
            }
        }

        val buatAkunTextView = findViewById<TextView>(R.id.buatakun)
        // Span String font (Tag <u>)
        val spannable = SpannableString(buatAkunTextView.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Buka activity registrasi (RegisterActivity)
                val intent = Intent(this@Login, Registrasi::class.java)
                startActivity(intent)
            }
        }
        spannable.setSpan(clickableSpan, 19, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        buatAkunTextView.text = spannable
        buatAkunTextView.movementMethod = LinkMovementMethod.getInstance()

        //Link Movement Tag <a>
        val privacyTextView = findViewById<TextView>(R.id.desckebijakanprivasi)
        privacyTextView.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun ActivityAdmin(){
        val intent = Intent(this@Login, MainAdminActivity::class.java)
        startActivity(intent)
    }

    private fun ActivityUser(){
        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
    }


    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login sukses
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        checkUserRole(userId)
                    }
                } else {
                    // Login gagal
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
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
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error getting user data: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }


}