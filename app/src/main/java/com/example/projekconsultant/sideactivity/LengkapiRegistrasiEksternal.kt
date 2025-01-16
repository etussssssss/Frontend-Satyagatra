package com.example.projekconsultant.sideactivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LengkapiRegistrasiEksternal : AppCompatActivity() {
    private var pekerjaan:String = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

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
        setContentView(R.layout.activity_lengkapi_registrasi_eksternal)
        baseAct()

        val rootView = findViewById<View>(R.id.main)
        addCloseKeyboard(rootView)
        auth = Firebase.auth
        db = Firebase.firestore

        // Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

//        findViewById<ImageButton>(R.id.whatsapp).setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send/?phone=62811803875&text&type=phone_number&app_absent=0&wame_ctl=1&fbclid=PAY2xjawHpcj1leHRuA2FlbQIxMAABpkg7FCarCgH0DNRyX_YH3gDLtB4lRkeh4MKMsaR49emAEkFIjpAidh5HIw_aem_NoWyvW6URsRE8K3jWr6qZA"))
//            startActivity(intent)
//        }


        val spinner: Spinner = findViewById(R.id.spinnereksternal)
        val items = arrayOf("PNS (Pegawai Negeri Sipil)", "TNI", "Karyawan Swasta", "Pengusaha", "Ibu Rumah Tangga (IRT)",
            "Pelajar (Mahasiswa)", "Dokter", "Dosen/Guru", "Lainnya", "Pilih Pekerjaan")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getCount(): Int {
                return super.getCount() // Mengurangi satu agar "Pilih status" tidak muncul di dropdown
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView

                val layoutParams = view.layoutParams
                layoutParams.height = 100 // set the height in pixels
                view.layoutParams = layoutParams
                view.setPadding(16, 16, 16, 16) // Set padding (left, top, right, bottom)
                view.maxHeight = 100

                if (position == items.size - 1) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                if (position == items.size - 1) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        spinner.adapter = adapter
        spinner.setSelection(items.size - 1)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items.size - 1) {
                    pekerjaan = "${items[position]}"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }

        val userData = intent.getStringArrayListExtra("userData")

        findViewById<Button>(R.id.save).setOnClickListener {
            findViewById<TextView>(R.id.warningtexteksternal).visibility = View.GONE

            if (pekerjaan.isNotEmpty() && userData != null) {
                val pekerjaanUser = "${pekerjaan}"

                Log.d("MSG", "Selected 1.1: ${userData.joinToString(", ")}")
                Log.d("MSG", "Selected 2: ${pekerjaanUser}")

                val intent = Intent(this@LengkapiRegistrasiEksternal, LengkapiResgistrasiTopikKonseling::class.java)
                intent.putExtra("iORe", "E")
                intent.putStringArrayListExtra("userData", userData)
                intent.putExtra("pekerjaanUserEksternal", pekerjaanUser)
                startActivity(intent)
            } else {
                findViewById<TextView>(R.id.warningtexteksternal).visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addCloseKeyboard(rootView: View){
        rootView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}






//Dialog Ingin Kembali
//override fun onBackPressed() {
//    val builder = AlertDialog.Builder(this)
//    builder.setMessage("Yakin membatalkan registrasi Anda?")
//        .setCancelable(false)
//        .setPositiveButton("Ya") { dialog, _ ->
//            dialog.dismiss()
//            finish()
//            super.onBackPressed()  // Melanjutkan aksi kembali
//        }
//        .setNegativeButton("Tidak") { dialog, _ ->
//            dialog.dismiss()  // Menutup dialog, tetap di halaman ini
//        }
//    val alert = builder.create()
//    alert.show()
//}
//
//private fun deleteUser() {
//    val currentUser = FirebaseAuth.getInstance().currentUser
//    val uid = currentUser?.uid ?: ""
//
//    if (uid.isNotEmpty()) {
//        FirebaseFirestore.getInstance().collection("users").document(uid).delete()
//            .addOnSuccessListener {
//                currentUser?.delete()?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        FirebaseAuth.getInstance().signOut()
//                        Log.d("DeleteUser", "User berhasil dihapus dari FirebaseAuth dan Firestore")
//                    } else {
//                        Log.e("DeleteUser", "Gagal menghapus user dari FirebaseAuth", task.exception)
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("DeleteUser", "Gagal menghapus dokumen di Firestore", e)
//            }
//    } else {
//        Log.e("DeleteUser", "User tidak valid atau tidak ada user yang login")
//    }
//}