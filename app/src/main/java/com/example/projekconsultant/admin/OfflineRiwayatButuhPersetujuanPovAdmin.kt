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
import com.example.projekconsultant.adapter2.AdapterAdminButuhPersetujuanOffline


class OfflineRiwayatButuhPersetujuanPovAdmin : Fragment() {
    private var thData:List<DataPendaftaran> = listOf()


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun dataButuhPersetujuan(datas: ArrayList<DataPendaftaran>) = OfflineRiwayatButuhPersetujuanPovAdmin().apply {
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
        val view = inflater.inflate(R.layout.fragment_offline_riwayat_butuh_persetujuan_pov_admin, container, false)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val rec = view.findViewById<RecyclerView>(R.id.recyclerView)

        rec.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterAdminButuhPersetujuanOffline(thData)
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