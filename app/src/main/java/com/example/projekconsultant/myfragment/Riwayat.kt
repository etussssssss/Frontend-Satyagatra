package com.example.projekconsultant.myfragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.RiwayatItem
import com.example.projekconsultant.adapter.RiwayatItemAdapter
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import com.example.projekconsultant.admin.DataPendaftaran
import com.example.projekconsultant.data.UserSelesai
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class Riwayat : Fragment() {
    private var thNamee: String? = ""
    private var thTopikk: String? = ""
    private var thLayanan: String? = ""
    private var thTanggal: String? = ""
    private var thOffOrOnn: String? = ""
    private var no: String? = ""
    private var thDescTopikk: String? = ""
    private lateinit var auth: FirebaseAuth

    companion object {
        @JvmStatic
        fun newInstance(name: String) =
            Riwayat().apply {
                arguments = Bundle().apply {
                    putString("NAME", name)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thNamee = it.getString("NAME")
        }
    }
    private var thDataSelesai:ArrayList<SelesaiData> = arrayListOf()
    private var thDataDetail:ArrayList<SelesaiDetailReviewUser> = arrayListOf()
    private lateinit var db: FirebaseFirestore
    private var thDataUserSelesai:ArrayList<UserSelesai> = arrayListOf()
    private var statusButuhPersetujuan:Boolean = false
    private var statusDisetujui:Boolean = false
    private var statusSelesai:Boolean = false
    private lateinit var progbar: ProgressBar
    private lateinit var recy: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)
        //
        auth = Firebase.auth
        val userId = auth.currentUser?.uid
        //
        progbar = view.findViewById(R.id.loading_progbar)
        recy = view.findViewById<RecyclerView>(R.id.recyclerView)

        view.findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            val homeFragment = Home()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.home
        }

        db = FirebaseFirestore.getInstance()
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Observe each LiveData to update UI in real time
        sharedViewModel.riwayatStatusButuhPersetujuan.observe(viewLifecycleOwner) { status ->
            statusButuhPersetujuan = status ?: false
            // Update UI or perform logic based on `statusButuhPersetujuan`
            //Update
            updateMenuItems(userId.toString())
        }

        sharedViewModel.riwayatStatusDisetujui.observe(viewLifecycleOwner) { status ->
            statusDisetujui = status ?: false
            // Update UI or perform logic based on `statusDisetujui`
            //Update
            updateMenuItems(userId.toString())
        }

        sharedViewModel.riwayatStatusSelesai.observe(viewLifecycleOwner) { status ->
            statusSelesai = status ?: false
            // Update UI or perform logic based on `statusSelesai`
            //Update
            updateMenuItems(userId.toString())
        }

        sharedViewModel.namaUser.observe(viewLifecycleOwner) { name ->
            thNamee = name ?: "Nama Default"
            // Update UI if necessary, e.g., textView.text = thNamee
        }

        sharedViewModel.riwayatTopik.observe(viewLifecycleOwner) { topic ->
            thTopikk = topic ?: "Topik Default"
            // Update UI if necessary
        }

        sharedViewModel.riwayatLayanan.observe(viewLifecycleOwner) { service ->
            thLayanan = service ?: "Layanan Default"
            // Update UI if necessary
        }

        sharedViewModel.riwayatTanggal.observe(viewLifecycleOwner) { date ->
            thTanggal = date ?: "Tanggal Default"
            // Update UI if necessary
        }

        sharedViewModel.riwayatOffOrOn.observe(viewLifecycleOwner) { status ->
            thOffOrOnn = status ?: "Status Default"
            // Update UI if necessary
        }

        sharedViewModel.riwayatDescTopik.observe(viewLifecycleOwner) { description ->
            thDescTopikk = description ?: "Deskripsi Default"
            // Update UI if necessary
        }
        sharedViewModel.riwayatNoPendaftaran.observe(viewLifecycleOwner) { description ->
            no = description ?: ""
        }

        sharedViewModel.dataSelesai.value?.forEach { data ->
                db.collection("review").document("${data.nomorPendaftaran}").addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Error mendapatkan snapshot real-time", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val dbReview = snapshot.data // Map<String, Any> dari dokumen
                    // Cek apakah data dengan nomorPendaftaran sudah ada dalam thDataUserSelesai
                    val existingDataIndex = thDataUserSelesai.indexOfFirst { it.selesaidata.nomorPendaftaran == data.nomorPendaftaran }
                    if (existingDataIndex != -1) {
                        // Jika data sudah ada, update data tersebut
                        thDataUserSelesai[existingDataIndex] = UserSelesai(
                            SelesaiData(
                                nomorPendaftaran = "${data.nomorPendaftaran}",
                                name = "${data.name}",
                                topik = "${data.topik}",
                                tanggal = "${data.tanggal}",
                                offOrOn = "${data.offOrOn}",
                                isiCurhatan = "${data.isiCurhatan}",
                                konselor = "${data.konselor}"
                            ),
                            SelesaiDetailReviewUser(
                                nomorPendaftaran = "${data.nomorPendaftaran}",
                                pengalaman = "${dbReview?.get("pengalaman")}",
                                tag = "${dbReview?.get("tag")}",
                                rateStar = (dbReview?.get("star") as? Long)?.toInt() ?: 0,
                            )
                        )
                    } else {
                        // Jika data belum ada, tambahkan data baru
                        thDataUserSelesai.add(
                            UserSelesai(
                                SelesaiData(
                                    nomorPendaftaran = "${data.nomorPendaftaran}",
                                    name = "${data.name}",
                                    topik = "${data.topik}",
                                    tanggal = "${data.tanggal}",
                                    offOrOn = "${data.offOrOn}",
                                    isiCurhatan = "${data.isiCurhatan}",
                                    konselor = "${data.konselor}"
                                ),
                                SelesaiDetailReviewUser(
                                    nomorPendaftaran = "${data.nomorPendaftaran}",
                                    pengalaman = "${dbReview?.get("pengalaman")}",
                                    tag = "${dbReview?.get("tag")}",
                                    rateStar = (dbReview?.get("star") as? Long)?.toInt() ?: 0,
                                )
                            )
                        )
                        //Update
                        updateMenuItems(userId.toString())
                    }
                    // Update data pada ViewModel setelah perubahan
                    sharedViewModel.updateDataUserSelesai(thDataUserSelesai)
                } else {
                    thDataUserSelesai.removeAll { it.selesaidata.nomorPendaftaran == data.nomorPendaftaran }
                    sharedViewModel.updateDataUserSelesai(thDataUserSelesai)
                    // Jika dokumen tidak ada atau snapshot kosong, tambahkan data default
                    thDataUserSelesai.add(
                        UserSelesai(
                            SelesaiData(
                                nomorPendaftaran = "${data.nomorPendaftaran}",
                                name = "${data.name}",
                                topik = "${data.topik}",
                                tanggal = "${data.tanggal}",
                                offOrOn = "${data.offOrOn}",
                                isiCurhatan = "${data.isiCurhatan}",
                                konselor = "${data.konselor}"
                            )
                        )
                    )
                    sharedViewModel.updateDataUserSelesai(thDataUserSelesai)
                }
            }
        }

        //Update
        updateMenuItems(userId.toString())

        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Fungsi untuk memperbarui menu secara real-time
    private fun updateMenuItems(userId: String) {
        progbar.visibility = View.VISIBLE
        // Perbarui menu berdasarkan status terkini
       Handler().postDelayed({
            // Data menu item
            val menuItems = listOf(
                RiwayatItem("Butuh Persetujuan",
                    if (statusButuhPersetujuan) RiwayatButuhPersetujuan.newInstance(
                        thNamee!!, thTopikk!!, thOffOrOnn!!,
                        thDescTopikk!!, thTanggal!!, thLayanan!!,
                        no, userId
                    )
                    else RiwayatTidakAdaJadwalPersetujuan()),
                RiwayatItem("Di Setujui",
                    if (statusDisetujui) RiwayatDisetujui.newInstance(
                        thNamee!!, thTopikk!!, thOffOrOnn!!,
                        thDescTopikk!!, thTanggal!!, thLayanan!!,
                        no, userId
                    )
                    else RiwayatTidakAdaJadwalDisetujui()),
                RiwayatItem("Selesai",
                    if (statusSelesai) RiwayatSelesai()
                    else RiwayatTidakAdaJadwalDisetujui())
            )

            recy.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = RiwayatItemAdapter(menuItems) { menuItem ->
                    navigateToFragment(menuItem.fragment)
                }
            }

            val adapter = RiwayatItemAdapter(menuItems) { menuItem ->
                navigateToFragment(menuItem.fragment)
            }

           recy.adapter = adapter
           progbar.visibility = View.GONE
           recy.post {
                navigateToFragment(menuItems.first().fragment)
                (recy.adapter as? RiwayatItemAdapter)?.setDefaultSelection()
            }
        }, 2500)
    }



    private fun getMyReview(datasReview:ArrayList<SelesaiDetailReviewUser> , no: ArrayList<String>){
        Log.d("MASUK MAIN", "${no}")
        no.forEach { nomor ->
            db.collection("review").document("${nomor}").addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Error mendapatkan snapshot real-time", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val dbReview = snapshot.data // Map<String, Any> dari dokumen
                    datasReview.removeAll { it.nomorPendaftaran == "${nomor}" }
                    datasReview.add(
                        SelesaiDetailReviewUser(
                            nomorPendaftaran = "${nomor}",
                            pengalaman = "${dbReview?.get("pengalaman")}",
                            tag = "${dbReview?.get("tag")}",
                            rateStar = (dbReview?.get("rate") as? Int) ?: 0, // Pastikan null-safe untuk Int
                        )
                    )
                } else {
                    Log.d("Firestore", "Snapshot kosong atau dokumen tidak ada untuk $no.")
                }
            }
        }
    }
}



