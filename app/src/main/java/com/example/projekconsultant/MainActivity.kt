package com.example.projekconsultant

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.adapter.ReviewItem
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
//import com.example.projekconsultant.methods.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.example.projekconsultant.myfragment.Home
import com.example.projekconsultant.myfragment.Penjadwalan
import com.example.projekconsultant.myfragment.Profile
import com.example.projekconsultant.myfragment.Riwayat
import com.example.projekconsultant.myfragment.jadwalOffline
import com.example.projekconsultant.myfragment.jadwalOnline
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class MainActivity : BaseActivity() {
    private  var namaprofile:String? = "Anonymous"
    private  var favTopik:String? = ""
    private  var domisili:String? = ""

    private lateinit var myUid:String
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var db:FirebaseFirestore

    // Untuk menyimpan data ke Shared View model (Live Data Frontend)
    private var datasSelesai:ArrayList<SelesaiData> = ArrayList()
    private var datasReview:ArrayList<SelesaiDetailReviewUser> = ArrayList()
    private var datasNomorPendafataranRiwayat:ArrayList<String> = ArrayList()
    private var datasReviewHome:ArrayList<ReviewItem> = ArrayList()

    //
    // val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
    //
    private var listener11: ListenerRegistration? = null
    private var listener12: ListenerRegistration? = null
    private var listener13: ListenerRegistration? = null
    private var listener14: ListenerRegistration? = null

    private lateinit var ruid:String

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        // Inisialisasi Autentikasi Firebase
        val auth = FirebaseAuth.getInstance().currentUser
        //Real UID
        ruid = auth?.uid.toString()
        // myUid = uid fake bukan auth
        // Ambil Uid user untuk pendaftaran konseling, data ini di ambil dari class FirstProgress sebelum nya.
        myUid = intent.getStringExtra("uid").toString()

        // Variable shared view model untuk mengambil data yang sudah di manipulasi untuk live data.
        val sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // Inisialisasi Menu Bottom Navigasi Aplikasi pada MainAcitivity.
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)

        // Inisialisasi NetworkChangeReceiver, Ini berfungsi untuk cek internet pada lewat front end jika si user tidak memakai jaringan internet atau tidak ada jaringan internet.
        // Jika tidak ada jaringan internet akan di arahkan ke Class NoInternet yang menampilkan coba lagi.
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }

        // Pengecekan Authentikasi
        if (auth != null) {
            // Pengambilan Data si User secara privasi.
            listener11 = db.collection("datas").document(auth.uid).addSnapshotListener { data, error ->
                if (error != null) {
                    // Tangani error jika ada
                    Log.e("Firestore Datas", "Error getting user data: ", error)
                    return@addSnapshotListener
                }

                if (data != null && data.exists()) {
                    namaprofile = data.getString("nama")
                    favTopik = data.getString("selectTopik")
                    domisili = data.getString("domisili")


                    sharedViewModel.namaUser.value = data.getString("nama")
                    sharedViewModel.gender.value = data.getString("gender")
                    sharedViewModel.umur.value = data.getString("tanggalLahir")
                    sharedViewModel.nomorTelepon.value = data.getString("nomortelepon")
                    sharedViewModel.typeUserIORE.value = data.getString("type")
                    sharedViewModel.status.value = data.getString("status")
                    sharedViewModel.domisili.value = data.getString("domisili")


                    // Ganti fragment dengan data terbaru
                    replaceFragment(Home.newInstance(namaprofile ?: ""))
                }
            }

            sharedViewModel.typeUserIORE.observe(this) { status ->
                // Jika ada perubahan status pendaftaran nya maka otomatis variable ini berubah.
                if(status.toString() == "I"){
                    db.collection("internals").document(ruid).get().addOnSuccessListener { data ->
                        sharedViewModel.profesi.value = data.getString("roleinternal")
                        sharedViewModel.fakultas.value = data.getString("fakultas")
                    }
                } else if (status.toString() == "E"){
                    db.collection("eksternals").document(ruid).get().addOnSuccessListener { data ->
                        sharedViewModel.profesi.value = data.getString("pekerjaan")
                    }
                }
            }

            // Pengambilan Data Daftar Konseling si user secara privasi.
            // Perbedaan pengambilan data pakai snapshot dan get biasa adalah kalau snapshot jika ada update data atau data baru akan secara real time di front end ikut
            // update, kalau hanya memakai get biasa butuh refresh aplikasi ulang untuk mendapatkan data yang baru masuk atau ada data yang di update.
            listener12 = db.collection("daftarKonseling").document(auth.uid).addSnapshotListener { data, error ->
                if (error != null) {
                    // Tangani error jika ada
                    Log.e("Firestore Daftar Konseling", "Error getting counseling data: ", error)
                    return@addSnapshotListener
                }

                // Pengambilan data si user apakah si user sudah ada pendaftaran nya atau belum, karena di aplikasi ini hanya boleh daftar 1 per 1 di setiap akun.
                // Jika si user saat ini tidak ada pendaftaran sama sekali maka (true) si user boleh mendaftar kan konseling.
                // Tetapi jika si user sudah ada penjadwalan saat ini yang belum di selesaikan maka tidak bisa.
                if (data != null && data.exists()) {
                    // Perbarui status berdasarkan perubahan secara real time menggunakan shared view model (live data front end)
                    sharedViewModel.riwayatStatusButuhPersetujuan.value = data.getBoolean("butuhPersetujuan")
                    sharedViewModel.riwayatStatusDisetujui.value = data.getBoolean("diSetujui")
                    sharedViewModel.riwayatTopik.value = data.getString("pilihTopikKonsultasi")
                    sharedViewModel.riwayatLayanan.value = data.getString("pilihLayananKonsultasi")
                    sharedViewModel.riwayatTanggal.value = data.getString("pilihTanggal")
                    sharedViewModel.riwayatOffOrOn.value = data.getString("onoff")
                    sharedViewModel.riwayatDescTopik.value = data.getString("isiCurhatan")
                    sharedViewModel.riwayatNoPendaftaran.value = data.getString("nomorPendaftaran")
                    // Tidak Bisa Mendaftar
                    sharedViewModel.statusPendaftaran.value = false
                } else {
                    // Bisa Mendaftar
                    sharedViewModel.statusPendaftaran.value = true
                    sharedViewModel.riwayatStatusDisetujui.value = false
                }
            }

            // Pengambilan Data Riwayat Konseling si user secara privasi.
            db.collection("riwayat").document(ruid).collection("allRiwayat")
                .orderBy("pilihTanggal", Query.Direction.DESCENDING).get().addOnSuccessListener { snapshots ->
                    if (!snapshots.isEmpty) {
                        // Mendapatkan riwayat status pertama si user (untuk membuat Class Selesai di Class Riwayat nanti nya).
                        sharedViewModel.riwayatStatusSelesai.value = true

                        for (document in snapshots.documents) {
                            val data = document.data // Mendapatkan isi data dokumen
                            val documentId = document.id // Mendapatkan ID dokumen

                            datasSelesai.add(
                                SelesaiData(
                                    nomorPendaftaran = "${documentId}",
                                    name = "${data?.get("nama")}",
                                    topik = "${data?.get("pilihTopikKonsultasi")}",
                                    tanggal = "${data?.get("pilihTanggal")}",
                                    offOrOn = "${data?.get("onoff")}",
                                    isiCurhatan = "${data?.get("isiCurhatan")}",
                                    konselor = "${data?.get("konselor")}",
                                )
                            )

                            // (menyimpan data sementara pakai variable class main activity yang (sudah di inisialisasi di atas)
                            datasNomorPendafataranRiwayat.add("${documentId}")
                            // Update di shared view model (live data)
                            sharedViewModel.updateDataSelesai(datasSelesai)
                        }
                    } else {
                        Log.d("FirestoreGet Riwayat", "Tidak ada data atau snapshot kosong")
                    }
            }.addOnFailureListener { error ->
                Log.e("FirestoreGet Riwayat", "Gagal mendapatkan data: ${error.message}")
            }

            // Pengambilan Data Review Konseling si user secara public.
            db.collection("review").get().addOnSuccessListener { queryResult ->
                queryResult.documents.forEach { document ->
                    val dbReview = document.data // Map<String, Any> dari dokumen

                    // Tambahkan data baru ke data class datasReview
                    datasReviewHome.add(
                        ReviewItem(
                            nama = dbReview?.get("inisial")?.toString() ?: "",
                            pengalaman = dbReview?.get("pengalaman")?.toString() ?: "", // Null-safe
                            tag = dbReview?.get("tag")?.toString()?.split(", ") ?: emptyList(), // Null-safe
                            rateStar = (dbReview?.get("star") as? Long)?.toInt() ?: 0 // Null-safe casting ke Int
                        )
                    )
                }

                // Perbarui data di ViewModel
                sharedViewModel.updateDataReviewAllUser(datasReviewHome)
            }.addOnFailureListener { exception ->
                Log.e("Firestore review", "Error mengambil data dari Firestore: ${exception.message}")
            }
        }


        //Men Default kan pilihan bottom navigation ke home langsung.
        replaceFragment(Home.newInstance(namaprofile ?: ""))

        //  Bundling Data Lewat live data
        sharedViewModel.ruid.value = ruid
        // Bottom Navigation Function, Berserta bundling variable data ke Class Tersebut.
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> Home()
                R.id.checked -> Penjadwalan()
                R.id.history -> Riwayat()
                R.id.profil -> Profile.newInstance("${namaprofile}", "${domisili}", "${favTopik}")
                else -> null
            }
            selectedFragment?.let {
                replaceFragment(it)
                true
            } ?: false
        }

        //Untuk memperhalus On Back pengganti OnBackPressed karna ini lebih halus dan tidak ada bug.
        onBackPressedDispatcher.addCallback(this) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

            // Kalo berada di Class JadwalOffline dan JadwalOnline ketika klik kembali maka akan ke Class Penjadwalan.
            when (currentFragment) {
                is jadwalOffline, is jadwalOnline -> {
                    bottomNavigationView.selectedItemId = R.id.checked
                    replaceFragment(Penjadwalan())
                }
                is Home -> {
                    finish()
                }
                else -> {
                    bottomNavigationView.selectedItemId = R.id.home
                    replaceFragment(Home())
                }
            }
        }
    }

    // Replace Fragment berfungsi menempatkan tampilan dari kelas-kelas yag sudah di manipulasi.
    private fun replaceFragment(fragment: Fragment) {
        if (isDestroyed || supportFragmentManager.isStateSaved) {
            return  // Jangan lakukan transaksi jika Activity sedang dihancurkan atau state sudah disimpan
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()  // Gunakan ini untuk menghindari crash
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan Listener untuk menghindari memory leak
        listener11?.remove()
        listener12?.remove()
        //  listener13?.remove()
        //  listener14?.remove()
    }

    // Pengecekan internet lewat On Resume
    override fun onResume() {
        super.onResume()
        // Register BroadcastReceiver
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    // Pengecekan internet lewat On Pause
    override fun onPause() {
        super.onPause()
        // Unregister BroadcastReceiver
        unregisterReceiver(networkChangeReceiver)
    }
}

























//private fun getMyReview(no: ArrayList<String>){
//    Log.d("MASUK MAIN", "${no}")
//    no.forEach { nomor ->
//        db.collection("review").document("${nomor}").addSnapshotListener { snapshot, exception ->
//            if (exception != null) {
//                Log.e("Firestore", "Error mendapatkan snapshot real-time", exception)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && snapshot.exists()) {
//                val dbReview = snapshot.data // Map<String, Any> dari dokumen
//                datasReview.removeAll { it.nomorPendaftaran == "${nomor}" }
//
//                datasReview.add(
//                    SelesaiDetailReviewUser(
//                        nomorPendaftaran = "${nomor}",
//                        pengalaman = "${dbReview?.get("pengalaman")}",
//                        tag = "${dbReview?.get("tag")}",
//                        rateStar = (dbReview?.get("rate") as? Int) ?: 0, // Pastikan null-safe untuk Int
//                    )
//                )
//
//            } else {
//                Log.d("Firestore", "Snapshot kosong atau dokumen tidak ada untuk $no.")
//            }
//        }
//    }
//}






//db.collection("review").document(documentId).get().addOnSuccessListener { doc ->
//    if (doc != null && doc.exists()) {
//        val dbReview = doc.data // Ini berisi data dokumen dalam bentuk Map<String, Any> jika ada
//
//
//        datasReview.add(
//            SelesaiDetailReviewUser(
//                konselor = "${dbReview?.get("konselor")}",
//                nama = "${namaprofile}",
//                layanan = "${dbReview?.get("layanan")}",
//                tanngalHari = "${dbReview?.get("tanngalHari")}",
//                pengalaman = "${dbReview?.get("pengalaman")}",
//                onoff = "${dbReview?.get("onoff")}" ,
//                rate = dbReview?.get("rate") as Int ,
//                kepuasan = "${dbReview["kepuasan"]}",
//            )
//        )
//
//        sharedViewModel.updateDataSelesai(datasSelesai)
//        sharedViewModel.updateDataDetailReview(datasReview)
//    } else {
//        datasSelesai.add(
//            SelesaiData(
//                nomorPendaftaran = "${documentId}",
//                name = "${data?.get("")}",
//                topik = "${data?.get("pilihTopikKonsultasi")}",
//                offOrOn = "${data?.get("onoff")}",
//                isiCurhatan = "${data?.get("isiCurhatan")}",
//                konselor = "${data?.get("konselor")}",
//                cek = false,
//            )
//        )
//
//        sharedViewModel.updateDataSelesai(datasSelesai)
//    }
//}.addOnFailureListener { exception ->
//    Log.e("Firestore", "Error mendapatkan dokumen", exception)
//}






//    override fun onBackPressed() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)
//
//        when (currentFragment) {
//            is jadwalOffline, is jadwalOnline -> {
//                if (currentFragment !is Penjadwalan) { // Cek apakah fragment tujuan sudah aktif
//                    bottomNavigationView.selectedItemId = R.id.checked
//                    replaceFragment(Penjadwalan())
//                }
//            }
//            is Home -> {
//                finish()
//            }
//            else -> {
//                if (currentFragment !is Home) { // Cek apakah fragment tujuan sudah aktif
//                    bottomNavigationView.selectedItemId = R.id.home
//                    replaceFragment(Home())
//                }
//            }
//        }
//    }


//    override fun onBackPressed() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)
//
//        when(currentFragment){
//            is jadwalOffline, is jadwalOnline -> {
//                bottomNavigationView.selectedItemId = R.id.checked
//                replaceFragment(Penjadwalan())
//            }
//            is Home -> {
//                finish()
//            }
//            else -> {
//                bottomNavigationView.selectedItemId = R.id.home
//                replaceFragment(Home())
//            }
//        }
//    }


//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//                when (item.itemId) {
//                    R.id.home -> {
//                        // val fragment = Home.newInstance("${namaprofile}")
//                        val fragment = Home()
//                        replaceFragment(fragment)
//                        true
//                    }
//                    R.id.checked -> {
//                        val fragment = Penjadwalan.inputUIDS(myUid)
//                        replaceFragment(fragment)
//                        true
//                    }
//                    R.id.history -> {
//                        val fragment = Riwayat()
//                        replaceFragment(fragment)
//                        true
//                    }
//                    R.id.profil -> {
//                        val fragment = Profile.newInstance("${namaprofile}", "${domisili}", "${favTopik}")
//                        replaceFragment(fragment)
//                        true
//                    }
//                    else -> false
//                }
//        }



//        Log.d("Asd", "${currentFragment}")
//        Log.d("Asd", "${bottomNavigationView.selectedItemId}")



//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.nav_host_fragment, fragment)
//            .addToBackStack(null)
//            .commit()
//    }

//    private lateinit var networkChangeReceiver: NetworkChangeReceiver
//    override fun onResume() {
//        super.onResume()
//        // Register BroadcastReceiver
//        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(networkChangeReceiver, intentFilter)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Unregister BroadcastReceiver
//        unregisterReceiver(networkChangeReceiver)
//    }


//db.collection("datas").document(auth.uid).get().addOnSuccessListener { data ->
//    namaprofile = data.getString("nama")
//    favTopik = data.getString("selectTopik")
//    domisili = data.getString("domisili")
//    sharedViewModel.namaUser.value = data.getString("nama")
//    replaceFragment(Home.newInstance(namaprofile ?: ""))
//}
//
//db.collection("daftarKonseling").document(auth.uid).get().addOnSuccessListener { data ->
//    if(data.exists()) {
//        sharedViewModel.riwayatStatusButuhPersetujuan.value = data.getBoolean("butuhPersetujuan")
//        sharedViewModel.riwayatStatusDisetujui.value = data.getBoolean("diSetujui")
//
//        sharedViewModel.riwayatTopik.value = data.getString("pilihTopikKonsultasi")
//        sharedViewModel.riwayatOffOrOn.value = data.getString("onoff")
//        sharedViewModel.riwayatDescTopik.value = data.getString("isiCurhatan")
//        sharedViewModel.riwayatNoPendaftaran.value = data.getString("nomorPendaftaran")
//        sharedViewModel.statusPendaftaran.value = false
//    } else {
//        sharedViewModel.statusPendaftaran.value = true
//    }
//}


