package com.example.projekconsultant.sideactivity

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projekconsultant.MainActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.Registrasi
import com.example.projekconsultant.data.UserDataLengkapiRegistrasi
import com.example.projekconsultant.data.UserEksternal
import com.example.projekconsultant.data.UserInternal
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LengkapiResgistrasiTopikKonseling : AppCompatActivity() {
    private var topics:String = "none"

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
    private fun baseAct(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            //window.navigationBarColor = Color.TRANSPARENT

            // Mengatur ikon status bar menjadi gelap jika background terang
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lengkapi_resgistrasi_topik_konseling)
        baseAct()

        //Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        val depresi = findViewById<TextView>(R.id.depresi)
        val kecemasan = findViewById<TextView>(R.id.kecemasan)
        val percintaan = findViewById<TextView>(R.id.percintaan)

        val keluarga = findViewById<TextView>(R.id.keluarga)
        val trauma = findViewById<TextView>(R.id.trauma)
        val pekerjaan = findViewById<TextView>(R.id.pekerjaan)

        val kepribadian = findViewById<TextView>(R.id.kepribadian)
        val kendaliemosi = findViewById<TextView>(R.id.kendaliemosi)
        val kecanduan = findViewById<TextView>(R.id.kecanduan)

        val stress = findViewById<TextView>(R.id.stres)
        val pengembangandiri = findViewById<TextView>(R.id.pengembangandiri)

        val a = arrayListOf(depresi, kecemasan, percintaan, keluarga, trauma, pekerjaan, kepribadian, kendaliemosi, kecanduan, stress, pengembangandiri)
        for (btn in a){ select(btn) }

        val internalOReksternal = intent.getStringExtra("iORe")
        val userData = intent.getStringArrayListExtra("userData")

        val save = findViewById<Button>(R.id.save)
        val eksplor = findViewById<TextView>(R.id.eksplorsendiri)

        save.setOnClickListener {
            topics = "${selectTopik.joinToString(", ")}"

            if(internalOReksternal != null && userData != null && topics.isNotEmpty()){
                if (internalOReksternal.equals("I")) {
                    val ur = intent.getStringExtra("userRole")
                    val uf = intent.getStringExtra("userFakultas")
                    val userBagianTendik = intent.getStringExtra("userBagianTendik")
                    val ubt = userBagianTendik ?: "none"

                    if (ur != null && uf != null) {
                        inputInternal(topics, userData, ur, uf, ubt)
                    }

                } else if (internalOReksternal.equals("E")) {
                    val userEksternal = intent.getStringExtra("pekerjaanUserEksternal")

                    if(userEksternal != null){
                        inputEksternal(topics, userData, userEksternal)
                    }
                }
            } else {
                findViewById<TextView>(R.id.warningtexttopik).visibility = View.VISIBLE
            }
        }

        //Tombol Eksplorasi Sendiri
        eksplor.setOnClickListener {
            findViewById<TextView>(R.id.warningtexttopik).visibility = View.GONE
            topics = "${selectTopik.joinToString(", ")}"

            if(internalOReksternal != null && userData != null){
                if (internalOReksternal.equals("I")) {
                    val ur = intent.getStringExtra("userRole")
                    val uf = intent.getStringExtra("userFakultas")
                    val userBagianTendik = intent.getStringExtra("userBagianTendik")
                    val ubt = userBagianTendik ?: "none"

                    if (ur != null && uf != null) {
                        inputInternal(topics, userData, ur, uf, ubt)
                    }

                } else if (internalOReksternal.equals("E")) {
                    val userEksternal = intent.getStringExtra("pekerjaanUserEksternal")

                    if(userEksternal != null){
                        inputEksternal(topics, userData, userEksternal)
                    }
                }
            }
        }
    }

    private fun inputInternal(topik: String, userData: ArrayList<String>, ur: String, uf: String, ubt: String) {
        val dataUsers = UserDataLengkapiRegistrasi(
            type = "I",
            nama = userData[0],
            gender = userData[1],
            domisili = userData[2],
            nomortelepon = userData[3],
            tanggalLahir = userData[4],
            status = userData[5],
            selectTopik = topik,
        )

        val internalUser = UserInternal(
            roleinternal = ur,
            fakultas = uf,
            bagiantendik = ubt,
        )

        Log.d("SUCCESS", "${dataUsers}, ${internalUser}")
        val intent = Intent(this@LengkapiResgistrasiTopikKonseling, Registrasi::class.java)
        intent.putExtra("DATAUSERS", dataUsers)
        intent.putExtra("INTERNAL", internalUser)
        startActivity(intent)
    }

    private fun inputEksternal(topik:String ,userData: ArrayList<String>, pekerjaanUser: String) {
        val dataUsers = UserDataLengkapiRegistrasi(
            type = "E",
            nama = userData[0],
            gender = userData[1],
            domisili = userData[2],
            nomortelepon = userData[3],
            tanggalLahir = userData[4],
            status = userData[5],
            selectTopik = topik,
        )

        val eksternalUser = UserEksternal(
            pekerjaan = pekerjaanUser
        )

        Log.d("SUCCESS", "${dataUsers}, ${eksternalUser}")
        val intent = Intent(this@LengkapiResgistrasiTopikKonseling, Registrasi::class.java)
        intent.putExtra("DATAUSERS", dataUsers)
        intent.putExtra("EKSTERNAL", eksternalUser)
        startActivity(intent)
    }

    private var select3: Int = 0
    private val selectTopik = ArrayList<String>()
    fun select(btn: TextView) {
        btn.setOnClickListener {
            if (btn.isSelected) {

                selectTopik.remove(btn.text.toString())
                select3--
                btn.isSelected = false // Deselect
            } else if (select3 < 3) {

                selectTopik.add(btn.text.toString())
                select3++
                btn.isSelected = true // Select
            }
        }
    }
}










