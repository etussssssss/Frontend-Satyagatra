package com.example.projekconsultant.admin

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.ReviewItem
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.adapter3sealedclass.ChatsUsers
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MainAdminActivity : BaseActivity() {
    private  var email:String? = ""
    private  var namaprofile:String? = "Anonymous"
    private  var tanggalLahir:String? = ""
    private  var domisili:String? = "DKI Jaka"
    private  var gender:String? = ""
    private  var nomorTelp:String? = ""

    private  var favTopik:String? = ""

    private var listener: ListenerRegistration? = null
    private var listener2: ListenerRegistration? = null
    private var listener3: ListenerRegistration? = null
    private var listener4: ListenerRegistration? = null

    private var dataNNChat:ArrayList<ChatsUsers> = ArrayList()
    private var dataPendaftaran:ArrayList<DataPendaftaran> = arrayListOf()

    private var dataChange:ArrayList<Long> = ArrayList()
    private lateinit var dataChat: ArrayList<String>
    private lateinit var noPendaftaran:ArrayList<String>
    private var datasRiwayatKonsulDanKonselor:ArrayList<DataRiwayatSelesaiAdmin> = ArrayList()
    private var datasReviewHome:ArrayList<ReviewItem> = ArrayList()

    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)

        // Variable shared view model untuk mengambil data yang sudah di manipulasi untuk live data.
        // Non Model Admin
        val sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance().currentUser

        //
        // Model Admin
        val sharedDatasAdmin = ViewModelProvider(this)[SharedViewModelAdmin::class.java]

        if (auth != null) {
            //
            db.collection("datas").document(auth.uid).get().addOnSuccessListener { data ->
                namaprofile = data.getString("nama")
                tanggalLahir = data.getString("tanggalLahir")
                domisili = data.getString("domisili")
                gender = data.getString("gender")
                nomorTelp = data.getString("nomortelepon")

                favTopik = data.getString("selectTopik")

                replaceFragment(HomeAdmin.newInstance(namaprofile ?: ""))
            }
            //

            //
            listener2 = db.collection("daftarKonseling").addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FirestoreError", "Error Daftar Konseling: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots.documentChanges) {
                        when (doc.type) {
                            // Tambahkan logika untuk menangani data baru
                            DocumentChange.Type.ADDED -> {
                                val data = doc.document.data
                                // Log.d("Firestore", "Data Added DaftarKonseling: ${data.get("nomorPendaftaran")}")
                                Log.d("Firestore", "Data Added DaftarKonseling")


                                dataNNChat.add(
                                    ChatsUsers(
                                        namaUser = "${data.get("nama")}",
                                        //noPendaftaran = "${data.get("nomorPendaftaran")}",
                                        noRuid = "${data.get("uid")}",
                                        butuhPersetujuan = data.get("butuhPersetujuan") as? Boolean ?: false,
                                        diSetujui = data.get("diSetujui") as? Boolean ?: false
                                    )
                                )

                                dataPendaftaran.add(
                                    DataPendaftaran(
                                        uuid = "${data.get("uid")}",
                                        nomorPendaftaran = "${data.get("nomorPendaftaran")}",

                                        nomorTeleponUser = "${data.get("nomortelepon")}",
                                        nama = "${data.get("nama")}",
                                        umur = "${data.get("umur")}",
                                        gender = "${data.get("gender")}",
                                        type = "${data.get("typeIOrE")}",

                                        profesi = "${data.get("profesi")}",
                                        fakultas = "${data.get("fakultas")}",
                                        status = "${data.get("status")}",
                                        domisili = "${data.get("domisili")}",

                                        PilihTanggal = "${data.get("pilihTanggal")}",
                                        PilihLayananKonsultasi = "${data.get("pilihLayananKonsultasi")}",
                                        PilihTopikKonsultasi = "${data.get("pilihTopikKonsultasi")}",
                                        IsiCurhatan = "${data.get("isiCurhatan")}",

                                        ButuhPersetujuan = data.get("butuhPersetujuan") as? Boolean ?: false,
                                        DiSetujui = data.get("diSetujui") as? Boolean ?: false,

                                        onoff = "${data.get("onoff")}",
                                        konselor =  "${data.get("konselor")}"
                                    )
                                )

                                sharedDatasAdmin.updateDataChat(dataNNChat)
                                sharedDatasAdmin.updateDataPendaftaran(dataPendaftaran)
                            }
                            // Tambahkan logika untuk menangani data yang diubah
                            DocumentChange.Type.MODIFIED -> {
                                val data = doc.document.data
                                Log.d("Firestore", "Data diubah DaftarKonseling: $data")

                                //Check No Pendaftaran
                                val nomorPendaftaran = data.get("nomorPendaftaran") ?: ""
                                //Check Ruid
                                val ruid = data.get("uid") ?: ""

                                // Cari index data yang sesuai berdasarkan nomorPendaftaran di dataNN
                                val existingDataIndexN = dataNNChat.indexOfFirst { it.noRuid == ruid }
                                // Cari index data yang sesuai berdasarkan nomorPendaftaran
                                val existingDataIndexP = dataPendaftaran.indexOfFirst { it.nomorPendaftaran == nomorPendaftaran }

                                // Jika data sudah ada, perbarui data tersebut
                                if (existingDataIndexN != -1) {
                                    // Perbarui data pada dataNN
                                    dataNNChat[existingDataIndexN] = ChatsUsers(
                                        namaUser = "${data.get("nama")}",
                                        // noPendaftaran = "${data.get("nomorPendaftaran")}",
                                        noRuid = "${data.get("uid")}",
                                        butuhPersetujuan = data.get("butuhPersetujuan") as Boolean,
                                        diSetujui = data.get("diSetujui") as Boolean
                                    )
                                }

                                // Jika data sudah ada, perbarui data tersebut
                                if (existingDataIndexP != -1) {
                                    dataPendaftaran[existingDataIndexP] = DataPendaftaran(
                                        uuid = "${data.get("uid")}",
                                        nomorPendaftaran = "${nomorPendaftaran}",

                                        nomorTeleponUser = "${data.get("nomortelepon")}",
                                        nama = "${data.get("nama")}",
                                        umur = "${data.get("umur")}",
                                        gender = "${data.get("gender")}",
                                        type = "${data.get("typeIOrE")}",

                                        profesi = "${data.get("profesi")}",
                                        fakultas = "${data.get("fakultas")}",
                                        status = "${data.get("status")}",
                                        domisili = "${data.get("domisili")}",

                                        PilihTanggal = "${data.get("pilihTanggal")}",
                                        PilihLayananKonsultasi = "${data.get("pilihLayananKonsultasi")}",
                                        PilihTopikKonsultasi = "${data.get("pilihTopikKonsultasi")}",
                                        IsiCurhatan = "${data.get("isiCurhatan")}",

                                        ButuhPersetujuan = data.get("butuhPersetujuan") as? Boolean ?: false,
                                        DiSetujui = data.get("diSetujui") as? Boolean ?: false,

                                        onoff = "${data.get("onoff")}",
                                        konselor = "${data.get("konselor")}"
                                    )
                                }

                                sharedDatasAdmin.updateDataChat(dataNNChat)
                                sharedDatasAdmin.updateDataPendaftaran(dataPendaftaran)
                            }
                            // Tambahkan logika untuk menangani data yang dihapus
                            DocumentChange.Type.REMOVED -> {
                                val data = doc.document.data
                                Log.d("Firestore", "Data dihapus DaftarKonseling: $data")

                                //Check No Pendaftaran
                                val nomorPendaftaran = data.get("nomorPendaftaran") ?: ""
                                //Check Ruid
                                val ruid = data.get("uid") ?: ""

                                // Remove Data Array Penjadwalan Di Riwayat
                                val dataToRemoveP = dataPendaftaran.find { it.nomorPendaftaran == nomorPendaftaran }
                                // Remove Data Array Chat
                                val dataToRemoveN = dataNNChat.find { it.noRuid == ruid }

                                // Jika data ditemukan, hapus
                                if (dataToRemoveP != null) {
                                    dataPendaftaran.remove(dataToRemoveP)
                                }

                                // Remove Data Array Chat
                                if (dataToRemoveN != null) {
                                    dataNNChat.remove(dataToRemoveN)
                                }

                                sharedDatasAdmin.updateDataChat(dataNNChat)
                                sharedDatasAdmin.updateDataPendaftaran(dataPendaftaran)
                            }
                        }
                    }
                }
            }


            //Pengambilan Data Review Konseling si user secara public.
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
                Log.e("Firestore", "Error mengambil data dari Firestore: ${exception.message}")
            }



           listener4 = db.collectionGroup("allRiwayat").addSnapshotListener { snapshots, error ->
               if (error != null) {
                   Log.e("RIWAYAT", "Error fetching snapshots: ${error.message}")
                   return@addSnapshotListener
               }

               if (snapshots != null && !snapshots.isEmpty) {
                   for (document in snapshots.documents) {
                       val data = document.data // Mendapatkan isi data dokumen

                       // Memeriksa apakah data kosong atau null
                       if (data.isNullOrEmpty()) {
                           Log.d("RIWAYAT KOSONG", "Document ID: ${document.id}")
                       } else {
                           val nomorPendaftaranUser = document.id
                           val namaUser = data["nama"]?.toString().orEmpty()
                           val namaKonselor = data["konselor"]?.toString().orEmpty()
                           val topikKonsultasi = data["pilihTopikKonsultasi"]?.toString().orEmpty()
                           val tanggalSelesaiUser = data["pilihTanggal"]?.toString().orEmpty()
                           val layananOnlineOrOfflineUser = data["onoff"]?.toString().orEmpty()
                           val layananKonsultasi = data["pilihLayanan"]?.toString().orEmpty()
                           val curhatanUser = data["isiCurhatan"]?.toString().orEmpty()
                           val typeUser = when (data["type"]?.toString()) {
                               "I" -> "Internal"
                               "E" -> "Eksternal"
                               else -> ""
                           }
                           val umur = data["umur"] as? Long
                           val umurInt = umur?.toInt() ?: 0  // Mengonversi Long ke Int atau default ke 0 jika null
                           val profesi = data["profesi"]?.toString().orEmpty()
                           val fakultas = data["fakultas"]?.toString().orEmpty()
                           val gender = data["gender"]?.toString().orEmpty()
                           val status = data["status"]?.toString().orEmpty()
                           val domisili = data["domisili"]?.toString().orEmpty()


                           // Menambahkan data ke dalam list jika lengkap
                           if (namaUser.isNotEmpty() && topikKonsultasi.isNotEmpty() && tanggalSelesaiUser.isNotEmpty()) {
                               datasRiwayatKonsulDanKonselor.add(
                                   DataRiwayatSelesaiAdmin(
                                       nomorPendaftaranUser = nomorPendaftaranUser,
                                       namaUser = namaUser,
                                       namaKonselor = namaKonselor,
                                       topikKonsultasi = topikKonsultasi,
                                       tanggalSelesaiUser = tanggalSelesaiUser,
                                       layananOnlineOrOfflineUser = layananOnlineOrOfflineUser,
                                       layananKonsultasi = layananKonsultasi,
                                       curhatanUser = curhatanUser,
                                       typeUser = typeUser,
                                       umur = umurInt,
                                       profesi = profesi,
                                       fakultas = fakultas,
                                       gender = gender,
                                       status = status,
                                       domisili = domisili
                                   )
                               )
                               sharedDatasAdmin.updateDataRiwayatSelesaiAdmin(datasRiwayatKonsulDanKonselor)
                               //Log.d("MAIN ADMIN", "${datasRiwayatKonsulDanKonselor}")
                           }
                       }
                   }
               } else {
                   Log.d("RIWAYAT", "No documents found.")
               }
           }
        }

        // Replace Default
        replaceFragment(HomeAdmin.newInstance(namaprofile ?: ""))
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeAdmin.newInstance("${namaprofile}"))
                    true
                }
                R.id.chat -> {
                    replaceFragment(ForumChatAdminFragment())
                    true
                }
                R.id.history -> {
                    replaceFragment(RiwayatPovAdminFragment())
                    true
                }
                R.id.profil -> {
                    replaceFragment(ProfilePovAdminFragment.newInstanceThree("${namaprofile}","${domisili}","${favTopik}"))
                    true
                }
                else -> false
            }
        }

        // Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }else{

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan Listener untuk menghindari memory leak
        listener2?.remove()
        listener4?.remove()
    }

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

    // Untuk Tampilan main activity admin masih menggunakan onBackPressed berbeda dengan main activity user sudah menggunakan on Call Back
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)
        
        if(currentFragment is RiwayatPovAdminOnline || currentFragment is RiwayatPovAdminOffline){
            bottomNavigationView.selectedItemId = R.id.history
            replaceFragment(RiwayatPovAdminFragment())
            super.onBackPressed()
        }else if(currentFragment is HomeAdmin){
            finish()
        } else {
            bottomNavigationView.selectedItemId = R.id.home
            replaceFragment(HomeAdmin())
            super.onBackPressed() // Jika bukan Riwayat, lanjutkan seperti biasa
        }
    }

    // Replace Fragment berfungsi menempatkan tampilan dari kelas-kelas yag sudah di manipulasi.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
















