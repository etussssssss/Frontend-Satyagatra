package com.example.projekconsultant.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.R
import com.google.android.material.bottomnavigation.BottomNavigationView



class RiwayatPovAdminFragment : Fragment() {

    private var param1: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("ARG_PARAM1")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) = RiwayatPovAdminFragment().apply {
            arguments = Bundle().apply {
                putString("ARG_PARAM1", param1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat_pov_admin, container, false)

        view.findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, HomeAdmin()).commit()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.home
        }

        view.findViewById<View>(R.id.online).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, RiwayatPovAdminOnline()).addToBackStack(null).commit()
        }

        view.findViewById<View>(R.id.offline).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, RiwayatPovAdminOffline()).addToBackStack(null).commit()
        }

        return view
    }
}







//    private var dataPendaftaranOnline:ArrayList<DataPendaftaran> = arrayListOf()
//    private var dataPendaftaranOffline:ArrayList<DataPendaftaran> = arrayListOf()