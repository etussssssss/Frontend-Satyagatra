package com.example.projekconsultant.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.AdapterAdminLayananRiwayatItem
import com.example.projekconsultant.adapter.AdapterAdminRiwayatItem
import com.example.projekconsultant.myfragment.RiwayatTidakAdaJadwalDisetujui
import com.google.android.material.bottomnavigation.BottomNavigationView



class RiwayatPovAdminOnline : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("ARG_PARAM1")
            param2 = it.getString("ARG_PARAM2")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = RiwayatPovAdminOnline().apply {
            arguments = Bundle().apply {
                putString("ARG_PARAM1", param1)
                putString("ARG_PARAM2", param2)
            }
        }
    }

    private var dataPendaftaranOnline:ArrayList<DataPendaftaran>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat_pov_admin_online, container, false)
        val sharedDatas = ViewModelProvider(requireActivity())[SharedViewModelAdmin::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        sharedDatas.dataPendaftaran.observe(viewLifecycleOwner) { updatedData ->
            val butuhPersetujuanAdmin = ArrayList<DataPendaftaran>()
            val diSetujuiAdmin = ArrayList<DataPendaftaran>()

            updatedData.filter { it.onoff == "Online" }.forEach { data ->
                if (data.ButuhPersetujuan == true) {
                    butuhPersetujuanAdmin.add(data)
                } else if (data.DiSetujui == true) {
                    diSetujuiAdmin.add(data)
                }
            }

            // Data menu item
            val menuItems = listOf(
                AdapterAdminLayananRiwayatItem("Butuh Persetujuan", if (butuhPersetujuanAdmin.isNotEmpty()) OnlineRiwayatButuhPersetujuanPovAdmin.dataButuhPersetujuan(butuhPersetujuanAdmin)
                else RiwayatTidakAdaJadwalDisetujui()),
                AdapterAdminLayananRiwayatItem("Di Setujui", if (diSetujuiAdmin.isNotEmpty()) OnlineRiwayatDisetujuiPovAdmin.dataDiSetujui(diSetujuiAdmin)
                else RiwayatTidakAdaJadwalDisetujui()),
            )

            // Update adapter RecyclerView
            recyclerView.adapter = AdapterAdminRiwayatItem(menuItems) { menuItem ->
                navigateToFragment(menuItem.fragment)
            }

            // Navigasi fragment setelah RecyclerView diupdate
            recyclerView.post {
                navigateToFragment(menuItems.first().fragment)
                (recyclerView.adapter as? AdapterAdminRiwayatItem)?.setDefaultSelection()
            }
        }

        // Mengatur aksi tombol back
        view.findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, RiwayatPovAdminFragment()).commit()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.history
        }

        // if(sharedDatas.dataPendaftaran.value?.find { it.onoff == "Online" } == null){
        //    navigateToFragment(RiwayatTidakAdaJadwalDisetujui())
        // }

        return view
    }


    private fun navigateToFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment) // Ganti fragment
        transaction.addToBackStack(null) // Menambahkan transaksi ke back stack
        transaction.commit() // Terapkan transaksi
    }
}


//private val online:String = "Online"
//private val dat = arrayListOf(
//    DataPendaftaran(
//        "S-21","Sza",
//        "27 Desember","Zoom Meeting (Online)",
//        "Agama","Jadi Gini Curhat Agama",
//        true,false, online),
//    DataPendaftaran(
//        "Z-22","Zara","" +
//                "22 Desember","Via Chat WA (Online)",
//        "Keluarga","Saya Kenapa ya, kadang rasanya capek banget sama semua? Tugas kuliah numpuk, teman-teman suka berubah-ubah, dan orang tua juga ",
//        true,false, online),
//    DataPendaftaran(
//        "W-23","Wakus",
//        "21 Desember","Via Video Call (Online)",
//        "Finansial","Jadi Gini Curhat Finansial",
//        true,false, online),
//)
//
//
//private val dat2 = arrayListOf(
//    DataPendaftaran(
//        "S-21","Pangki",
//        "27 Desember","Zoom Meeting (Online)",
//        "Agama","Jadi Gini Curhat Agama",
//        true,false, online),
//    DataPendaftaran(
//        "Z-22","Caca","" +
//                "22 Desember","Via Chat WA (Online)",
//        "Keluarga","Saya Kenapa ya, kadang rasanya capek banget sama semua? Tugas kuliah numpuk, teman-teman suka berubah-ubah, dan orang tua juga ",
//        true,false, online),
//    DataPendaftaran(
//        "W-23","Marica",
//        "21 Desember","Via Video Call (Online)",
//        "Finansial","Jadi Gini Curhat Finansial",
//        true,false, online),
//)



//        var butuhPersetujuanAdmin:ArrayList<DataPendaftaran> = dat
//        var diSetujuiAdmin:ArrayList<DataPendaftaran> = dat2



//private val online:String = "Online"
//private val dat = arrayListOf(
//    DataPendaftaran(
//        "S-21","Sza",
//        "27 Desember","Zoom Meeting (Online)",
//        "Agama","Jadi Gini Curhat Agama",
//        true,false, online),
//    DataPendaftaran(
//        "Z-22","Zara","" +
//                "22 Desember","Via Chat WA (Online)",
//        "Keluarga","Saya Kenapa ya, kadang rasanya capek banget sama semua? Tugas kuliah numpuk, teman-teman suka berubah-ubah, dan orang tua juga ",
//        true,false, online),
//    DataPendaftaran(
//        "W-23","Wakus",
//        "21 Desember","Via Video Call (Online)",
//        "Finansial","Jadi Gini Curhat Finansial",
//        true,false, online),
//)



//val dats = arrayListOf(
//    AdminDataPendaftaranUser("Online","Wily Ahmad", "Curhat Remaja"),
//    AdminDataPendaftaranUser("Online","Wily Ahmad", "Keluarga"),
//)

//val dat = arrayListOf(
//    AdminDataPendaftaranUser("Online","Wily Ahmad", "Curhat Remaja"),
//    AdminDataPendaftaranUser("Online","Wily Ahmad", "Keluarga"),
//)