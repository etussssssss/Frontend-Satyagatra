package com.example.projekconsultant.sideactivity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.projekconsultant.Login
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LengkapiRegistrasi : AppCompatActivity() {
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

    private fun toLogin(){
        //Login
        val tologin = findViewById<TextView>(R.id.sudahpunyakun)
        val haveAccount = getString(R.string.login_akun)
        val startIndexLogin = haveAccount.indexOf("Login")
        val spannable = SpannableString(tologin.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Buka activity login (LoginActivity)
                val intent = Intent(this@LengkapiRegistrasi, Login::class.java)
                startActivity(intent)
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@LengkapiRegistrasi, R.color.textbirufigma) // Set warna di sini
            }
        }
        spannable.setSpan(clickableSpan, startIndexLogin, startIndexLogin + "Login".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tologin.text = spannable
        tologin.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lengkapi_registrasi)

        val rootView = findViewById<View>(R.id.main)
        val rootView2 = findViewById<View>(R.id.main2)

        var namalengkap = findViewById<EditText>(R.id.nama_lengkap_user)
        var jeniskelamin = findViewById<RadioGroup>(R.id.radioGroup)
        var domisiliprov = findViewById<Spinner>(R.id.domisili)
        var nomortelepon = findViewById<EditText>(R.id.nomortelepon)
        var tanggalLahir = findViewById<TextView>(R.id.tanggalLahir)
        val statushubungan = findViewById<Spinner>(R.id.spinner)

        baseAct()
        toLogin()
        kebijakanPrivasi()

        val wajib = listOf(
            findViewById<TextView>(R.id.textView11),
            findViewById<TextView>(R.id.textView13),
            findViewById<TextView>(R.id.texumur),
            findViewById<TextView>(R.id.s),
        )

        wajib.forEach {
            setRedAsteriskLabel(it, "${it.text}")
        }

        // Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        addCloseKeyboard(rootView)
        addCloseKeyboard(rootView2)

        val provIndo = arrayOf(
            "Aceh",
            "Sumatera Barat",
            "Riau",
            "Kepulauan Riau",
            "Jambi",
            "Sumatera Selatan",
            "Bangka Belitung",
            "Bengkulu",
            "Lampung",
            "DKI Jakarta",
            "Jawa Barat",
            "Banten",
            "Jawa Tengah",
            "DI Yogyakarta",
            "Jawa Timur",
            "Bali",
            "Nusa Tenggara Barat",
            "Nusa Tenggara Timur",
            "Kalimantan Barat",
            "Kalimantan Tengah",
            "Kalimantan Selatan",
            "Kalimantan Timur",
            "Kalimantan Utara",
            "Sulawesi Utara",
            "Gorontalo",
            "Sulawesi Tengah",
            "Sulawesi Barat",
            "Sulawesi Selatan",
            "Sulawesi Tenggara",
            "Maluku",
            "Maluku Utara",
            "Papua",
            "Papua Barat",
            "Papua Selatan",
            "Papua Tengah",
            "Papua Pegunungan",
            "Papua Barat Daya",
            "Sumatera Utara",
            "Pilih",
        )

        val adapterDomisili = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, provIndo) {
            override fun getCount(): Int {
                return super.getCount() - 1
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                tv.setTextColor(if (position == provIndo.size - 1) Color.GRAY else Color.BLACK)
                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != provIndo.size - 1
            }
        }

        domisiliprov.adapter = adapterDomisili
        domisiliprov.setSelection(provIndo.size - 1) // Pastikan opsi "Pilih" adalah default

        domisiliprov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != provIndo.size - 1) {
                    Log.d("Select Domisili", "${provIndo[position]}")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        addCloseKeyboard(domisiliprov)

        val items = arrayOf("Single", "Pacaran", "Menikah", "Lainnya", "Pilih status")
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

        statushubungan.adapter = adapter
        statushubungan.setSelection(items.size - 1)
        addCloseKeyboard(statushubungan)
        statushubungan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items.size - 1) {
                    Log.d("Status", "${items[position]}")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        setDate(tanggalLahir)
        settingsNomorTelepon(nomortelepon)

        val warningText = findViewById<TextView>(R.id.warningdatabelumlengkap)
        findViewById<Button>(R.id.lanjutregister).setOnClickListener {
            warningText.visibility = View.GONE

            if (jeniskelamin.checkedRadioButtonId != -1){
                val nl = namalengkap.text.toString().trim()
                val jk = findViewById<RadioButton>(jeniskelamin.checkedRadioButtonId).text.toString()
                val dp = domisiliprov.selectedItem.toString()
                var no = "62"+nomortelepon.text.toString().trim()
                val tl = tanggalLahir.text.toString()
                val sh = statushubungan.selectedItem.toString()

                val bundleUserData = ArrayList<String>()

                if (nl.isNotEmpty() && jk.isNotEmpty() && dp.isNotEmpty() && tl.isNotEmpty() &&
                    !sh.equals("Pilih status", ignoreCase = true) && !dp.equals("Pilih", ignoreCase = true)) {
                    if(no.length > 10) {
                        bundleUserData.add("${nl}")
                        bundleUserData.add("${jk}")
                        bundleUserData.add("${dp}")
                        bundleUserData.add("${no}")
                        bundleUserData.add("${tl}")
                        bundleUserData.add("${sh}")
                        Log.d("Lengkapi Registrasi", "${bundleUserData.joinToString(", ")}")

                        val intent = Intent(this@LengkapiRegistrasi, LengkapiRegistrasiInstansi::class.java)
                        intent.putStringArrayListExtra("userData", bundleUserData)
                        startActivity(intent)
                    } else {
                        warningText.visibility = View.VISIBLE
                        warningText.text = "Nomor Kamu Tidak Valid, Kurang Dari 9"
                    }
                } else {
                    warningText.visibility = View.VISIBLE
                }
            }else {
                warningText.visibility = View.VISIBLE
            }
        }
    }

    private fun setDate(tanggal: TextView) {
        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        tanggal.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                tanggal.context,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    // Simpan tanggal terakhir yang dipilih
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth

                    val currentDate = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val dateString = dateFormat.format(selectedDate.time)

                    // Validasi usia minimal 1 tahun
                    val age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR)
                    val isLessThanOneYear = age < 1 || (age == 1 && currentDate.before(selectedDate.apply {
                        set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                    }))

                    if (isLessThanOneYear) {
                        tanggal.text = ""  // Kosongkan TextView jika umur kurang dari 1 tahun
                        Toast.makeText(tanggal.context, "Tanggal harus minimal 1 tahun ke belakang!", Toast.LENGTH_SHORT).show()
                    } else {
                        tanggal.text = dateString
                        Log.d("TANGGAL LAHIR", tanggal.text.toString())
                    }
                },
                selectedYear, selectedMonth, selectedDay // Menggunakan tanggal terakhir yang dipilih
            )

            datePickerDialog.setOnCancelListener {
                tanggal.text = ""
            }

            datePickerDialog.setCancelable(true)
            datePickerDialog.show()
        }
    }

    private fun settingsNomorTelepon(editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                // Hapus spasi langsung
                if (text.contains(" ")) {
                    editText.setText(text.replace(" ", ""))
                    editText.setSelection(editText.text.length) // Pindahkan kursor ke akhir
                }
            }
        })
    }

    private fun kebijakanPrivasi(){
        // Kebijakan Privasi
        val privacyTextView = findViewById<TextView>(R.id.desckebijakanprivasireg)
        val privacyPolicyText = getString(R.string.kebijakanprivasi)
        val spannableString = SpannableStringBuilder(Html.fromHtml(privacyPolicyText, Html.FROM_HTML_MODE_LEGACY))
        val clickableSpan2 = object  : ClickableSpan(){
            override fun onClick(widget: View) {
                val intent = Intent(this@LengkapiRegistrasi, KebijakanPrivasi::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@LengkapiRegistrasi, R.color.textbirufigma)
            }
        }
        val startIndex = privacyPolicyText.indexOf(" kebijakan privasi")
        if (startIndex != -1) {
            spannableString.setSpan(clickableSpan2, startIndex, startIndex + "kebijakan privasi".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        privacyTextView.text = spannableString
        privacyTextView.movementMethod = LinkMovementMethod.getInstance()
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

    fun setRedAsteriskLabel(textView: TextView, label: String) {
        val spannable = SpannableString("$label*")

        // Mengatur warna merah hanya untuk bintang (*)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(textView.context, R.color.red500)),
            label.length,      // Mulai dari posisi setelah teks
            label.length + 1,  // Hingga karakter bintang (*)
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Mengatur teks yang telah diubah ke TextView
        textView.text = spannable
    }


    //Referensi Custom Dialog
    private fun setDate2(tanggal: EditText){
        // Inisialisasi variabel untuk menyimpan tanggal terakhir yang dipilih
        var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
        var selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
        var selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        var isDatePickerShown = false  // Status dialog

        tanggal.setOnClickListener {
            if (!isDatePickerShown) {  // Hanya tampilkan dialog jika belum muncul
                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.item_custom_date_picker, null)
                builder.setView(dialogLayout)

                val datePicker = dialogLayout.findViewById<DatePicker>(R.id.datePicker)
                datePicker.updateDate(selectedYear, selectedMonth, selectedDay)  // Set tanggal terakhir dipilih

                val okButton = dialogLayout.findViewById<Button>(R.id.okButton)
                val dialog = builder.create()

                okButton.setOnClickListener {
                    // Ambil tanggal yang dipilih
                    selectedYear = datePicker.year
                    selectedMonth = datePicker.month
                    selectedDay = datePicker.dayOfMonth

                    val selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    tanggal.setText(selectedDate)  // Tampilkan tanggal di EditText
                    dialog.dismiss()  // Menutup dialog
                }

                dialog.setOnDismissListener {
                    isDatePickerShown = false  // Set kembali ke false saat dialog ditutup
                }

                isDatePickerShown = true  // Set status dialog menjadi true
                dialog.show()
            }
        }
    }
}









//domisiliprov.setDropDownAnchor(R.id.domisili)
//
//// Set the tokenizer to handle input as single selection
//domisiliprov.setTokenizer(object : MultiAutoCompleteTextView.Tokenizer {
//    override fun terminateToken(text: CharSequence?): CharSequence? {
//        return text // terminate token when user enters a character
//    }
//
//    override fun findTokenStart(text: CharSequence?, cursor: Int): Int {
//        return 0 // start of token (ignoring previous entries)
//    }
//
//    override fun findTokenEnd(text: CharSequence?, cursor: Int): Int {
//        return text!!.length // end of token (limit to 1)
//    }
//})
//
//// Optional: Clear previous entries if any new selection is made
//domisiliprov.setOnItemClickListener { parent, view, position, id ->
//    val selectedItem = adapterProvIndo.getItem(position)
//    domisiliprov.setText(selectedItem)
//
//    // Set cursor to the end of the text
//    domisiliprov.setSelection(selectedItem?.length ?: 0)
//}


//    private fun setDate(tanggal: TextView){
//        // Inisialisasi variabel untuk menyimpan tanggal terakhir yang dipilih
//        var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
//        var selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
//        var selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        var isDatePickerShown = false  // Status dialog
//        tanggal.setOnClickListener {
//            if (!isDatePickerShown) {  // Hanya tampilkan dialog jika belum muncul
//                val datePickerDialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, { _, year, month, dayOfMonth ->
//                    // Tindakan saat tanggal dipilih
//                    selectedYear = year
//                    selectedMonth = month
//                    selectedDay = dayOfMonth
//
//                    // Hitung umur
//                    val birthDate = Calendar.getInstance().apply {
//                        set(selectedYear, selectedMonth, selectedDay)
//                    }
//                    val currentDate = Calendar.getInstance()
//                    var age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
//                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//                    val dateString = dateFormat.format(birthDate.time)
//
//                    // Cek apakah ulang tahun sudah lewat di tahun ini
//                    if (currentDate.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) || (currentDate.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
//                                currentDate.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
//                        age -= 1
//                    } else {
////                        tanggal.setText(age.toString())  // Tampilkan umur
//                        if (age < 1) {
//                            tanggal.setText("")  // Tampilkan 0 jika umur kurang dari 1
//                        } else {
//                            tanggal.setText("${dateString}")  // Tampilkan umur
//                            Log.d("BIRTH DAY", "${tanggal.text}")
//                        }
//                    }
//                    isDatePickerShown = false  // Set kembali ke false saat dialog selesai
//                },
//                    selectedYear,  // Gunakan tanggal terakhir dipilih sebagai nilai awal
//                    selectedMonth,
//                    selectedDay
//                )
//                datePickerDialog.setOnDismissListener {
//                    isDatePickerShown = false  // Set kembali ke false saat dialog ditutup
//                }
//                isDatePickerShown = true  // Set status dialog menjadi true
//                datePickerDialog.show()
//                datePickerDialog.setCancelable(false)
//            }
//        }
//    }