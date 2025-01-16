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
import com.example.projekconsultant.adapter2.AdapterAdminDiSetujuiOnline


class OnlineRiwayatDisetujuiPovAdmin : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var thData:List<DataPendaftaran> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("ARG_PARAM1")
            param2 = it.getString("ARG_PARAM2")

            thData = it.getParcelableArrayList("data")!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = OnlineRiwayatDisetujuiPovAdmin().apply {
                arguments = Bundle().apply {
                    putString("ARG_PARAM1", param1)
                    putString("ARG_PARAM2", param2)
                }
            }

        fun dataDiSetujui(datas: ArrayList<DataPendaftaran>) = OnlineRiwayatDisetujuiPovAdmin().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("data", datas)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_online_riwayat_disetujui_pov_admin, container, false)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val rec = view.findViewById<RecyclerView>(R.id.recyclerView)

        rec.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterAdminDiSetujuiOnline(thData)
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