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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter2.AdapterProfileDataRiwayatKonselorAdmin
import com.example.projekconsultant.adapter2.AdapterProfileDataRiwayatKonsultasiAdmin
import java.util.Date
import java.util.Locale


class ProfileRiwayatKonselorF : Fragment() {
    private var dataRiwayatKonselor: ArrayList<DataRiwayatKonselor> = arrayListOf()
    private lateinit var spinnerBulan: Spinner
    private lateinit var spinnerTahun: Spinner
    private lateinit var recy: RecyclerView
    private lateinit var adapterRecy: AdapterProfileDataRiwayatKonselorAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataRiwayatKonselor = it.getParcelableArrayList("RIWAYAT")!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileRiwayatKonselorF().apply {
                arguments = Bundle().apply {

                }
            }

        fun dataRiwayat(param1: ArrayList<DataRiwayatKonselor>?) =
            ProfileRiwayatKonselorF().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("RIWAYAT", param1)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_riwayat_konselor, container, false)
        // Ambil referensi Spinner
        spinnerBulan = view.findViewById(R.id.spinnerBulan)
        spinnerTahun = view.findViewById(R.id.spinnerTahun)
        recy = view.findViewById(R.id.recyclerView)

        val bulanArray = arrayOf("Pilih", "Januari", "Februari", "Maret", "April", "Mei","Juni", "Juli", "Agustus","September","Oktober","November","Desember")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bulanArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown style
        spinnerBulan.adapter = adapter

        // Data Riwayat
        val tahunArray = listOf("Pilih") + getUniqueYears(dataRiwayatKonselor)
        val tahunAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tahunArray)
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTahun.adapter = tahunAdapter

        // Data Riwayat
        val sortedDataArray = dataRiwayatKonselor.sortedByDescending { parseDate(it.tanggalSelesaiUser) }

        spinnerBulan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData2(sortedDataArray)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTahun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterData2(sortedDataArray)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        adapterRecy = AdapterProfileDataRiwayatKonselorAdmin(sortedDataArray)
        recy.layoutManager = LinearLayoutManager(requireContext())
        recy.adapter = adapterRecy

        return view
    }

    private fun getUniqueYears(dataArray: List<DataRiwayatKonselor>): List<String> {
        return dataArray.mapNotNull { data ->
            val tanggalParts = data.tanggalSelesaiUser.split(" ")
            if (tanggalParts.size == 3) tanggalParts[2] else null
        }.distinct().sorted()
    }

    private fun parseDate(tanggal: String): Date? {
        return try {
            val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            format.parse(tanggal)
        } catch (e: ParseException) {
            null
        }
    }

    private fun filterData2(dataArray: List<DataRiwayatKonselor>) {
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
}