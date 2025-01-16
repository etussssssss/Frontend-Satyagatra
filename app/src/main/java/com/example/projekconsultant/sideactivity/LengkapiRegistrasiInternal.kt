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
import android.widget.RadioButton
import android.widget.RadioGroup
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
//import java.util.ArrayList
import kotlin.collections.ArrayList

class LengkapiRegistrasiInternal : AppCompatActivity() {
    private var userFakultas:String = ""
    private var userBagianTendik:String = "none"
    private var userRole:String = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var dataInternal:ArrayList<String> = ArrayList()

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
        setContentView(R.layout.activity_lengkapi_registrasi_internal)
        baseAct()

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

        val rootView = findViewById<View>(R.id.main)
        addCloseKeyboard(rootView)
        var radioGrupBtn = findViewById<RadioGroup>(R.id.instansiradioGroup)
        var spinnerMahasiswa = findViewById<Spinner>(R.id.spinnerinternalmahasiswa)
        var spinnerDosen = findViewById<Spinner>(R.id.spinnerinternaldosen)
        var spinnerTendik = findViewById<Spinner>(R.id.spinnerinternaltendik)
        var spinnerTendik2 = findViewById<Spinner>(R.id.spinnerinternaltendik2)

        listOf<Spinner>(spinnerMahasiswa, spinnerDosen, spinnerTendik, spinnerTendik2).forEach {
            it.isEnabled = false
            it.setSelection(it.count - 1)
        }

        auth = Firebase.auth
        db = Firebase.firestore

        val items = arrayOf("Fakultas Kedokteran (FK)", "Fakultas Psikologi (FP)",
            "Fakultas Teknologi Informasi (FTI)", "Fakultas Kedokteran Gigi (FKG)",
            "Fakultas Hukum (FH)", "Fakultas Ekonomi Dan Bisnis (FEB)",
            "Pilih fakultas")

        val items2 = arrayOf("Rektor", "Wakil Rektor I", "Wakil Rektor II",
            "Wakil Rektor III", "Wakil Rektor IV", "Wakil Rektor V",
            "PDJAMEA", "PJJ", "Pilih Bagian di Universitas")

        configSpinner(spinnerMahasiswa, items, items.size - 1)
        configSpinner(spinnerDosen, items, items.size - 1)
        configSpinner(spinnerTendik, items, items.size - 1)
        configSpinner2(spinnerTendik2, items2, items2.size - 1)