//private fun inputInternal(topik: String, userData: ArrayList<String>, ur: String, uf: String, ubt: String, uid: String) {
//    val dataUsers = UserDataLengkapiRegistrasi(
//        type = "I",
//        nama = userData[0],
//        gender = userData[1],
//        domisili = userData[2],
//        nomortelepon = userData[3],
//        tanggalLahir = userData[4],
//        umur = userData[5],
//        status = userData[6],
//        selectTopik = topik,
//    )
//
//    val internalUser = UserInternal(
//        roleinternal = ur,
//        fakultas = uf,
//        bagiantendik = ubt,
//    )
//
//    val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
//    val dialog = builder.create()
//    dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
//    dialog.setCancelable(false)
//
//    // Variabel untuk melacak status
//    var saveCount = 0
//
//    // Fungsi untuk memeriksa status simpan
//    fun checkAllDataSaved() {
//        if (saveCount == 2) { // Jika kedua operasi selesai
//            dialog.show()
//            dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
//                val intent = Intent(this@LengkapiResgistrasiTopikKonseling, MainActivity::class.java)
//                startActivity(intent)
//                dialog.dismiss()
//                finish()
//            }
//        }
//    }
//
//    // Menyimpan data pertama
//    db.collection("datas").document(uid).set(dataUsers)
//        .addOnSuccessListener {
//            saveCount++
//            checkAllDataSaved() // Cek setelah sukses
//        }
//        .addOnFailureListener { e ->
//            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//    // Menyimpan data kedua
//    db.collection("internals").document(uid).set(internalUser)
//        .addOnSuccessListener {
//            saveCount++
//            checkAllDataSaved() // Cek setelah sukses
//        }
//        .addOnFailureListener { e ->
//            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//}
//
//private fun inputEksternal(topik:String ,userData: ArrayList<String>, pekerjaanUser: String, uid:String) {
//
//    val dataUsers = UserDataLengkapiRegistrasi(
//        type = "E",
//        nama = userData[0],
//        gender = userData[1],
//        domisili = userData[2],
//        nomortelepon = userData[3],
//        tanggalLahir = userData[4],
//        umur = userData[5],
//        status = userData[6],
//        selectTopik = topik,
//    )
//
//    val eksternalUser = UserEksternal(
//        pekerjaan = pekerjaanUser
//    )
//
//    val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
//    val dialog = builder.create()
//    dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
//    dialog.setCancelable(false)
//
//    var saveCount = 0
//
//    fun checkAllDataSaved() {
//        if (saveCount == 2) { // Jika kedua operasi selesai
//            dialog.show()
//            dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
//                val intent = Intent(this@LengkapiResgistrasiTopikKonseling, MainActivity::class.java)
//                startActivity(intent)
//                dialog.dismiss()
//                finish()
//            }
//        }
//    }
//
//    db.collection("datas").document(uid).set(dataUsers).addOnSuccessListener {
//        saveCount++
//        checkAllDataSaved()
//    }.addOnFailureListener { e ->
//        Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//    }
//
//    db.collection("eksternals").document(uid).set(eksternalUser).addOnSuccessListener {
//        saveCount++
//        checkAllDataSaved()
//    }.addOnFailureListener { e ->
//        Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//    }
//}




















