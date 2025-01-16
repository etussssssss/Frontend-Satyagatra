package com.example.projekconsultant.myfragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projekconsultant.R
import com.example.projekconsultant.Registrasi
import com.example.projekconsultant.mymainactivity.Pengaturan

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val nama = "param1"
private const val gmail = "param2"
private const val foto = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var myname: String? = null
    private var mygmail: String? = null
    private var myfoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myname = it.getString(nama)
            mygmail = it.getString(gmail)
            myfoto = it.getString(foto)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_profile, container, false)

        val nama = view.findViewById<TextView>(R.id.myname)
        var  gmail = view.findViewById<TextView>(R.id.mygmail)
        nama.text = myname
        gmail.text = mygmail

        val foto = view.findViewById<ImageView>(R.id.myfoto)

//        val load = myfoto

            Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/consultant-mental.appspot.com/o/image-app%2Fconselor1hd.jpg?alt=media&token=552e9d1a-a2d3-40fa-afe9-9d465beb0a16")
                .into(foto)


        foto.setOnClickListener {
            Log.d("FOTO", "INI MASUK")
        }

        val pengaturan =  view.findViewById<LinearLayout>(R.id.pengaturan)
        pengaturan.setOnClickListener{
            Log.d("Pengaturan", "INI MASUK")
            val intent = Intent(requireActivity(), Pengaturan::class.java)
            startActivity(intent)
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(urname: String, uremail: String, urfoto:String) = Profile().apply {
                arguments = Bundle().apply {
                    putString(nama, urname)
                    putString(gmail, uremail)
                    putString(foto, urfoto)
                }
            }
    }
}