//listener4 = db.collection("riwayat").addSnapshotListener { snapshot, error ->
//    if (error != null) {
//        Log.w("Firestore Riwayat", "Error: ", error)
//        return@addSnapshotListener
//    }
//
//    Log.d("Firestore Riwayat", "Data ditemukan, jumlah dokumen: ${snapshot?.size()}")
//
//
//    // Cek apakah snapshot berisi data
//    if (snapshot == null || snapshot.isEmpty) {
//        Log.d("Firestore Riwayat", "Tidak ada data di koleksi riwayat.")
//    } else {
//        Log.d("Firestore Riwayat", "Data ditemukan, jumlah dokumen: ${snapshot.size()}")
//        snapshot.forEach { user ->
//            // Real-time listener untuk sub-collection 'allRiwayat'
//            user.reference.collection("allRiwayat").addSnapshotListener { idRiwayat, error ->
//                if (error != null) { Log.w("Firestore Riwayat", "Error: ", error)
//                    return@addSnapshotListener
//                }
//
//                Log.d("Firestore Riwayat", "DATAS: ${user.id}")
//                // Cek apakah sub-koleksi ada data
//                if (idRiwayat == null || idRiwayat.isEmpty) {
//                    Log.d("Firestore Riwayat", "Tidak ada data di sub-collection allRiwayat.")
//                } else {
//                    idRiwayat.forEach { datass ->
//                        datasRiwayatKonsulDanKonselor.add(
//                            DataRiwayatSelesaiAdmin(
//                                nomorPendaftaranUser = "${idRiwayat}",
//                                namaUser = "${datass.get("nama")}",
//                                topikKonsultasi = "${datass.get("pilihTopikKonsultasi")}",
//                                tanggalSelesaiUser = "${datass.get("pilihTanggal")}",
//                                layananOnlineOrOfflineUser = "${datass.get("onoff")}",
//                                layananKonsultasi = "${datass.get("pilihLayanan")}",
//                                curhatanUser = "${datass.get("isiCurhatan")}",
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
//}











