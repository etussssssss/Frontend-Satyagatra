package com.example.projekconsultant

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.projekconsultant.admin.MainAdminActivity
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.example.projekconsultant.sideactivity.LengkapiRegistrasi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class FirstProgress : BaseActivity() {
    // Membuat variable lateinit firebase firestore.
    private lateinit var db: FirebaseFirestore

    // Method yang berfungsi membuat tampilan GreenScreen.
    private fun startCircularRevealAnimation() {
        val animationOverlay = findViewById<View>(R.id.animation_overlay)
        // Fungsi untuk menampilkan Animasi GreenScreen ketika awal membuka aplikasi.
        animationOverlay.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                animationOverlay.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val centerX = animationOverlay.width / 2
                val centerY = animationOverlay.height / 2
                val endRadius = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()
                val revealAnimation = ViewAnimationUtils.createCircularReveal(
                    animationOverlay, centerX, centerY, 0f, endRadius
                )
                revealAnimation.duration = 1000
                revealAnimation.start()
                revealAnimation.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        // Tidak melakukan apa-apa
                    }
                    override fun onAnimationEnd(animation: Animator) {
                        // Setelah animasi circular reveal selesai, mulai animasi fade out
                        val fadeOut = ObjectAnimator.ofFloat(animationOverlay, "alpha", 1f, 0f)
                        fadeOut.duration = 500 // Durasi untuk fade out
                        fadeOut.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationEnd(animation: Animator) {
                                animationOverlay.visibility = View.GONE
                                findViewById<ConstraintLayout>(R.id.main).setBackgroundColor(ContextCompat.getColor(this@FirstProgress, R.color.white))
                                findViewById<ImageView>(R.id.YARSIconsultant).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.textView2223).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.textView22).visibility = View.VISIBLE
                            }
                            override fun onAnimationStart(animation: Animator) {
                                // Tidak melakukan apa-apa
                            }
                            override fun onAnimationCancel(animation: Animator) {
                                // Tidak melakukan apa-apa
                            }
                            override fun onAnimationRepeat(animation: Animator) {
                                // Tidak melakukan apa-apa
                            }
                        })
                        fadeOut.start()
                    }
                    override fun onAnimationCancel(animation: Animator) {
                        // Tidak melakukan apa-apa
                    }
                    override fun onAnimationRepeat(animation: Animator) {
                        // Tidak melakukan apa-apa
                    }
                })
            }
        })
    }

    // Fungsi Untuk pengecekan internet di front end.
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private fun checkNetworkAndProceed() {
        if (networkChangeReceiver == null) {
            networkChangeReceiver = NetworkChangeReceiver { isConnected ->
                if (!isConnected) {
                    startActivity(Intent(this, NoInternet::class.java))
                } else {
                    handleUserAuthentication()
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    private fun handleUserAuthentication() {
        // Mendapatkan pengguna yang saat ini sedang masuk menggunakan Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Jika pengguna sudah masuk, ambil UID pengguna dan periksa peran pengguna
            val userId = currentUser.uid
            checkUserRole(userId)
        } else {
            // Jika tidak ada pengguna yang masuk, lakukan sign out untuk memastikan status bersih
            FirebaseAuth.getInstance().signOut()
            // Navigasikan pengguna ke layar "Get Started" (layar awal aplikasi)
            val intent = Intent(this, GetStarted::class.java)
            startActivity(intent)
            finish()// Tutup aktivitas saat ini agar tidak bisa kembali menggunakan tombol back
        }
    }


    // Pengecekan real time lewat onResume
    override fun onResume() {
        super.onResume()
        // Register BroadcastReceiver hanya jika sudah diinisialisasi
        networkChangeReceiver?.let {
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(it, intentFilter)
        }
    }

    // Pengecekan real time lewat onPause
    override fun onPause() {
        super.onPause()
        // Unregister BroadcastReceiver hanya jika sudah diinisialisasi
        networkChangeReceiver?.let {
            unregisterReceiver(it)
        }
    }

    //Class OnCreat Class Ini (FirstProgress)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_progress)
        // Inisialisasi Database firestore.
        db = Firebase.firestore
        // Memulai Animasi GreenScreen.
        startCircularRevealAnimation()

        // Mengatur waktu tampilan.
        Handler().postDelayed({
            checkNetworkAndProceed()
        }, 5000)
    }

    // uuids My Users untuk pendaftaran.
    private lateinit var uid:String
    // Method yang berfungsi untuk pengecekan role (lewat Front End).
    // Method ini juga berfungsi apakah data si user sudah lengkap ketika registrasi.
    fun checkUserRole(userId: String) {
        val userRef = db.collection("users").document(userId)
        // Jika ada di firestore data user akan melanjutkan pengecekan role.
        userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("typeroleuid")
                    uid = document.getString("uid").toString()
                    handleUserRole(userId, role)
                } else {
                    // Data user tidak ada.
                    handleInvalidUser(userId)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error mendapatkan data users: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Pengecekan Role : admin atau user biasa.
    private fun handleUserRole(userId: String, role: String?) {
        if (role == null) {
            handleInvalidUser(userId)
            return
        }

        when (role) {
            "admin" -> {
                Log.d("First Progress", "Berhasil Role Admin")
                ActivityAdmin()
            }
            "user" -> {
                Log.d("First Progress", "Berhasil Role User")
                checkUserData(userId)
            }
            else -> {
                handleInvalidUser(userId)
            }
        }
    }

    // Pengecekan data, pengambilan data sebenar nya bisa lewat First Progress ini. tetapi perlu authen
    private fun checkUserData(userId: String) {
        val dataRef = db.collection("datas").document(userId)

        dataRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Ke Home untuk user
                    ActivityUser()
                } else {
                    handleInvalidUser(userId)
                }
            }.addOnFailureListener { exception ->
                Log.e("Error mendapatkan data datas:", "${exception.message}")
                Toast.makeText(this, "Coba Lagi Nanti: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Ini akan di arahkan ke Pemulaian atau Activity Class GetStarted.
    private fun handleInvalidUser(userId: String) {
        // Setelah itu akun email user akan terhapus secara otomatis lewat front end.
        deleteUser(userId)
        val intent = Intent(this, GetStarted::class.java)
        startActivity(intent)
        finish()
    }


    // Method penghapusan email user yang tidak ada data di firestore/
    private fun deleteUser(userUUID:String ){
        FirebaseFirestore.getInstance().collection("users").document(userUUID).delete().addOnSuccessListener {
            FirebaseAuth.getInstance().currentUser?.delete()
            FirebaseAuth.getInstance().signOut()
        }
    }

    // Class Activity Admin
    private fun ActivityAdmin(){
        val intent = Intent(this@FirstProgress, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Class Activity User
    private fun ActivityUser(){
        val intent = Intent(this@FirstProgress, MainActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }
}

















//    fun checkUserRole(userId: String) {
//        val userRef = db.collection("users").document(userId)
//        userRef.get().addOnSuccessListener { document ->
//
//            if (document != null && document.exists()) {
//                val role = document.getString("typeroleuid")
//                if (role != null) {
//                    Log.d("First Progress", "Berhasil Role")
//
//                    if (role == "admin") {
//                        // User adalah admin
//                        ActivityAdmin()
//                    } else if (role == "user") {
//
//                        val dataRef = db.collection("datas").document(userId)
//                        dataRef.get().addOnSuccessListener { document ->
//                            if (document != null && document.exists()) {
//                                //Ke Home
//                                ActivityUser()
//                            } else {
//
//                                deleteUser(userId)
//                                intent = Intent(this, GetStarted::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//
//                    }
//                }
//            } else {
//                deleteUser(userId)
//                intent = Intent(this, GetStarted::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }.addOnFailureListener { exception ->
//            Toast.makeText(this, "Error mendapatkan data users: ${exception.message}", Toast.LENGTH_LONG).show()
//        }
//    }





//Ke register melengkapi data karena tidak ada data nya
//                                ActivityLengkapiRegister()



//        Handler().postDelayed({
//            var intent : Intent
//            if (FirebaseAuth.getInstance().currentUser != null) {
//                val userId = FirebaseAuth.getInstance().currentUser?.uid
//                checkUserRole(userId.toString())
//            } else{
////                ActivityLengkapiRegistrasiTester()
//
//                FirebaseAuth.getInstance().signOut()
//                intent = Intent(this, GetStarted::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }, 5000)







//                val userId = FirebaseAuth.getInstance().currentUser?.uid
//                FirebaseAuth.getInstance().currentUser?.let { user ->
//                    if (user.isEmailVerified) {
//                        checkUserRole(userId.toString())
//                    } else {
//                        //Email tidak terverifikasi
//                        // Tampilkan pesan bahwa email belum diverifikasi
//                        FirebaseAuth.getInstance().currentUser?.delete()
//                        FirebaseAuth.getInstance().signOut()
//
//                        intent = Intent(this, GetStarted::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }