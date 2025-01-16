package com.example.projekconsultant.admin

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.AdapterAdminLayananRiwayatItem
import com.example.projekconsultant.adapter.AdapterAdminRiwayatItem
import com.example.projekconsultant.adapter2.AdapterProfileRiwayatKonsultasiKonselor
import com.example.projekconsultant.myfragment.RiwayatTidakAdaJadwalDisetujui

class ProfileRiwayatKonsultasiAct : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_riwayat_konsultasi)

        val dataRiwayatKonselor:ArrayList<DataRiwayatKonselor> = ArrayList()
         val receivedUserList = intent.getParcelableArrayListExtra<DataRiwayatSelesaiAdmin>("RIWAYAT")
//        val receivedUserList = dataArray

        receivedUserList?.forEach {
            dataRiwayatKonselor.add(
                DataRiwayatKonselor(
                    nomorPendaftaranUser = "${it.nomorPendaftaranUser}",
                    namaKonselor = "${it.namaKonselor}",
                    tanggalSelesaiUser = "${it.tanggalSelesaiUser}",
                )
            )
        }

        // Data menu item
        val menuItems = listOf(
            AdapterAdminLayananRiwayatItem("RIWAYAT KONSULTASI", if (!receivedUserList.isNullOrEmpty()) ProfileRiwayatKonsultasiF.dataRiwayat(receivedUserList) else RiwayatTidakAdaJadwalDisetujui()),
            AdapterAdminLayananRiwayatItem("RIWAYAT KONSELOR", if (dataRiwayatKonselor.isNotEmpty()) ProfileRiwayatKonselorF.dataRiwayat(dataRiwayatKonselor) else RiwayatTidakAdaJadwalDisetujui()),
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@ProfileRiwayatKonsultasiAct, LinearLayoutManager.HORIZONTAL, false)
            adapter = AdapterProfileRiwayatKonsultasiKonselor(menuItems) { menuItem ->
                replaceFragment(menuItem.fragment)
            }
        }

        recyclerView.post {
            replaceFragment(menuItems.first().fragment)
            (recyclerView.adapter as? AdapterAdminRiwayatItem)?.setDefaultSelection()
        }

        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerRKK, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerRKK)

        if(currentFragment is ProfileRiwayatKonsultasiF){
            super.onBackPressed()
        }else {
            finish()
        }
    }

    private val dataArray = arrayListOf(
        DataRiwayatSelesaiAdmin(
            "C-115",
            "Wajid",
            "Dr. Maya Trisilawati, MKM.",
            "Pos Sapa",
            "26 Januari 2024",
            "Online",
            "Via Chat",
            "Curhat saya Lorem Ipsum",
            "Internal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-115",
            "Aril",
            "Dr. Maya Trisilawati, MKM.",
            "Pos Sapa",
            "21 Januari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem 123",
            "Internal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-143",
            "Wilay",
            "Dr. Maya Trisilawati, MKM.",
            "Parenting",
            "26 Januari 2024",
            "Online",
            "Via Chat",
            "Curhat Lorem 123",
            "Internal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-413",
            "Isa",
            "Dr. Liko Maryudhiyanto, Sp.KJ., C.Ht",
            "Agama",
            "16 Januari 2024",
            "Online",
            "Via Chat",
            "Curhat saya 343",
            "Internal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-813",
            "Isa",
            "Dr. Lusy Liany, S.H., M.H.",
            "Agama",
            "6 Januari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya 124123",
            "Eksternal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Parenting",
            "2 Januari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Parenting",
            "2 Februari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Motivasi Belajar",
            "2 Maret 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Motivasi Belajar",
            "2 April 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal"
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Finansial",
            "2 Mei 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal"
        ),
    )

}