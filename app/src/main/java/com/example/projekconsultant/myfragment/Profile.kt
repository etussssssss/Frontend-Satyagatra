package com.example.projekconsultant.myfragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.content.ContextCompat.getDrawable
import com.example.projekconsultant.Login
import com.example.projekconsultant.R
import com.example.projekconsultant.sideactivity2.Pengaturan
import com.example.projekconsultant.sideactivity.KebijakanPrivasi
import com.google.android.flexbox.FlexboxLayout
import com.google.firebase.auth.FirebaseAuth



class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var myname: String? = null
    private var mydomisili: String? = null
    private var mytags: String? = null

    companion object {

        @JvmStatic
        fun newInstance(urname: String, urdomisili: String, urtags:String) = Profile().apply {
            arguments = Bundle().apply {
                putString("paramNama", urname)
                putString("paramDomisili", urdomisili)
                putString("paramTags", urtags)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myname = it.getString("paramNama")
            mydomisili = it.getString("paramDomisili")
            mytags = it.getString("paramTags")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_profile, container, false)

        val nama = view.findViewById<TextView>(R.id.myname)
        var  domisili = view.findViewById<TextView>(R.id.mydomisili)
        nama.text = myname
        domisili.text = mydomisili

        val foto = view.findViewById<ImageView>(R.id.myfoto)
        foto.setImageResource(R.drawable.img_ppdefault)

        val pengaturan =  view.findViewById<LinearLayout>(R.id.pengaturan)
        pengaturan.setOnClickListener{
            val intent = Intent(requireActivity(), Pengaturan::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.kebijakanprivasi).setOnClickListener {
            val intent = Intent(requireContext(), KebijakanPrivasi::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.instagram).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ppks_universitasyarsi?igsh=MWJzM3lpdWM1bWRzcA=="))
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.whatsapp).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send/?phone=62811803875&text&type=phone_number&app_absent=0&wame_ctl=1&fbclid=PAY2xjawHpcj1leHRuA2FlbQIxMAABpkg7FCarCgH0DNRyX_YH3gDLtB4lRkeh4MKMsaR49emAEkFIjpAidh5HIw_aem_NoWyvW6URsRE8K3jWr6qZA"))
            startActivity(intent)
        }

        val cont = view.findViewById<FlexboxLayout>(R.id.tag_layout)
        if(mytags != null && mytags!!.isNotEmpty()){
            for (text in mytags!!.split(", ")) {
                val textView = TextView(cont.context).apply {

                    val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(8, 8, 8, 8)
                    this.layoutParams = layoutParams
                    setText(text)
                    textSize = 7f
                    background = getDrawable(context, R.drawable.border_radius_2)
                    backgroundTintList = getColorStateList(context, R.color.ijo3)
                    setPadding(16, 8, 16, 8)
                    gravity = Gravity.CENTER
                }
                cont.addView(textView)
            }
        }else{
            cont.visibility = View.GONE
        }

        val builder = AlertDialog.Builder(requireContext()).setView(R.layout.item_dialog_logout)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(getDrawable(requireContext(), R.drawable.borderadius_dialog_white))
        dialog.setCancelable(true)

        view.findViewById<LinearLayout>(R.id.logout).setOnClickListener {
            dialog.show() // Tampilkan dialog ketika layout diklik

            dialog.findViewById<Button>(R.id.btn_no)?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.findViewById<Button>(R.id.btn_yes)?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
        }

        return view
    }




}

//        Glide.with(this)
//            .load("https://firebasestorage.googleapis.com/v0/b/consultant-mental.appspot.com/o/image-app%2Fconselor1hd.jpg?alt=media&token=552e9d1a-a2d3-40fa-afe9-9d465beb0a16")
//            .into(foto)

