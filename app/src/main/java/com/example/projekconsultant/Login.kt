package com.example.projekconsultant

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.projekconsultant.admin.MainAdminActivity
import com.example.projekconsultant.methodsNService.ClassOfFunc
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.example.projekconsultant.sideactivity.KebijakanPrivasi
import com.example.projekconsultant.sideactivity.LengkapiRegistrasi
import com.example.projekconsultant.sideactivity.LupaPassword
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore



class Login : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var enter: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var ingatSaya:CheckBox

    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0
    private val handler = Handler(Looper.getMainLooper())

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
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        db = Firebase.firestore
        enter = findViewById(R.id.Enter)
        email = findViewById(R.id.LoginUsername)
        password = findViewById(R.id.Password)
        ingatSaya = findViewById(R.id.checkBoxIngatSaya)

        ClassOfFunc().setupPasswordToggle(password)
        addCloseKeyboard(findViewById(R.id.main))

        val lupapwTextView = findViewById<TextView>(R.id.lupapasswordlogin)
        val lupapwText = getString(R.string.lupapw)
        val spannableStr = SpannableStringBuilder(Html.fromHtml(lupapwText, Html.FROM_HTML_MODE_LEGACY))
        loadSavedCredentials()

        val clickableSpanns = object  : ClickableSpan(){
                override fun onClick(widget: View) {
                    val intent = Intent(this@Login, LupaPassword::class.java)
                    startActivity(intent)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@Login, R.color.textbirufigma) // Set warna di sini
                }
            }
            val strtIndex = lupapwText.indexOf(" Lupa Password?")
            if (strtIndex != -1) {
                spannableStr.setSpan(clickableSpanns, strtIndex, strtIndex + "Lupa Password?".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            lupapwTextView.text = spannableStr
            lupapwTextView.movementMethod = LinkMovementMethod.getInstance()

            val buatAkunTextView = findViewById<TextView>(R.id.buatakun)
            // Span String font (Tag <u>)
            val spannable = SpannableString(buatAkunTextView.text)

            // Ini berfungi sebagai tombol biru klik registrasi yang akan di arah kan ke
            // Class Lengkapi Registrasi.
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Buka activity registrasi (RegisterActivity)
                    val intent = Intent(this@Login, LengkapiRegistrasi::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@Login, R.color.textbirufigma) // Set warna di sini
                }
            }
            spannable.setSpan(clickableSpan, 19, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            buatAkunTextView.text = spannable
            buatAkunTextView.movementMethod = LinkMovementMethod.getInstance()

            // Kebijakan Privasi
            //Link Movement Tag <a>
            val privacyTextView = findViewById<TextView>(R.id.desckebijakanprivasi)
            val privacyPolicyText = getString(R.string.kebijakanprivasi)
            val spannableString = SpannableStringBuilder(Html.fromHtml(privacyPolicyText, Html.FROM_HTML_MODE_LEGACY))
            val clickableSpan2 = object  : ClickableSpan(){
                override fun onClick(widget: View) {
                    val intent = Intent(this@Login, KebijakanPrivasi::class.java)
                    startActivity(intent)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@Login, R.color.textbirufigma) // Set warna di sini
                }
            }
            val startIndex = privacyPolicyText.indexOf(" kebijakan privasi")
            if (startIndex != -1) {
                spannableString.setSpan(clickableSpan2, startIndex, startIndex + "kebijakan privasi".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            privacyTextView.text = spannableString
            privacyTextView.movementMethod = LinkMovementMethod.getInstance()

            val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
            dialog.setCancelable(false)

            progressBar = findViewById<ProgressBar>(R.id.progressBarLogin)
            // Set progres menjadi 50 (contoh)
            progressBar.progress = 0  // Nilai bisa disesuaikan (0-100)
            progressStatus = 0

            // Inisialisasi NetworkChangeReceiver
            networkChangeReceiver = NetworkChangeReceiver { isConnected ->
                if (!isConnected) {
                    val intent = Intent(this, NoInternet::class.java)
                    startActivity(intent)
                }else{

                }
            }

            enter.setOnClickListener {

                val dialogEmailNotFound = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Tolong Isi Dengan Benar"

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() && email.text.toString().isNotEmpty() &&  password.text.toString().isNotEmpty()) {
                    // Dialog Berhasil Login
                    loginUser(email.text.toString(), password.text.toString(), dialog)
                }else{
                    dialogEmailNotFound.show()

                }
            }

            findViewById<ImageButton>(R.id.whatsapp).setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send/?phone=62811803875&text&type=phone_number&app_absent=0&wame_ctl=1&fbclid=PAY2xjawHpcj1leHRuA2FlbQIxMAABpkg7FCarCgH0DNRyX_YH3gDLtB4lRkeh4MKMsaR49emAEkFIjpAidh5HIw_aem_NoWyvW6URsRE8K3jWr6qZA"))
                startActivity(intent)
            }
    }

    fun showCustomDialog(context: Context, layoutId: Int, backgroundDrawableId: Int,
                                     onDialogViewReady: (view: View, dialog: AlertDialog) -> Unit): AlertDialog {
        // Membuat builder dialog
        val builder = AlertDialog.Builder(context)
        // Inflasi layout dialog
        val view = LayoutInflater.from(context).inflate(layoutId, null)
        // Pasang layout ke dialog
        builder.setView(view)

        // Buat dialog
        val dialog = builder.create()

        // Mengatur ukuran wrap_content untuk dialog
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Set background untuk dialog
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, backgroundDrawableId))
        dialog.setCancelable(false) // Dialog dapat ditutup dengan back atau klik luar

        // Callback untuk memberikan akses ke view dialog dan dialog itu sendiri
        onDialogViewReady(view, dialog)

        return dialog
    }

    // Menyimpan data ke  penyimpanan lokal user masing-masing.
    // Ini fungsi dari fitur ingat saya.
    private fun saveCredentials(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("rememberMe", true)
        editor.apply()
    }

    // Pengecekan fungsi apakah sebelum nya user sudah klik check box ingat saya.
    private fun loadSavedCredentials() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        // Muat data hanya jika 'rememberMe' aktif
        if (rememberMe) {
            email.setText(savedEmail)
            password.setText(savedPassword)
            ingatSaya.isChecked = true
        } else {
            ingatSaya.isChecked = false
        }
    }

    // Membersihkan data dari fitur ingat saya.
    // ini berfungsi jika tombol check box nya tidak di ceklis.
    private fun clearCredentials() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun ActivityAdmin(){
        val intent = Intent(this@Login, MainAdminActivity::class.java)
        startActivity(intent)
    }

    private fun ActivityUser(){
        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
    }

    // Login User Terbaru
