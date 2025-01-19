package com.example.projekconsultant.admin

import KeunggulanHome
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.example.projekconsultant.myfragment.Penjadwalan
import com.example.projekconsultant.myfragment.jadwalOffline
import com.example.projekconsultant.myfragment.jadwalOnline
import com.example.projekconsultant.sideactivity.KebijakanPrivasi
import com.google.android.material.bottomnavigation.BottomNavigationView



class HomeAdmin : Fragment() {
    // TODO: Rename and change types of parameters
    private var namae: String? = ""

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(namaUser: String?) =
            HomeAdmin().apply {
                arguments = Bundle().apply {
                    putString("paramNama", namaUser)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            namae = it.getString("paramNama")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_admin, container, false)
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        view.findViewById<TextView>(R.id.sayhitouser).setText("Hi, ${namae?.split(" ")?.get(0)}")

        view.findViewById<Button>(R.id.forumchat).setOnClickListener {
            //Ke Forum Chat View
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, ForumChatAdminFragment()).commit()
            //Bottom Nav
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.chat
        }

        view.findViewById<View>(R.id.offline).setOnClickListener {
            //Bottom Nav
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.history
            //Ke Penjadwalan View
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, RiwayatPovAdminOffline()).commit()
        }

        view.findViewById<View>(R.id.online).setOnClickListener {
            //Bottom Nav
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.history
            //Ke Penjadwalan View
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, RiwayatPovAdminOnline()).commit()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHome1)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val profileList = listOf(
            ProfileKonselorHome("dr. Maya Trisiswati, MKM.", R.drawable.s_img_drmaya, listOf("HIV", "Pos Sapa/Kekerasan", "Kesehatan Reproduksi"), R.string.deskripDrMaya),
            ProfileKonselorHome("dr. Liko Maryudhiyanto, Sp.KJ., C.Ht", R.drawable.s_img_drliko, listOf("Motivasi Belajar", "Kesehatan Mental dan Psikologi"), R.string.deskripDrLiko),
            ProfileKonselorHome("dr. Melok Roro Kinanthi, M.Psi., Psikolog", R.drawable.s_img_drmelok, listOf("Keluarga", "Pos Sapa/Kekerasan", "Parenting", "Kesehatan Mental dan Psikologi"), R.string.deskripDrMelok),
            ProfileKonselorHome("dr. Octaviani Indrasari Ranakusuma, M.Si., Psi.", R.drawable.s_img_droktaviani, listOf("Keluarga", "Parenting", "Kesehatan Mental dan psikologi"), R.string.deskripDrOctaviani),
            ProfileKonselorHome("dr. Miwa Pathani, M.Si., Psikolog", R.drawable.s_img_drmiwa, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrMiwa),
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
        val adapter = ProfileKonselorHomeAdapter(profileList)
        recyclerView.adapter = adapter


        val recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerViewHome2)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val desc = listOf(
            KeunggulanHome("Bebas Antri & Jadwal tercepat dalam <24 jam"),
            KeunggulanHome("Konselor yang dijamin berpengalaman & ahli di bidangnya"),
            KeunggulanHome("Layanan konseling gratis & yang pasti Privasi Terjaga dan aman"),
            KeunggulanHome("Akses ke berbagai Topik konseling & Sesi yang fleksible"),
            KeunggulanHome("Ayo segera jadwalkan sesi konselingmu sekarang!"),
        )
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











//private val datas = listOf(
//        ReviewItem("Sukinem", 2, listOf("Talkaktive")),
//        ReviewItem("Panco", 3, listOf("Good-Listener", "Profesional", "Non-Judgemental")),
//        ReviewItem("Tajir Banget", 0, "Good-Listener, Non-Judgemental".split(", ")),
//        ReviewItem("Tajir 2", 0, listOf("Profesional", "Good-Listener")),
////        ReviewItem("Tajir 3", 4, listOf("Profesional", "Good-Listener")),
////        ReviewItem("Tajir 4", 4, listOf("Profesional", "Good-Listener")),
//    )



//        view.findViewById<View>(R.id.editreview).setOnClickListener {
//            val intent = Intent(requireContext(), ReviewAllAdmin::class.java)
//            intent.putParcelableArrayListExtra("data", ArrayList(dataReview))
//            requireContext().startActivity(intent)
//        }