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
}