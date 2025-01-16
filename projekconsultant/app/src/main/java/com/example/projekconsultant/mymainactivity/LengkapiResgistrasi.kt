package com.example.projekconsultant.mymainactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.Registrasi
import com.example.projekconsultant.myfragment.MelengkapiRegisProfile

class LengkapiResgistrasi : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lengkapi_resgistrasi)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, v.paddingTop, systemBars.right, v.paddingBottom)
            insets
        }

        var namalengkap = findViewById<EditText>(R.id.nama_lengkap_user)
        var jeniskelamin = findViewById<RadioGroup>(R.id.radioGroup)
        var domisiliprov = findViewById<EditText>(R.id.domisili)
        var nomortelepon = findViewById<EditText>(R.id.nomortelepon)
//        var statushubungan = findViewById<EditText>(R.id.statushubungan)

//        getStatus(statushubungan)

        findViewById<Button>(R.id.lanjutregister).setOnClickListener {
            if (jeniskelamin.checkedRadioButtonId != -1){
                val nl = namalengkap.text.toString()
                val jk = findViewById<RadioButton>(jeniskelamin.checkedRadioButtonId).text.toString()
                val dp = domisiliprov.text.toString()
                val no = nomortelepon.text.toString()

                Toast.makeText(this, "Jenis = ${jk}", Toast.LENGTH_SHORT).show()

                if (nl.isNotEmpty() && jk.isNotEmpty() && dp.isNotEmpty() && no.length < 7 ) {
//                    val intent = Intent(this@LengkapiResgistrasi, Registrasi::class.java)
//                    startActivity(intent)
                }
            }
        }

        val spinner: Spinner = findViewById(R.id.spinner)
        val items = arrayOf("Pilih status", "Single", "Pacaran", "Menikah", "Lainnya")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            override fun isEnabled(position: Int): Boolean {
                // Disable item pertama (hint) agar tidak dapat dipilih
                return position != 0
            }


            @SuppressLint("ResourceAsColor")
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                // Warnai item pertama (hint) dengan warna abu-abu
                if (position == 0) {
                    textView.setTextColor(R.color.bluegrey200)
                } else {
                    textView.setTextColor(R.color.black)
                }
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

            // Deteksi pilihan pengguna
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                if (position == 0) {
//                    // User memilih item pertama (hint), abaikan pilihan ini
//                } else {
//                    // Tangani pilihan yang valid
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tidak ada yang dipilih
//            }
//        }






    }

    private fun getStatus(editText: EditText){
            editText.setOnClickListener {
                // Buat PopupMenu untuk menampilkan daftar item
                val popupMenu = PopupMenu(this, editText)
                popupMenu.menuInflater.inflate(R.menu.menu_statushubungan, popupMenu.menu)

                // Set listener ketika item di menu dipilih
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    editText.setText(menuItem.title)
                    true
                }

                // Tampilkan PopupMenu
                popupMenu.show()
            }

    }
}