//        val dataUsers = UserDataLengkapiRegistrasi(
//            type = "E",
//            nama = userData[0],
//            gender = userData[1],
//            domisili = userData[2],
//            nomortelepon = userData[3],
//            umur = userData[4],
//            status = userData[5],
//            selectTopik = topik,
//        )






//    private fun inputInternal(topik:String ,userData: ArrayList<String>, ur: String, uf: String, ubt: String, uid:String) {
//        val dataUsers = UserDataLengkapiRegistrasi(
//            type = "I",
//            nama = userData[0],
//            gender = userData[1],
//            domisili = userData[2],
//            nomortelepon = userData[3],
//            tanggalLahir = userData[4],
//            umur = userData[5],
//            status = userData[6],
//            selectTopik = topik,
//        )
//
//        db.collection("datas").document(uid).set(dataUsers).addOnSuccessListener {
//            succes1 = true
//        }.addOnFailureListener { e ->
//            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//        val internalUser = UserInternal(
//            roleinternal = ur,
//            fakultas = uf,
//            bagiantendik = ubt,
//        )
//
//        db.collection("internals").document(uid).set(internalUser).addOnSuccessListener {
//            succes2 = true
//        }.addOnFailureListener { e ->
//            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//
//        val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
//        val dialog = builder.create()
//        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
//        dialog.setCancelable(false)
//
//        if(succes1 && succes2){
//            Toast.makeText(this, "Failed to save user data: ${succes1}, ${succes2}", Toast.LENGTH_SHORT).show()
//            dialog.show()
//            dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
//                val intent = Intent(this@LengkapiResgistrasiTopikKonseling, MainActivity::class.java)
//                startActivity(intent)
//                dialog.dismiss()
//                finish()
//            }
//        }
//    }



//    private fun inputInternal(topik: String, userData: ArrayList<String>, ur: String, uf: String, ubt: String, uid: String) {
//        val dataUsers = UserDataLengkapiRegistrasi(
//            type = "I",
//            nama = userData[0],
//            gender = userData[1],
//            domisili = userData[2],
//            nomortelepon = userData[3],
//            tanggalLahir = userData[4],
//            umur = userData[5],
//            status = userData[6],
//            selectTopik = topik,
//        )
//
//        val internalUser = UserInternal(
//            roleinternal = ur,
//            fakultas = uf,
//            bagiantendik = ubt,
//        )
//
//        val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
//        val dialog = builder.create()
//        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
//        dialog.setCancelable(false)
//
//        // Variabel untuk melacak status
//        var saveCount = 0
//
//        // Menyimpan data pertama
//        db.collection("datas").document(uid).set(dataUsers).addOnSuccessListener {
//                saveCount++
////                checkAllDataSaved(saveCount, dialog)
//        }.addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//        // Menyimpan data kedua
//        db.collection("internals").document(uid).set(internalUser).addOnSuccessListener {
//                saveCount++
////                checkAllDataSaved(saveCount, dialog)
//        }.addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//        checkAllDataSaved(saveCount, dialog)
//    }
//
//    // Fungsi untuk mengecek apakah semua data berhasil disimpan
//    private fun checkAllDataSaved(saveCount: Int, dialog: AlertDialog) {
//        if (saveCount == 2) { // Jika kedua operasi selesai
//            dialog.show()
//            dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
//                val intent = Intent(this@LengkapiResgistrasiTopikKonseling, MainActivity::class.java)
//                startActivity(intent)
//                dialog.dismiss()
//                finish()
//            }
//        }
//    }