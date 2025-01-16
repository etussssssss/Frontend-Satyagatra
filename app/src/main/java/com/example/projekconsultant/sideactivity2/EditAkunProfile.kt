package com.example.projekconsultant.sideactivity2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.MainActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditAkunProfile : BaseActivity() {
//    private lateinit var etName: EditText

    private lateinit var etName: TextView
    private lateinit var etGender: TextView
    private lateinit var etDomisili: Spinner
    private lateinit var etNomor: EditText
    private lateinit var etTanggalLahir: TextView
    private lateinit var etStatusHubungan: Spinner

    private  var cekName:String = ""
    private  var cekGender:String = ""
    private  var cekDomisili:String = ""
    private  var cekNomor:String = ""
    private  var cekTanggalLahir:String = ""
    private  var cekStatusHubungan:String = ""

    private val items = arrayOf("Single", "Pacaran", "Menikah", "Lainnya", "Pilih status")
    private val items2 = arrayOf(
        "Aceh","Sumatera Barat", "Riau", "Kepulauan Riau", "Jambi", "Sumatera Selatan", "Bangka Belitung", "Bengkulu", "Lampung",
        "DKI Jakarta", "Jawa Barat", "Banten", "Jawa Tengah", "DI Yogyakarta", "Jawa Timur", "Bali", "Nusa Tenggara Barat", "Nusa Tenggara Timur",
        "Kalimantan Barat","Kalimantan Tengah", "Kalimantan Selatan", "Kalimantan Timur", "Kalimantan Utara", "Sulawesi Utara", "Gorontalo",
        "Sulawesi Tengah", "Sulawesi Barat", "Sulawesi Selatan", "Sulawesi Tenggara", "Maluku", "Maluku Utara",
        "Papua", "Papua Barat", "Papua Selatan", "Papua Tengah", "Papua Pegunungan",
        "Papua Barat Daya", "Sumatera Utara", "Lainnya",
        )


    private lateinit var btnSave: Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
        setContentView(R.layout.activity_edit_akun_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addCloseKeyboard(findViewById<LinearLayout>(R.id.main))
        addCloseKeyboard(findViewById<ScrollView>(R.id.scedit))

        etName = findViewById(R.id.nama)
        etGender = findViewById(R.id.gender)
        etDomisili = findViewById(R.id.domisiliKm)
        etNomor = findViewById(R.id.nomortelepon)
        etTanggalLahir = findViewById(R.id.tanggalLahir)
        etStatusHubungan = findViewById(R.id.spinner)

        //Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        val adapter1 = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2) {
            override fun getCount(): Int {
                return super.getCount() - 1
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == items2.size - 1) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                if (position == items2.size - 1) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        etDomisili.adapter = adapter1
        etDomisili.setSelection(items2.size - 1)
        addCloseKeyboard(etDomisili)
        etDomisili.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items2.size - 1) {

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // -----------------------------
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getCount(): Int {
                return super.getCount() - 1
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
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

        etStatusHubungan.adapter = adapter
        etStatusHubungan.setSelection(items.size - 1)
        addCloseKeyboard(etStatusHubungan)
        etStatusHubungan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items.size - 1) {

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        etStatusHubungan.isEnabled = false

        loadProfile()
        findViewById<Button>(R.id.btnSimpan).setOnClickListener {
            findViewById<TextView>(R.id.warning).visibility = View.GONE
            val selecetedDomisili = etDomisili.selectedItem.toString()

            if("${etName.text}".equals("${cekName}")  &&  "${selecetedDomisili}".equals("${cekDomisili}") &&  "${etNomor.text}".equals("${cekNomor}")){
                Log.d("HASIL", "${cekName},  ${cekDomisili}, ${cekNomor}")
            }else if (etName.text.isNotEmpty() && selecetedDomisili.isNotEmpty() && etNomor.text.isNotEmpty()){
                saveProfile(
                    "${etName.text}",
                    "${etGender.text}",
                    "${selecetedDomisili}",
                    "${etNomor.text}",
                    "${etTanggalLahir.text}",
                    "${etStatusHubungan}"
                )
            }else {
                findViewById<TextView>(R.id.warning).visibility = View.VISIBLE
                Log.d("HASIL", "${etName.text}, ${selecetedDomisili},  ${etNomor.text}")
            }
        }

        findViewById<ImageView>(R.id.backtoViewHome).setOnClickListener {
            finish()
        }
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("datas").document(userId).get().addOnSuccessListener { document ->
            etName.setText(document.getString("nama"))
            cekName = "${document.getString("nama")}"
            
            etGender.text = document.getString("gender")
            cekGender = "${document.getString("gender")}"

            val selectedIndexDom = items2.indexOf(document.getString("domisili"))
            etDomisili.setSelection(if (selectedIndexDom >= 0) selectedIndexDom else items2.size - 1)
            cekDomisili = "${document.getString("domisili")}"

            val nomorTelepon = document.getString("nomortelepon")
            if (!nomorTelepon.isNullOrBlank() && nomorTelepon.length > 2) {
                etNomor.setText(nomorTelepon.substring(2))
                cekNomor = "${document.getString("nomortelepon")}".substring(2)
            } else {
                etNomor.setText("") // Atur default atau biarkan kosong
            }
            etTanggalLahir.text =  document.getString("tanggalLahir")
            cekTanggalLahir = "${document.getString("tanggalLahir")}"

            val selectedIndex = items.indexOf(document.getString("status"))
            etStatusHubungan.setSelection(if (selectedIndex >= 0) selectedIndex else items2.size - 1)
            cekStatusHubungan = "${document.getString("status")}"


        }.addOnFailureListener {
            Toast.makeText(this, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile(mname: String, mgender: String, mdomisili: String, mnomortelepon: String, mtanggallahir: String, mstatus: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = hashMapOf<String, Any>()

        // Tambahkan hanya field yang diubah
        if (mname.isNotEmpty()) {
            updates["nama"] = mname
        }
        if (mdomisili.isNotEmpty()) {
            updates["domisili"] = mdomisili
        }

        if (mnomortelepon.isNotEmpty()) {
            updates["nomortelepon"] = "62${mnomortelepon}"
        }

        db.collection("datas").document(userId).update(updates).addOnSuccessListener {
            Toast.makeText(this, "Profil berhasil disimpan.", Toast.LENGTH_SHORT).show()
            // Kirim hasil kembali ke activity
            val intent = Intent(this@EditAkunProfile, MainActivity::class.java)
            intent.putExtra("updatedName", mname)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menyimpan profil.", Toast.LENGTH_SHORT).show()
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




//    private val adapter1 = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, provIndo) {
//        override fun getCount(): Int {
//            return super.getCount() - 1
//        }
//
//        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            val view = super.getDropDownView(position, convertView, parent)
//            val tv = view as TextView
//            if (position == items.size - 1) {
//                tv.setTextColor(Color.GRAY)
//            } else {
//                tv.setTextColor(Color.BLACK)
//            }
//
//            return view
//        }
//
//        override fun isEnabled(position: Int): Boolean {
//            if (position == items.size - 1) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//    }
//
//    private val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
//        override fun getCount(): Int {
//            return super.getCount() - 1
//        }
//
//        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            val view = super.getDropDownView(position, convertView, parent)
//            val tv = view as TextView
//            if (position == items.size - 1) {
//                tv.setTextColor(Color.GRAY)
//            } else {
//                tv.setTextColor(Color.BLACK)
//            }
//
//            return view
//        }
//
//        override fun isEnabled(position: Int): Boolean {
//            if (position == items.size - 1) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//    }