//            db.collection("riwayats").document("SJ24083FRafbjJsJcePKOqtBKK13").collection("allRiwayat")
//            db.collection("riwayats").document("idSayaRahasia").collection("allRiwayat")
//                .orderBy("pilihTanggal", Query.Direction.DESCENDING).get()
//                .addOnSuccessListener { snapshots ->
//                    Log.d("FirestoreGet", "Snapshot size: ${snapshots.size()}")
//                    if (!snapshots.isEmpty) {
//                        for (document in snapshots.documents) {
//                            val data = document.data
//                            val documentId = document.id
//                            Log.d("FirestoreGet", "Document ID: $documentId")
//                        }
//                    } else {
//                        Log.d("FirestoreGet", "Tidak ada data atau snapshot kosong")
//                    }
//                }.addOnFailureListener { error ->
//                    Log.e("FirestoreGet", "Gagal mendapatkan data: ${error.message}")
//                }






//val dataDaftar = arrayListOf(
//    DataPendaftaran(
//        nomorPendaftaran = "W-123", nama = "Wily",
//        PilihTanggal = "26 Desember 2024", PilihLayananKonsultasi = "Janji Tatap Muka/Offline",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//        ButuhPersetujuan = true, onoff = "Offline", DiSetujui = false
//    ),
//    DataPendaftaran(
//        nomorPendaftaran = "B-124", nama = "Bardul",
//        PilihTanggal = "28 Desember 2024", PilihLayananKonsultasi = "Via Chat",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//        ButuhPersetujuan = true, onoff = "Online", DiSetujui = false
//    ),
//    DataPendaftaran(
//        nomorPendaftaran = "C-124", nama = "Jakis",
//        PilihTanggal = "28 Desember 2024", PilihLayananKonsultasi = "Via Chat",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//        ButuhPersetujuan = true, onoff = "Online", DiSetujui = false
//    ),
//    DataPendaftaran(
//        nomorPendaftaran = "D-124", nama = "Isa",
//        PilihTanggal = "28 Desember 2024", PilihLayananKonsultasi = "Via Chat",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//        ButuhPersetujuan = true, onoff = "Online", DiSetujui = false
//    ),
//)


