package com.example.projekconsultant.admin

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.net.ParseException
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter2.AdapterProfileDataRiwayatKonsultasiAdmin
import java.util.Date
import java.util.Locale


class ProfileRiwayatKonsultasiF : Fragment() {
    private var dataRiwayat: ArrayList<DataRiwayatSelesaiAdmin> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataRiwayat = it.getParcelableArrayList("RIWAYAT")!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = ProfileRiwayatKonsultasiF().apply {
                arguments = Bundle().apply {

                }
            }

        fun dataRiwayat(param1: ArrayList<DataRiwayatSelesaiAdmin>?) = ProfileRiwayatKonsultasiF().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("RIWAYAT", param1)
            }
        }
    }

    private lateinit var spinnerBulan:Spinner
    private lateinit var spinnerTahun:Spinner
    private lateinit var recy:RecyclerView
    private lateinit var adapterRecy: AdapterProfileDataRiwayatKonsultasiAdmin

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_riwayat_konsultasi, container, false)

        // Ambil referensi Spinner
        spinnerBulan = view.findViewById(R.id.spinnerBulan)
        spinnerTahun = view.findViewById(R.id.spinnerTahun)
        recy = view.findViewById(R.id.recyclerView)


        val bulanArray = arrayOf("Pilih", "Januari", "Februari", "Maret", "April", "Mei","Juni", "Juli", "Agustus","September","Oktober","November","Desember")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bulanArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown style
        spinnerBulan.adapter = adapter

        // Data Riwayat
        val tahunArray = listOf("Pilih") + getUniqueYears(dataRiwayat)
        val tahunAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tahunArray)
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTahun.adapter = tahunAdapter

        // Data Riwayat
        val sortedDataArray = dataRiwayat.sortedByDescending { parseDate(it.tanggalSelesaiUser) }

        // Milih Bulan
        spinnerBulan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData2(sortedDataArray)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Milih Tahun
        spinnerTahun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData2(sortedDataArray)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        adapterRecy = AdapterProfileDataRiwayatKonsultasiAdmin(sortedDataArray)
        recy.layoutManager = LinearLayoutManager(requireContext())
        recy.adapter = adapterRecy

        return view
    }

    // Get Setiap Tahun
    private fun getUniqueYears(dataArray: List<DataRiwayatSelesaiAdmin>): List<String> {
        return dataArray.mapNotNull { data ->
            val tanggalParts = data.tanggalSelesaiUser.split(" ")
            if (tanggalParts.size == 3) tanggalParts[2] else null
        }.distinct().sorted()
    }

    // Conver String Ke Date Buat Di Filterisasi
    private fun parseDate(tanggal: String): Date? {
        return try {
            val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            format.parse(tanggal)
        } catch (e: ParseException) {
            null
        }
    }

    // Memfilter pilihan sesuai bulan dan tahun
    private fun filterData2(dataArray: List<DataRiwayatSelesaiAdmin>) {
        val bulanDipilih = spinnerBulan.selectedItem.toString()
        val tahunDipilih = spinnerTahun.selectedItem.toString()

        if (bulanDipilih == "Pilih" || tahunDipilih == "Pilih") {
            adapterRecy.updateData(dataArray.sortedByDescending { parseDate(it.tanggalSelesaiUser) })
        } else {
            val filteredData = dataArray.filter { data ->
                val tanggalParts = data.tanggalSelesaiUser.split(" ")
                if (tanggalParts.size == 3) {
                    val bulan = tanggalParts[1]
                    val tahun = tanggalParts[2]
                    bulan.equals(bulanDipilih, ignoreCase = true) && tahun == tahunDipilih
                } else {
                    false
                }
            }.sortedByDescending { parseDate(it.tanggalSelesaiUser) }

            adapterRecy.updateData(filteredData)
        }
    }

    // Referensi
    private fun filterData(dataArray: List<DataRiwayatSelesaiAdmin>) {
        val bulanDipilih = spinnerBulan.selectedItem.toString()
        val tahunDipilih = spinnerTahun.selectedItem.toString()

        if (bulanDipilih == "Pilih" || tahunDipilih == "Pilih") {
            adapterRecy.updateData(dataArray)
        } else {
            val filteredData = dataArray.filter { data ->
                val tanggalParts = data.tanggalSelesaiUser.split(" ")
                if (tanggalParts.size == 3) {
                    val bulan = tanggalParts[1]
                    val tahun = tanggalParts[2]
                    bulan.equals(bulanDipilih, ignoreCase = true) && tahun == tahunDipilih
                } else {
                    false
                }
            }
            adapterRecy.updateData(filteredData)
        }
    }
}





//private val dataArray = arrayListOf(
//    DataRiwayatSelesaiAdmin(
//        "C-113","Wajid","Keluarga",
//        "26 Januari 2024","Online",
//        "Via Chat", "Curhat saya Lorem 123"),
//    DataRiwayatSelesaiAdmin(
//        "S-253","Nanas", "Curhat Remaja",
//        "21 Januari 2024","Online",
//        "Zoom", "Curhat saya Lorem 125"),
//    DataRiwayatSelesaiAdmin(
//        "S-213","Jaki", "Agama",
//        "26 Januari 2024","Online",
//        "Zoom", "Curhat saya Lorem 346"),
//    DataRiwayatSelesaiAdmin(
//        "S-243","Topik","Agama",
//        "26 Februari 2024","Online",
//        "Zoome", "Curhat saya Lorem 361"),
//    DataRiwayatSelesaiAdmin(
//        "B-263","Adon","Agama",
//        "26 Maret 2024","Online",
//        "Zoom", "Curhat saya Lorem 532"),
//    DataRiwayatSelesaiAdmin(
//        "C-253","Dani","Agama",
//        "26 Maret 2024","Online",
//        "Via Chat", "Curhat saya Lorem 20"),
//
//    DataRiwayatSelesaiAdmin(
//        "C-253","Jani","Finansial",
//        "26 Februari 2023","Online",
//        "Via Chat", "Curhat saya Lorem 20"),
//
//    DataRiwayatSelesaiAdmin(
//        "C-253","Pani","Kesehatan Mental dan Psikologi",
//        "26 Maret 2023","Online",
//        "Via Chat", "Curhat saya Lorem 20"),
//    DataRiwayatSelesaiAdmin(
//        "C-253","Jaja","Kesehatan Mental dan Psikologi",
//        "26 Maret 2023","Online",
//        "Via Chat", "Curhat saya Lorem 20"),
//)