//        Handler().postDelayed({
//            // Data menu item
//            val menuItems = listOf(
//                RiwayatItem("Butuh Persetujuan",
//                    if (statusButuhPersetujuan) RiwayatButuhPersetujuan.newInstance(
//                        thNamee!!, thTopikk!!, thOffOrOnn!!,
//                        thDescTopikk!!, thTanggal!!, thLayanan!!,
//                        no, userId
//                    )
//                    else RiwayatTidakAdaJadwalPersetujuan()),
//                RiwayatItem("Di Setujui",
//                    if (statusDisetujui) RiwayatDisetujui.newInstance(
//                        thNamee!!, thTopikk!!, thOffOrOnn!!,
//                        thDescTopikk!!, thTanggal!!, thLayanan!!,
//                        no, userId
//                    )
//                    else RiwayatTidakAdaJadwalDisetujui()),
//                RiwayatItem("Selesai",
//                    if (statusSelesai) RiwayatSelesai()
//                    else RiwayatTidakAdaJadwalDisetujui())
//            )
//
//            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
//                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//                adapter = RiwayatItemAdapter(menuItems) { menuItem ->
//                    navigateToFragment(menuItem.fragment)
//                }
//            }
//
//            val adapter = RiwayatItemAdapter(menuItems) { menuItem ->
//                navigateToFragment(menuItem.fragment)
//            }
//
//            recyclerView.adapter = adapter
//            view.findViewById<ProgressBar>(R.id.loading_progbar).visibility = View.GONE
//            recyclerView.post {
//                navigateToFragment(menuItems.first().fragment)
//                (recyclerView.adapter as? RiwayatItemAdapter)?.setDefaultSelection()
//            }
//        }, 2500)





