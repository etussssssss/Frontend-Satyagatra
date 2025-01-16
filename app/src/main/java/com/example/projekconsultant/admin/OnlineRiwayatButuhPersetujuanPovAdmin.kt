package com.example.projekconsultant.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter2.AdapterAdminButuhPersetujuanOnline


class OnlineRiwayatButuhPersetujuanPovAdmin : Fragment() {
    private var thNamee: String? = null
    private var thTopikk: String? = null
    private var thOffOrOnn: String? = null
    private var thDescTopikk: String? = null
    private var thData:List<DataPendaftaran> = listOf()

    companion object {
        @JvmStatic
        fun newInstance(name: String, topiks: String, offOrOns:String, descTopiks:String) =
            OnlineRiwayatButuhPersetujuanPovAdmin().apply {
                arguments = Bundle().apply {
                    putString("NAME", name)
                    putString("TOPIk", topiks)
                    putString("OFFORON", offOrOns)
                    putString("DESCPROBlEM", descTopiks)
                }
            }

        fun dataButuhPersetujuan(datas: ArrayList<DataPendaftaran>) =
            OnlineRiwayatButuhPersetujuanPovAdmin().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("data", datas)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thData = it.getParcelableArrayList("data")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_online_riwayat_butuh_persetujuan_pov_admin, container, false)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val rec = view.findViewById<RecyclerView>(R.id.recyclerView)

        rec.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterAdminButuhPersetujuanOnline(thData)
        rec.adapter = adapter

        searchView.queryHint = "Search name..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText?.lowercase() ?: ""

                // Filter data berdasarkan teks pencarian
                val filteredData = if (searchText.isNotEmpty()) {
                    thData.filter { it.nama.lowercase().contains(searchText) }
                } else {
                    thData
                }

                // Perbarui data adapter dengan hasil filter
                adapter.updateData(filteredData)
                return true
            }
        })


        return view
    }
}








//            thNamee = it.getString("NAME")
//            thTopikk = it.getString("TOPIk")
//            thOffOrOnn = it.getString("OFFORON")
//            thDescTopikk = it.getString("DESCPROBlEM")



//val online = "Online"
//val dat = arrayListOf(
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