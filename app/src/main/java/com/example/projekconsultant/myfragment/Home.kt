package com.example.projekconsultant.myfragment

import KeunggulanHome
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.KeunggulanHomeAdapter
import com.example.projekconsultant.adapter.ProfileKonselorHome
import com.example.projekconsultant.adapter.ProfileKonselorHomeAdapter
import com.example.projekconsultant.adapter.ReviewItem
import com.example.projekconsultant.adapter.ReviewItemAdapterLast
//import com.example.projekconsultant.methods.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class Home : Fragment() {
    private var myName: String? = ""
    private var myID:String = ""

    private var statusPendaftaran: Boolean? = null

    companion object {
        @JvmStatic
        // Method newInstance berfungsi untuk mengambil nama si user dari db main Activity.
        // Setelah mendapatkan data akan disimpan di put/shared preferences private bundle masing-masing class.
        fun newInstance(namaUser: String?) = Home().apply {
            arguments = Bundle().apply {
                putString("paramNama", namaUser)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Manipulasi variable arguments yang masuk.
        arguments?.let {
            myName = it.getString("paramNama").toString()
            myID   = it.getString("myID").toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Variable shared view model untuk mengambil data yang sudah di manipulasi untuk live data.
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        // Variable myName ini di ambil dari shared view model (live data) yang di ambil di main activity.
        myName = sharedViewModel.namaUser.value

        // Welcome Text untuk user Di home.
        view.findViewById<TextView>(R.id.sayhitouser).setText("Hi, ${myName?.split(" ")?.get(0)}")

        // Live Data Untuk Memaksimalkan Real Time Data Di Front End Kotlin Android.
        // Fungsi Observe viewlifecycleowner ini seperti Looping data yang masuk di db dari Main Activity
        sharedViewModel.statusPendaftaran.observe(viewLifecycleOwner) { status ->
            // Jika ada perubahan status pendaftaran nya maka otomatis variable ini berubah.
            statusPendaftaran = status
        }

        // Button Pilih Jadwal Biru Di Home
        view.findViewById<Button>(R.id.btnpilihjadwal).setOnClickListener {
            //Ke Penjadwalan View
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Penjadwalan.inputUIDS(myID)).commit()
            // Mengatur Bottom Navigation Di Main Activity agar tidak terjadi bug, dan menyesuaikan apa yang di pilih si user ketika klik bottom navigation.
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.checked
        }

        // Recycle View 1 Adalah Untuk Menampilkan Konselor profesional dan staff lain nya, Kita tidak menyimpan nya ke database firestore.
        // Karena ini tidak ada data yang sensitif atau hanya sedikit beberapa saja yang perlu di tampilkan di home.
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHome1)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val profileList = listOf(
            ProfileKonselorHome("dr. Maya Trisiswati, MKM.", R.drawable.s_img_drmaya, listOf("HIV", "Pos Sapa/Kekerasan", "Kesehatan Reproduksi"), R.string.deskripDrMaya),
            ProfileKonselorHome("dr. Liko Maryudhiyanto, Sp.KJ., C.Ht", R.drawable.s_img_drliko, listOf("Motivasi Belajar", "Kesehatan Mental dan Psikologi"), R.string.deskripDrLiko),
            ProfileKonselorHome("dr. Melok Roro Kinanthi, M.Psi., Psikolog", R.drawable.s_img_drmelok, listOf("Keluarga", "Pos Sapa/Kekerasan", "Parenting", "Kesehatan Mental dan Psikologi"), R.string.deskripDrMelok),
            ProfileKonselorHome("dr. Octaviani Indrasari Ranakusuma, M.Si., Psi.", R.drawable.s_img_droktaviani, listOf("Keluarga", "Parenting", "Kesehatan Mental dan psikologi"), R.string.deskripDrOctaviani),
            ProfileKonselorHome("Dr. Miwa Patnani, M.Si., Psikolog", R.drawable.s_img_drmiwa, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrMiwa),
            ProfileKonselorHome("Fitri Arlinkasari, Ph.D., Psikolog", R.drawable.s_img_drfitri, listOf("Agama"), R.string.deskripDrFitriArlinkasari),
            ProfileKonselorHome("Aya Yahya Maulana, Lc, M.H.", R.drawable.s_img_draya, listOf("Gizi"), R.string.deskripDrayaYahya),
            ProfileKonselorHome("dr. Siti Maulidya Sari, M.Epid, Dipl.DK", R.drawable.s_img_drsiti, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrSitiMaulidya),
            ProfileKonselorHome("Dilfa Juniar, M.Psi., Psikolog", R.drawable.s_img_drdiffa, listOf("Motivasi Belajar"), R.string.deskripDrDilfa),
            ProfileKonselorHome("dr. Aan Royhan, MSc, C,Ht", R.drawable.s_img_draan, listOf("Pos Sapa/Kekerasan", "Hukum"), R.string.deskripDrAan),
            ProfileKonselorHome("DR. Yusuf Shofie, S.H., M.H.", R.drawable.s_img_dryusuf, listOf("Pos Sapa/Kekerasan", "Kesehatan Mental dan Psikologi"), R.string.deskripDrYusuf),
            ProfileKonselorHome("Chandradewi Kusristanti, M.Psi., Psikolog", R.drawable.s_img_chandra, listOf("Motivasi Belajar"), R.string.deskripDrChandradewi),
            ProfileKonselorHome("Sari Zakiah Akmal, Ph.D., Psikolog", R.drawable.s_img_drsari, listOf("Motivasi Belajar", "Disabilitas", "Kesehatan Mental dan Psikologi"), R.string.deskripDrSariZakiah),
            ProfileKonselorHome("Alabanyo Brebahama, M.Psi., Psikolog ", R.drawable.s_img_dralabanyo, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrAlabanyo),
            ProfileKonselorHome("dr. Citra Fitri Agustina, Sp.KJ", R.drawable.s_img_drcitra, listOf("Hukum", "Disabilitas"), R.string.deskripDrCitra),
            ProfileKonselorHome("DR Lusy Liany, S.H., M.H.", R.drawable.s_img_drlusy, listOf("Finansial"), R.string.deskripDrLusy),
            ProfileKonselorHome("Ely Nurhayati, S.Pd, M.Si", R.drawable.s_img_drely, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrEly),
            ProfileKonselorHome("Qurrata A’yyun, M.Psi., Psikolog", R.drawable.s_qurrataayyun, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrAyyun),
            ProfileKonselorHome("dr. Yusnita., MKes.,Sp.KKLP.,Subsp.COPC", R.drawable.s_img_dryusnita, listOf("Gizi"), R.string.deskripDrYusnita),

                //Terakhir Tidak Boleh Di Ubah Karena Sesuai Logic Jika Ingin Di Ubah Maka Di Profile HomeKonselorAdapter Harus Di Ubah Juga
            ProfileKonselorHome("Peer Konselor", R.drawable.s_peer_konselor, listOf("Curhat Remaja"), R.string.deskripPeerKonselor),
        )

        // Setelah itu akan di implement ke adapter ProfileKonselorHomeAdapter function
        //             -> Adapter ProfileKonselorHomeAdapter function
        val adapter = ProfileKonselorHomeAdapter(profileList)
        recyclerView.adapter = adapter


        // Recycle View 2 Adalah Untuk Menampilkan Keunggulan Memakai Aplikasi Satya Gatra.
        val recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerViewHome2)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // Ini Beberapa String yang akan di tampilkan di menu home aplikasi.
        val desc = listOf(
            KeunggulanHome("Bebas Antri & Jadwal tercepat dalam <24 jam"),
            KeunggulanHome("Konselor yang dijamin berpengalaman & ahli di bidangnya"),
            KeunggulanHome("Layanan konseling gratis & yang pasti Privasi Terjaga dan aman"),
            KeunggulanHome("Akses ke berbagai Topik konseling & Sesi yang fleksible"),
            KeunggulanHome("Ayo segera jadwalkan sesi konselingmu sekarang!"),
        )
        // Setelah itu akan di implement ke adapter KeunggulanHomeAdapter function
        //             -> Adapter KeunggulanHomeAdapter function
        val adapter2 = KeunggulanHomeAdapter(desc)
        recyclerView2.adapter = adapter2


        // Recycle View 3 Adalah Untuk Menampilkan Review-review User.
        val recyclerView3 = view.findViewById<RecyclerView>(R.id.recyclerViewHome3)
        recyclerView3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Buat instance adapter tanpa data awal
        val adapter3 = ReviewItemAdapterLast(emptyList())
        recyclerView3.adapter = adapter3

        // Observe data dari SharedViewModel
        sharedViewModel.dataReviewAllUser.observe(viewLifecycleOwner) { reviews ->
            // Update adapter setiap ada perubahan pada data review
            adapter3.updateData(reviews.sortedByDescending { it.rateStar })
        }

        return view
    }
}








