package com.example.projekconsultant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.projekconsultant.data.UserData
import com.example.projekconsultant.methods.ClassOfFunc
import com.example.projekconsultant.mymainactivity.LengkapiResgistrasi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception


class Registrasi : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var passw: EditText
    private lateinit var confirmpassw: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        auth = Firebase.auth

        username = findViewById(R.id.regisusername)
        email = findViewById(R.id.regisemail)
        passw = findViewById(R.id.regispassword)
        confirmpassw =  findViewById(R.id.regisconfpassword)

        // Membuat daftar dari show/hide EditText password
        for (editText in listOf(passw, confirmpassw)) { ClassOfFunc().setupPasswordToggle(editText) }

        findViewById<Button>(R.id.enteregister).setOnClickListener {
            var myusername = username.text.toString()
            var myemail = email.text.toString()
            var mypassw = passw.text.toString()
            var myconfpassw = confirmpassw.text.toString()

            if (myusername.isNotEmpty() && myemail.isNotEmpty() && mypassw.isNotEmpty() && myconfpassw.isNotEmpty()) {
                if (mypassw.equals(myconfpassw)){
//                    auth.createUserWithEmailAndPassword(myemail, mypassw).addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
                            if (true) {
                                val intent = Intent(this@Registrasi, LengkapiResgistrasi::class.java)
//                                intent.putExtra("username", myusername)
//                                intent.putExtra("email", myemail)
//                                intent.putExtra("password", mypassw)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this, "Kamu gagal membuat akun", Toast.LENGTH_SHORT).show()
                            }
//                    }

                } else {
                    Toast.makeText(this, "Password nya belum sama nih", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Tolong lengkapi semua nya hehe", Toast.LENGTH_SHORT).show()
            }
        }

        val privacyTextView = findViewById<TextView>(R.id.desckebijakanprivasi)
        privacyTextView.movementMethod = LinkMovementMethod.getInstance()
    }


    fun registerUser(nama: String,email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            if (task.isSuccessful) {
                if (user != null) {
                    val uid = user.uid

                    val dataUsers = UserData(
                        typeroleuid = "user",
                        nama = nama,
                        email = email,
                        password = password
                    )

                    db.collection("users").document(uid).set(dataUsers).addOnSuccessListener {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}