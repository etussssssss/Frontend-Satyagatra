package com.example.projekconsultant.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.Login
import com.example.projekconsultant.R
import com.example.projekconsultant.sideactivity.DetailProfile
import com.example.projekconsultant.sideactivity.KebijakanPrivasi
import com.google.android.flexbox.FlexboxLayout
import com.google.firebase.auth.FirebaseAuth


class ProfilePovAdminFragment : Fragment() {
    private var myemail: String? = null
    private var myname: String? = null
    private var mybirth: String? = null
    private var mydomisili: String? = null
    private var mygender: String? = null
    private var mynotelp: String? = null

    private var mytags: String? = null

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstanceThree(urname: String, urdomisili: String, urtags: String) = ProfilePovAdminFragment().apply {
            arguments = Bundle().apply {
                putString("paramNama", urname)
                putString("paramDomisili", urdomisili)
                putString("paramTags", urtags)
            }
        }

        fun newInstance(uremail:String, urname: String, urbirth:String, urdomisili: String, urgender:String, urnotelp: String, urtags: String) = ProfilePovAdminFragment().apply {
            arguments = Bundle().apply {
                putString("paramEmail", uremail)
                putString("paramNama", urname)
                putString("paramBirth", urbirth)
                putString("paramDomisili", urdomisili)
                putString("paramGender", urgender)
                putString("paramNoTelp", urnotelp)

                putString("paramTags", urtags)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myemail = it.getString("paramEmail")
            myname = it.getString("paramNama")
            mybirth = it.getString("paramBirth")
            mydomisili = it.getString("paramDomisili")
            mygender = it.getString("paramGender")
            mynotelp = it.getString("paramNoTelp")

            mytags = it.getString("paramTags")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_pov_admin, container, false)
        val nama = view.findViewById<TextView>(R.id.myname)
        val domisili = view.findViewById<TextView>(R.id.mydomisili)
        val foto = view.findViewById<ImageView>(R.id.myfoto)
        val pengaturan =  view.findViewById<LinearLayout>(R.id.pengaturan)

        nama.text = myname
        domisili.text = mydomisili
        foto.setImageResource(R.drawable.img_ppdefault)

        //Detail Next Update
//        view.findViewById<ImageView>(R.id.myfoto).setOnClickListener {
//            val intent = Intent(requireContext(), DetailProfile::class.java)
//            startActivity(intent)
//        }

        pengaturan.setOnClickListener{
            val intent = Intent(requireActivity(), ProfilePengaturanPovAdmin::class.java)
            // Data Change Untuk Memberi limit waktu selama 1 minggu hanya bisa ganti data si user.
            // untuk next update mungkin
            startActivity(intent)
        }

        val sharedDatasAdmin = ViewModelProvider(requireActivity())[SharedViewModelAdmin::class.java]
        // Riwayat Konsultasi
        view.findViewById<LinearLayout>(R.id.riwayatKonsultasi).setOnClickListener {
            val intent = Intent(requireContext(), ProfileRiwayatKonsultasiAct::class.java)
            intent.putParcelableArrayListExtra("RIWAYAT", sharedDatasAdmin.dataRiwayatSelesaiAdmin.value)
            startActivity(intent)
        }

        // Statistik Riwayat
        view.findViewById<LinearLayout>(R.id.statistikKonsultasi).setOnClickListener {
            val intent = Intent(requireContext(), ProfileStatistikKonsultasi::class.java)
            intent.putParcelableArrayListExtra("RIWAYAT", sharedDatasAdmin.dataRiwayatSelesaiAdmin.value)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.kebijakanprivasi).setOnClickListener {
            val intent = Intent(requireContext(), KebijakanPrivasi::class.java)
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
                    background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
                    backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijo3)
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
        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.borderadius_dialog_white
            )
        )
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