//val dataChats = arrayListOf(
//    ChatsUsers(
//        namaUser = "Adam",
//        noPendaftaran = "A-123",
//        butuhPersetujuan = false,
//        diSetujui = true
//    ),
//    ChatsUsers(
//        namaUser = "Ilham",
//        noPendaftaran = "I-321",
//        butuhPersetujuan = false,
//        diSetujui = true
//    ),
//)

//val dataDaftar = arrayListOf(
//    DataPendaftaran(
//        nomorPendaftaran = "W-123", nama = "Wily",
//        PilihTanggal = "26 Desember 2024", PilihLayananKonsultasi = "Janji Tatap Muka/Offline",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//        ButuhPersetujuan = true, onoff = "Offline", DiSetujui = false
//    ),
//    DataPendaftaran(
//        nomorPendaftaran = "B-124", nama = "Bardul",
//        PilihTanggal = "28 Desember 2024", PilihLayananKonsultasi = "Via Chat",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//        ButuhPersetujuan = true, onoff = "Offline", DiSetujui = false
//    ),
//    DataPendaftaran(
//        nomorPendaftaran = "C-124", nama = "Jakis",
//        PilihTanggal = "28 Desember 2024", PilihLayananKonsultasi = "Via Chat",
//        PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd",
//        ButuhPersetujuan = true, onoff = "Offline", DiSetujui = false
//    ),
//        DataPendaftaran(
//            nomorPendaftaran = "D-124", nama = "Isa",
//            PilihTanggal = "28 Desember 2024", PilihLayananKonsultasi = "Via Chat",
//            PilihTopikKonsultasi = "Motivasi Belajar", IsiCurhatan = "Mendapatkan Nilai Bagus",
//            ButuhPersetujuan = true, onoff = "Online", DiSetujui = false
//        ),
//)





//private val dat = arrayListOf(
//    DataPendaftaran(
//        "S-21","Sza",
//        "27 Desember","Zoom Meeting (Online)",
//        "Agama","Jadi Gini Curhat Agama",
//        true,false, "Online"),
//    DataPendaftaran(
//        "Z-22","Zara","" +
//                "22 Desember","Via Chat WA (Online)",
//        "Keluarga","Saya Kenapa ya, kadang rasanya capek banget sama semua? Tugas kuliah numpuk, teman-teman suka berubah-ubah, dan orang tua juga ",
//        true,false, "Online"),
//    DataPendaftaran(
//        "W-23","Wakus",
//        "21 Desember","Via Video Call (Online)",
//        "Finansial","Jadi Gini Curhat Finansial",
//        true,false, "Offline"),
//)




//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }