package com.example.projekconsultant.myfragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class Penjadwalan : Fragment() {
    private var myName:String = ""
    private var myID:String? = ""

    companion object {
        @JvmStatic
        fun newInstance(name:String) = Penjadwalan().apply {
                arguments = Bundle().apply {
                    putString("myName", name)
                }
            }

        fun inputUIDS(id : String) = Penjadwalan().apply {
            arguments = Bundle().apply {
                putString("myID", id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myName = it.getString("myName").toString()
            myID   = it.getString("myID").toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_penjadwalan, container, false)

        Handler().postDelayed({
            view.findViewById<ProgressBar>(R.id.loading_progbar).visibility = View.GONE
            view.findViewById<ConstraintLayout>(R.id.midMain).visibility = View.VISIBLE
            view.findViewById<ImageView>(R.id.imageView6).visibility = View.VISIBLE

            val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            myID = sharedViewModel.ruid.value

            view.findViewById<ImageView>(R.id.backtohome).setOnClickListener {
                // Ganti fragment dengan Home val homeFragment = Home()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home()).commit()
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.home
            }

            view.findViewById<LinearLayout>(R.id.online).setOnClickListener {
                if (isAdded) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, jadwalOnline())
                        .addToBackStack(null)
                        .commit()
                }
            }

            view.findViewById<LinearLayout>(R.id.offline).setOnClickListener {
                if (isAdded) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, jadwalOffline())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }, 700)

        return view
    }
}











//            view.findViewById<LinearLayout>(R.id.online).setOnClickListener {
//                //requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, jadwalOnline.inputUIDS(myID ?: "")).commit()
//                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, jadwalOnline()).addToBackStack(null).commit()
//            }


//            view.findViewById<LinearLayout>(R.id.offline).setOnClickListener {
//                //requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, jadwalOffline.inputUIDS(myID ?: "")).commit()
//                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, jadwalOffline()).addToBackStack(null).commit()
//            }