//sharedViewModel.dataSelesai.value?.forEach { data ->
//    db.collection("review").document("${data.nomorPendaftaran}").addSnapshotListener { snapshot, exception ->
//        if (exception != null) {
//            Log.e("Firestore", "Error mendapatkan snapshot real-time", exception)
//            return@addSnapshotListener
//        }
//
//        if (snapshot != null && snapshot.exists()) {
//            val dbReview = snapshot.data // Map<String, Any> dari dokumen
//            // Cek apakah data dengan nomorPendaftaran sudah ada dalam thDataUserSelesai
//            val existingDataIndex = thDataUserSelesai.indexOfFirst { it.selesaidata.nomorPendaftaran == data.nomorPendaftaran }
//            if (existingDataIndex != -1) {
//                // Jika data sudah ada, update data tersebut
//                thDataUserSelesai[existingDataIndex] = UserSelesai(
//                    SelesaiData(
//                        nomorPendaftaran = "${data.nomorPendaftaran}",
//                        name = "${data.name}",
//                        topik = "${data.topik}",
//                        tanggal = "${data.tanggal}",
//                        offOrOn = "${data.offOrOn}",
//                        isiCurhatan = "${data.isiCurhatan}",
//                        konselor = "${data.konselor}"
//                    ),
//                    SelesaiDetailReviewUser(
//                        nomorPendaftaran = "${data.nomorPendaftaran}",
//                        pengalaman = "${dbReview?.get("pengalaman")}",
//                        tag = "${dbReview?.get("tag")}",
//                        rateStar = (dbReview?.get("star") as? Long)?.toInt() ?: 0,
//                    )
//                )
//            } else {
//                // Jika data belum ada, tambahkan data baru
//                thDataUserSelesai.add(
//                    UserSelesai(
//                        SelesaiData(
//                            nomorPendaftaran = "${data.nomorPendaftaran}",
//                            name = "${data.name}",
//                            topik = "${data.topik}",
//                            tanggal = "${data.tanggal}",
//                            offOrOn = "${data.offOrOn}",
//                            isiCurhatan = "${data.isiCurhatan}",
//                            konselor = "${data.konselor}"
//                        ),
//                        SelesaiDetailReviewUser(
//                            nomorPendaftaran = "${data.nomorPendaftaran}",
//                            pengalaman = "${dbReview?.get("pengalaman")}",
//                            tag = "${dbReview?.get("tag")}",
//                            rateStar = (dbReview?.get("star") as? Long)?.toInt() ?: 0,
//                        )
//                    )
//                )
//            }
//            // Update data pada ViewModel setelah perubahan
//            sharedViewModel.updateDataUserSelesai(thDataUserSelesai)
//        } else {
//            thDataUserSelesai.removeAll { it.selesaidata.nomorPendaftaran == data.nomorPendaftaran }
//            sharedViewModel.updateDataUserSelesai(thDataUserSelesai)
//            // Jika dokumen tidak ada atau snapshot kosong, tambahkan data default
//            thDataUserSelesai.add(
//                UserSelesai(
//                    SelesaiData(
//                        nomorPendaftaran = "${data.nomorPendaftaran}",
//                        name = "${data.name}",
//                        topik = "${data.topik}",
//                        tanggal = "${data.tanggal}",
//                        offOrOn = "${data.offOrOn}",
//                        isiCurhatan = "${data.isiCurhatan}",
//                        konselor = "${data.konselor}"
//                    )
//                )
//            )
//            sharedViewModel.updateDataUserSelesai(thDataUserSelesai)
//        }
//    }
//}
