        radioGrupBtn.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.Mahasiswa -> {
                    dataInternal.clear()
                    enableSpinners(spinnerMahasiswa, arrayOf(spinnerDosen, spinnerTendik, spinnerTendik2))
                }
                R.id.Dosen -> {
                    dataInternal.clear()
                    enableSpinners(spinnerDosen, arrayOf(spinnerMahasiswa, spinnerTendik, spinnerTendik2))
                }
                R.id.Tendik -> {
                    dataInternal.clear()
                    enableSpinners(spinnerTendik, arrayOf(spinnerMahasiswa, spinnerDosen))
                    enableSpinners(spinnerTendik2, arrayOf(spinnerMahasiswa, spinnerDosen))
                }
            }
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }

        val userData = intent.getStringArrayListExtra("userData")

        findViewById<Button>(R.id.save).setOnClickListener {
            val warningtext = findViewById<TextView>(R.id.warningtextinternal)
            funcNext(warningtext, radioGrupBtn, userData)
        }
    }

    private fun enableSpinners(enabledSpinner: Spinner, disabledSpinners: Array<Spinner>) {
        disabledSpinners.forEach {
            it.isEnabled = false
            it.setSelection(it.count - 1)
        }

        enabledSpinner.isEnabled = true
        dataInternal.clear()
    }

    private fun funcNext(text: TextView, radioG: RadioGroup, userData: ArrayList<String>?){
        text.visibility = View.GONE
        var sTendik = if(radioG.checkedRadioButtonId == R.id.Tendik) userBagianTendik.isNotEmpty() else true
        userRole = findViewById<RadioButton>(radioG.checkedRadioButtonId).text.toString()

        if(radioG.checkedRadioButtonId != -1 && userFakultas.isNotEmpty() && sTendik && userData != null ){

            Log.d("MSG", "Selected 1.1: ${userData.joinToString(", ")}")
            Log.d("MSG", "Selected 2: ${userRole} ${userFakultas}, ${userBagianTendik}")
            val intent = Intent(this@LengkapiRegistrasiInternal, LengkapiResgistrasiTopikKonseling::class.java)
            intent.putExtra("iORe", "I")
            intent.putStringArrayListExtra("userData", userData)
            intent.putExtra("userRole", userRole)
            intent.putExtra("userFakultas", userFakultas)
            intent.putExtra("userBagianTendik", userBagianTendik)
            startActivity(intent)

        }else {
            findViewById<TextView>(R.id.warningtextinternal).visibility = View.VISIBLE
        }
    }


    private fun configSpinner(spinner: Spinner, items: Array<String>, lastItemPos: Int) {
        spinner.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getCount() = super.getCount()
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView

                val layoutParams = view.layoutParams
                layoutParams.height = 100 // set the height in pixels

                view.layoutParams = layoutParams
                view.setPadding(16, 16, 16, 16) // Set padding (left, top, right, bottom)
                view.maxHeight = 100
                view.setTextColor(if (position == lastItemPos) Color.GRAY else Color.BLACK)
                return view
            }
            override fun isEnabled(position: Int) = position != lastItemPos
        }

        spinner.setSelection(lastItemPos)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var str = if (position != lastItemPos) items[position] else ""
                userFakultas = str
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    private fun configSpinner2(spinner: Spinner, items: Array<String>, lastItemPos: Int) {
        spinner.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getCount() = super.getCount()
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView

                val layoutParams = view.layoutParams
                layoutParams.height = 100 // set the height in pixels

                view.layoutParams = layoutParams
                view.setPadding(16, 16, 16, 16) // Set padding (left, top, right, bottom)
                view.maxHeight = 100
                view.setTextColor(if (position == lastItemPos) Color.GRAY else Color.BLACK)
                return view
            }
            override fun isEnabled(position: Int) = position != lastItemPos
        }

        spinner.setSelection(lastItemPos)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var str = if (position != lastItemPos) items[position] else ""
                userBagianTendik = str
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
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

    private fun funcSpinner1(spinner: Spinner, items: Array<String> ,spinnerEnabled: Array<Spinner>) {
        for (enable in spinnerEnabled){
            enable.isEnabled = false
            userFakultas = ""
        }

        spinner.isEnabled = true

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getCount(): Int = super.getCount() - 1

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                tv.setTextColor(if (position == items.size - 1) Color.GRAY else Color.BLACK)
                return view
            }

            override fun isEnabled(position: Int): Boolean = position != items.size - 1
        }

        spinner.adapter = adapter
        spinner.setSelection(items.size - 1)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items.size - 1) {
                    userFakultas = items[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}























//        findViewById<RadioButton>(R.id.Mahasiswa).setOnClickListener {
//            dataInternal.clear()
//            enableSpinners(spinnerMahasiswa, arrayOf(spinnerDosen, spinnerTendik, spinnerTendik2))
//        }
//
//
//        findViewById<RadioButton>(R.id.Dosen).setOnClickListener {
//            dataInternal.clear()
//            enableSpinners(spinnerDosen, arrayOf(spinnerMahasiswa, spinnerTendik, spinnerTendik2))
//        }
//
//
//        findViewById<RadioButton>(R.id.Tendik).setOnClickListener {
//            dataInternal.clear()
//            enableSpinners(spinnerTendik, arrayOf(spinnerMahasiswa, spinnerDosen))
//            enableSpinners(spinnerTendik2, arrayOf(spinnerMahasiswa, spinnerDosen))
//        }
