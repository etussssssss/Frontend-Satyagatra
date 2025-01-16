package com.example.projekconsultant

import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.projekconsultant.myfragment.Home
import com.example.projekconsultant.myfragment.Penjadwalan
import com.example.projekconsultant.myfragment.Profile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : BaseActivity() {
    private  var namaprofile:String? = "Anonymous"
    private  var gmailprofile:String? = "Anonymous@gmail.com"
    private  var fotoprofile:String? = ""
    private lateinit var getUID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, v.paddingTop, systemBars.right, v.paddingBottom)
            insets
        }

        // Inisialisasi Firestore
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance().currentUser

        if (auth != null) {
            db.collection("users").document(auth.uid).get().addOnSuccessListener { data ->
                namaprofile = data.getString("nama")
                gmailprofile = data.getString("email")
                fotoprofile = data.getString("profile")
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)

        val fhome = Home()
        replaceFragment(fhome)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(fhome)
                    true
                }
                R.id.profil -> {
                    val fragment = Profile.newInstance("${namaprofile}", "${gmailprofile}", "${fotoprofile}")
                        replaceFragment(fragment)
                    true
                }
                R.id.checked -> {
                    val fragment = Penjadwalan()
                    replaceFragment(fragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}

//                    Handler().postDelayed({
//                    }, 100)