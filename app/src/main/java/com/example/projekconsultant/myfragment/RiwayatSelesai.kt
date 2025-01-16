package com.example.projekconsultant.myfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.adapter.SelesaiDataAdapter
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import com.example.projekconsultant.data.UserSelesai
import com.example.projekconsultant.methodsNService.SharedViewModel


class RiwayatSelesai : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    companion object {
        @JvmStatic
        // Fungsi untuk membuat instance `RiwayatSelesai` dengan dua array list sebagai data (riwayat dan detail selesai).
        fun getData(riwayat: ArrayList<SelesaiData>, thDataDetailSelesai: ArrayList<SelesaiDetailReviewUser>) = RiwayatSelesai().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("SELESAI", riwayat)
                putParcelableArrayList("DETAILSELESAI", thDataDetailSelesai)
            }
        }

        // Fungsi untuk membuat instance `RiwayatSelesai` hanya dengan data riwayat.
        // Namun tidak di pakai, hanya untuk referensi jika berguna untuk update.
        fun getDataSelesai(riwayat: ArrayList<SelesaiData>) = RiwayatSelesai().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("SELESAI", riwayat)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat_selesai, container, false)
        // Mendapatkan ViewModel yang dibagikan antara activity/fragment
        // Variable shared view model untuk mengambil data yang sudah di manipulasi untuk live data.
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Inisialisasi Recycle View Selesai 1
        val recy = view.findViewById<RecyclerView>(R.id.recyclerViewSelesai1)
        recy.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        //
        val adapter = SelesaiDataAdapter(sharedViewModel.dataUserSelesai.value ?: emptyList())

        sharedViewModel.dataUserSelesai.observe(viewLifecycleOwner, Observer { data ->
            // Update data pada adapter
            adapter.updateData(data) // Misalnya, updateData() adalah fungsi di adapter untuk memperbarui data
            adapter.notifyDataSetChanged()
        })

        recy.adapter = adapter

        return view
    }
}










//private var thDataSelesai:ArrayList<SelesaiData> = arrayListOf()
//private var thDataDetail:ArrayList<SelesaiDetailReviewUser> = arrayListOf()
//private var thDatas:ArrayList<UserSelesai> = arrayListOf()