//// Recycle View 3 Adalah Untuk Menampilkan Review-review User.
//val recyclerView3 = view.findViewById<RecyclerView>(R.id.recyclerViewHome3)
//recyclerView3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//// Ambil Data Yang Di Oper Dari Main Activity User memakai Shared View Model, Setelah itu akan di implement ke -> Adapter ReviewAdapterLast function
//val dat = sharedViewModel.dataReviewAllUser.value ?: emptyList()
////             -> Adapter ReviewAdapterLast function
//val adapter3 = ReviewItemAdapterLast(dat.sortedByDescending { r -> r.rateStar  })
//recyclerView3.adapter = adapter3







//    private val datas = listOf(
//        ReviewItem("Sukinem", 2, "",listOf("Talkaktive")),
//        ReviewItem("Panco", 3, "",listOf("Good-Listener", "Profesional", "Non-Judgemental")),
//        ReviewItem("Tajir Banget", 0, "","Good-Listener, Non-Judgemental".split(", ")),
//        ReviewItem("Tajir 2", 0, "",listOf("Profesional", "Good-Listener")),
//    )




//        val textList3 = listOf("Keluarga", "Pos Sapa", "Parenting", "Kesehatan Mental dan Psikologi", "Kesehatan Kesehatan Kesehatan")
//        val textList4 = listOf("Keluarga", "Parenting", "Kesehatan Mental dan psikologi")
//        val textList5 = listOf("Kesehatan Mental dan Psikologi")
//        val textList6 = listOf("Agama")
//        val textList7 = listOf("Gizi")
//        val textList8 = listOf("Kesehatan Mental dan Psikologi")
//        val textList9 = listOf("Motivasi Belajar")
//        val textList10 = listOf("Pos Sapa", "Hukum")
//        val textList11 = listOf("Pos Sapa", "Kesehatan Mental dan Psikologi")
//        val textList12 = listOf("Motivasi Belajar")
//        val textList13 = listOf("Motivasi Belajar", "Disabilitas", "Kesehatan Mental dan Psikologi")
//        val textList14 = listOf("Kesehatan Mental dan Psikologi")
//        val textList15 = listOf("Hukum", "Disabilitas")
//        val textList16 = listOf("Finansial")
//        val textList17 = listOf("Kesehatan Mental dan Psikologi")