//val db = FirebaseFirestore.getInstance()
//val auth = FirebaseAuth.getInstance().currentUser

//    private fun navigateToFragment(fragment: Fragment) {
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragmentContainer, fragment) // Pastikan ID ini sesuai dengan yang di layout XML
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }


//    private fun navigateToFragment(fragment: Fragment) {
////        parentFragmentManager.beginTransaction()
//
//        val transaction = parentFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragmentContainer, fragment) // Ganti fragment
//        transaction.addToBackStack(null) // Menambahkan transaksi ke back stack
//        transaction.commit() // Terapkan transaksi
//
//    }


//val menuItems = listOf(
//            RiwayatItem("Butuh Persetujuan", if (true) RiwayatButuhPersetujuan() else RiwayatTidakAdaJadwal()),
//            RiwayatItem("Di Setujui", RiwayatDisetujui()),
//            RiwayatItem("Selesai", RiwayatSelesai()),
//        )
//
//        // Setup RecyclerView
//        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
//        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        recyclerView.layoutManager = layoutManager
//
//        navigateToFragment(if (true) RiwayatButuhPersetujuan() else RiwayatTidakAdaJadwal())
//
//        val adapter = RiwayatItemAdapter(menuItems) { menuItem ->
//            navigateToFragment(menuItem.fragment)
//        }
//
//        recyclerView.adapter = adapter
//        recyclerView.post {
//            adapter.setDefaultSelection() // Memastikan item pertama dipilih
//        } // Memastikan item pertama dipilih