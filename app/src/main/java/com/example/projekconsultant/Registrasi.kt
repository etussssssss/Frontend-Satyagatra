package com.example.projekconsultant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.projekconsultant.methodsNService.ClassOfFunc
import com.example.projekconsultant.sideactivity.KebijakanPrivasi
import com.example.projekconsultant.sideactivity.LengkapiRegistrasi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.example.projekconsultant.data.UserData
import com.example.projekconsultant.data.UserDataLengkapiRegistrasi
import com.example.projekconsultant.data.UserEksternal
import com.example.projekconsultant.data.UserInternal
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.firestore.firestore
import java.util.UUID

class Registrasi : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var passw: EditText
    private lateinit var confirmpassw: EditText

    private lateinit var progress:ProgressBar
    private lateinit var btnEnter:Button
    private lateinit var warningTex:TextView

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
        setContentView(R.layout.activity_registrasi)

        auth = Firebase.auth
        db = Firebase.firestore

        username = findViewById(R.id.regisusername)
        passw = findViewById(R.id.regispassword)
        confirmpassw =  findViewById(R.id.regisconfpassword)
        progress = findViewById(R.id.loading_progbar)
        btnEnter = findViewById<Button>(R.id.enterdaftar)
        warningTex = findViewById(R.id.pastikan)

        addCloseKeyboard(findViewById(R.id.main))
        addCloseKeyboard(findViewById(R.id.hostfragmentlengkapiregis))

        // Membuat daftar dari show/hide EditText password
        for (editText in listOf(passw, confirmpassw)) { ClassOfFunc().setupPasswordToggle(editText) }

        // Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        val data = intent.getParcelableExtra<UserDataLengkapiRegistrasi>("DATAUSERS")
        val internal = intent.getParcelableExtra<UserInternal>("INTERNAL")
        val eksternal = intent.getParcelableExtra<UserEksternal>("EKSTERNAL")

        btnEnter.setOnClickListener {

            val eml = username.text.toString().trim()
            val passw = passw.text.toString().trim()
            val confpassw = confirmpassw.text.toString().trim()

            if (eml.isNotEmpty() && passw.isNotEmpty() && confpassw.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(eml).matches() && passw == confpassw && passw.length >= 7) {
                btnEnter.isEnabled = false // Tombol dinonaktifkan
                progress.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(eml, passw).addOnCompleteListener { task ->
                    progress.visibility = View.GONE // Progress bar disembunyikan
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid.toString()
                        saveDataEP(eml, uid)

                        if (data != null && internal != null) {
                            inputInternal(data, internal, uid)
                        } else if (data != null && eksternal != null) {
                            inputEksternal(data, eksternal, uid)
                        }
                    } else {
                        btnEnter.isEnabled = true // Tombol diaktifkan kembali jika gagal

                        Log.e("Registrasi Gagal", "${task.exception?.message}")

                        // D1
                        val dialogFail1 = showCustomDialog(
                            context = this,
                            layoutId = R.layout.item_email_tidak_tersedia,
                            backgroundDrawableId = R.drawable.borderadius_dialog_white
                        ) { view, dialog ->
                            // Temukan tombol "kembali" di layout dialog
                            val kembaliButton = view.findViewById<Button>(R.id.kembali)
                            val textWarning = view.findViewById<TextView>(R.id.textWarning)

                            textWarning.text = "Registrasi Gagal. Coba lagi nanti, atau email tidak tersedia."

                            // Set aksi klik pada tombol
                            kembaliButton?.setOnClickListener {
                                // Logika saat tombol "kembali" diklik
                                dialog.dismiss() // Menutup dialog
                            }
                        }

                        dialogFail1.show()
                    }
                }
            } else if (passw != confpassw) {
                // D2
                val dialogFail2 = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Password Tidak Sama, Mohon Di Cek."

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogFail2.show()
            } else if (passw.length < 7){
                // D3
                val dialogFail3 = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Password Tidak Boleh Kurang Dari 7."

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogFail3.show()
            } else {
                Log.d("Registrasi", "Mohon lengkapi semua data dengan benar")
                // D4
                val dialogFail4 = showCustomDialog(
                    context = this,
                    layoutId = R.layout.item_email_tidak_tersedia,
                    backgroundDrawableId = R.drawable.borderadius_dialog_white
                ) { view, dialog ->
                    // Temukan tombol "kembali" di layout dialog
                    val kembaliButton = view.findViewById<Button>(R.id.kembali)
                    val textWarning = view.findViewById<TextView>(R.id.textWarning)

                    textWarning.text = "Mohon lengkapi semua data dengan benar."

                    // Set aksi klik pada tombol
                    kembaliButton?.setOnClickListener {
                        // Logika saat tombol "kembali" diklik
                        dialog.dismiss() // Menutup dialog
                    }
                }

                dialogFail4.show()
            }
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

    private fun saveDataEP(eml: String, uid:String) {
        val dataUsers = UserData(
            typeroleuid = "user",
            email = eml,
            uid = generateRandomId(),
        )

        db.collection("users").document(uid).set(dataUsers).addOnSuccessListener {
            Log.d("Berhasil", "Menyelesaikan Registrasi")
        }.addOnFailureListener { e ->
            Log.e("Failed to save user data: ", "${e.message}")
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

    private fun inputInternal(dataUsers: UserDataLengkapiRegistrasi, internal:UserInternal, uid: String) {
        val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
        dialog.setCancelable(false)

        // Variabel untuk melacak status
        var saveCount = 0
        // Fungsi untuk memeriksa status simpan
        fun checkAllDataSaved() {
            if (saveCount == 2) { // Jika kedua operasi selesai
                dialog.show()
                dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
                    val intent = Intent(this@Registrasi, MainActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
            }
        }

        // Menyimpan data pertama
        db.collection("datas").document(uid).set(dataUsers).addOnSuccessListener {
            saveCount++
            checkAllDataSaved() // Cek setelah sukses
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Menyimpan data kedua
        db.collection("internals").document(uid).set(internal).addOnSuccessListener {
            saveCount++
            checkAllDataSaved() // Cek setelah sukses
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputEksternal(dataUsers: UserDataLengkapiRegistrasi, eksternal:UserEksternal, uid: String) {
        val builder = AlertDialog.Builder(this).setView(R.layout.item_dialog_login)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.borderadius_dialog_white))
        dialog.setCancelable(false)

        // Variabel untuk melacak status
        var saveCount = 0
        // Fungsi untuk memeriksa status simpan
        fun checkAllDataSaved() {
            if (saveCount == 2) { // Jika kedua operasi selesai
                dialog.show()
                dialog.findViewById<Button>(R.id.btn_ok_login)?.setOnClickListener {
                    val intent = Intent(this@Registrasi, MainActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
            }
        }

        // Menyimpan data pertama
        db.collection("datas").document(uid).set(dataUsers).addOnSuccessListener {
            saveCount++
            checkAllDataSaved() // Cek setelah sukses
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Menyimpan data kedua
        db.collection("eksternals").document(uid).set(eksternal).addOnSuccessListener {
            saveCount++
            checkAllDataSaved() // Cek setelah sukses
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun regisAkunEmailNoVerif(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveDataEP(email, auth.currentUser?.uid.toString())
                val intent = Intent(this@Registrasi, LengkapiRegistrasi::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun hashDeviceId(deviceId: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(deviceId.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    //Dialog Ingin Kembali
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Yakin membatalkan registrasi Anda?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                finish()
                super.onBackPressed()  // Melanjutkan aksi kembali
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()  // Menutup dialog, tetap di halaman ini
            }
        val alert = builder.create()
        alert.show()
    }

    // Generate Random id untuk pendaftaran nanti nya.
    fun generateRandomId(): String {
        return "uid-"+UUID.randomUUID().toString()
    }

    // Method yang tidak terpakai.
    // Semua method di bawah ini hanya referensi.
    private fun sendEmailVerification(email: String, password: String) {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Email verifikasi telah dikirim. Periksa email Anda.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkRegisterUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification(email, password)
            } else {
                Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifRegisterUser(eml: String, confpassw: String) {
        auth.signInWithEmailAndPassword(eml, confpassw).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    auth.currentUser?.let { user ->
                        if (user.isEmailVerified) {
//                            findViewById<Button>(R.id.enterverif).visibility = View.GONE
                            Toast.makeText(this, "Verif successful", Toast.LENGTH_SHORT).show()

                            saveDataEP(eml, auth.currentUser?.uid.toString())

                            val intent = Intent(this@Registrasi, LengkapiRegistrasi::class.java)
                            startActivity(intent)
                            // Selesai Membuat Akun Yang Terferif
                            finish()
                        } else {
                            Toast.makeText(this, "Email belum diverifikasi. Silakan periksa email Anda.", Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
                }
            } else {
                Log.e("Failed Registrasi", ": ${task.exception?.message}")
            }
        }
    }

    fun security1(){
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val hashedDeviceId = hashDeviceId(deviceId)  // Hash Device ID sebelum menyimpan
        val userDoc = FirebaseFirestore.getInstance().collection("registrationAttempts").document(hashedDeviceId)

        userDoc.get().addOnSuccessListener { document ->
            val attempts = document.getLong("attempts") ?: 0
            val lockTime = document.getLong("lockTime") ?: 0L
            val currentTime = System.currentTimeMillis()

            // Blokir registrasi jika perangkat sudah mencoba 3 kali dalam 3 hari
            if (attempts >= 2 && currentTime - lockTime < 3 * 24 * 60 * 60 * 1000) {
                Toast.makeText(this, "Perangkat Anda diblokir dari pendaftaran selama 3 hari.", Toast.LENGTH_SHORT).show()
            } else {
                if (currentTime - lockTime >= 3 * 24 * 60 * 60 * 1000) {
                    // Reset percobaan jika lebih dari 3 hari
                    userDoc.set(mapOf("attempts" to 0, "lockTime" to 0L))
                }

                // Lanjutkan pendaftaran
//                registerUser(myusername, myconfpassw)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun kebijakanPrivasi(){
        // Kebijakan Privasi
        val privacyTextView = findViewById<TextView>(R.id.desckebijakanprivasireg)
        val privacyPolicyText = getString(R.string.kebijakanprivasi)
        val spannableString = SpannableStringBuilder(Html.fromHtml(privacyPolicyText, Html.FROM_HTML_MODE_LEGACY))
        val clickableSpan2 = object  : ClickableSpan(){
            override fun onClick(widget: View) {
                val intent = Intent(this@Registrasi, KebijakanPrivasi::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@Registrasi, R.color.textbirufigma)
            }
        }
        val startIndex = privacyPolicyText.indexOf(" kebijakan privasi")
        if (startIndex != -1) {
            spannableString.setSpan(clickableSpan2, startIndex, startIndex + "kebijakan privasi".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        privacyTextView.text = spannableString
        privacyTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}

















//        findViewById<Button>(R.id.enterverif).setOnClickListener {
//            var myusername = username.text.toString()
//            var mypassw = passw.text.toString()
//            var myconfpassw = confirmpassw.text.toString()
//
//            if (myusername.isNotEmpty() && mypassw.isNotEmpty() && myconfpassw.isNotEmpty()) {
//                if (mypassw.equals(myconfpassw)){
////                    checkRegisterUser(myusername, myconfpassw)
//                } else {
//                    Toast.makeText(this, "Password nya belum sama nih", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Tolong lengkapi semua nya hehe", Toast.LENGTH_SHORT).show()
//            }
//            findViewById<TextView>(R.id.pastikan).text = "Cek Email Kamu Yaa.."
//            findViewById<TextView>(R.id.pastikan).setTextColor(ContextCompat.getColor(this, R.color.red500))
//            findViewById<Button>(R.id.enterverif).visibility = View.GONE
//        }


//
//        btnEnter.setOnClickListener {
//            var eml = username.text.toString()
//            var passw = passw.text.toString()
//            var confpassw = confirmpassw.text.toString()
//
//            if (eml.isNotEmpty() && passw.isNotEmpty() && confpassw.isNotEmpty()) {
//                if (passw.equals(confpassw)){
//                    btnEnter.isActivated = false
//
//                    auth.createUserWithEmailAndPassword(eml, confpassw).addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            progress.visibility = View.VISIBLE
//                            Handler().postDelayed({
//
//                                saveDataEP(eml, auth.currentUser?.uid.toString())
//                                if(data != null && internal != null){
//                                    inputInternal(data, internal, auth.currentUser?.uid.toString())
//                                }else if(data != null && eksternal != null){
//                                    inputEksternal(data, eksternal, auth.currentUser?.uid.toString())
//                                }
//
//                                progress.visibility = View.GONE
//                                btnEnter.isActivated = true
//                            },4500)
//                        } else {
//                            Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//
//                } else {
//                    Toast.makeText(this, "Password nya belum sama nih", Toast.LENGTH_SHORT).show()
//                }
//
//            } else {
//                Toast.makeText(this, "Tolong lengkapi semua nya hehe", Toast.LENGTH_SHORT).show()
//            }
//        }