//    @SuppressLint("SuspiciousIndentation")
    fun loginUser(email: String, password: String, dialog: AlertDialog) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", "")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Login sukses
                progressBar.visibility = View.VISIBLE
                dialog.show()
                dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
                    dialog.dismiss()

                    // Jika email berbeda dari yang tersimpan, hapus data lama
                    if (email != savedEmail) {
                        // Jika email berbeda, hapus data lama dan simpan data baru
                        clearCredentials()
                    }

                    // Simpan kredensial jika checkbox "Ingat Saya" dicentang
                    if (ingatSaya.isChecked) {
                        saveCredentials(email, password)
                    } else {
                        // Hapus kredensial jika checkbox tidak dicentang
                        clearCredentials()
                    }

                    // Ambil userId pengguna yang berhasil login
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Jalankan thread untuk memperbarui progress bar hingga 100%
                        Thread {
                            while (progressStatus < 100) {
                                progressStatus += 1
                                handler.post { progressBar.progress = progressStatus
                                } // Tambahkan jeda waktu untuk simulasi progres
                                Thread.sleep(10) // Jeda waktu untuk setiap peningkatan
                            }
                            // Setelah progress selesai, periksa peran pengguna
                            checkUserRole(userId, dialog)
                        }.start()
                    }
                }
            } else {
                // Login gagal, tampilkan pesan peringatan
                // Login gagal
                val dialogEmailNotFound = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Gagal Login, Silahkan Coba Nanti. "

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogEmailNotFound.show()
            }
        }
    }

    // Ketika login ini akan berfungsi sebagai pengecekan role (front end)
    fun checkUserRole(userId: String, dialog: AlertDialog) {
        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val role = document.getString("typeroleuid")
                if (role != null) {
                    if (role == "admin") {
                        // User adalah admin
                        dialog.dismiss()
                        ActivityAdmin()
                    } else if (role == "user") {
                        // User adalah user biasa
                        dialog.dismiss()
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

    // Method menyembunyikan keyboard jika sudah selesai input di edit text.
    @SuppressLint("ClickableViewAccessibility")
    private fun addCloseKeyboard(rootView: View){
        rootView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }
    }

    // Method menyembunyikan keyboard jika sudah selesai input di edit text.
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    // Method Di bawah ini ga di pakai.
    // Fungsi ini mungkin berguna jika ada update an login lewat google jadi ini hanya untuk referensi saja.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Handle error
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Lakukan sesuatu dengan user
                } else {
                    // Handle sign in failure
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
}











//    fun loginUser(email: String, password: String, dialog: AlertDialog) {
//        if(ingatSaya.isChecked){
//            saveCredentials(email, password)
//        }else{
//            clearCredentials()
//        }
//
//        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//
//                    // Login sukses
//                    val userId = auth.currentUser?.uid
//                    if (userId != null) {
//                        checkUserRole(userId, dialog)
//                    }
//                } else {
//                    // Login gagal
//                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                }
//            }
//    }


//    private fun saveCredentials(email: String, password: String) {
//        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("email", email)
//        editor.putString("password", password)
//        editor.putBoolean("rememberMe", true)
//        editor.apply()
//    }

//    private fun loadSavedCredentials() {
//        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
//        val email = sharedPreferences.getString("email", "")
//        val password = sharedPreferences.getString("password", "")
//        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
//
//        if (rememberMe) {
//            this.email.setText(email)
//            this.password.setText(password)
//            ingatSaya.isChecked = true
//        }
//    }


//                        auth.currentUser?.let { user ->
//                            if (user.isEmailVerified) {
//                                checkUserRole(userId, dialog)
//                            } else {
//                                // Tampilkan pesan bahwa email belum diverifikasi
//                                Toast.makeText(this, "Email belum diverifikasi. Silakan periksa email Anda.", Toast.LENGTH_SHORT).show()
//                                FirebaseAuth.getInstance().signOut()
//                            }
//                        }