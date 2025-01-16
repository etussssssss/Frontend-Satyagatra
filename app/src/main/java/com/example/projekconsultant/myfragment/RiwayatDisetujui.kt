package com.example.projekconsultant.myfragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.projekconsultant.R
import com.example.projekconsultant.sideactivity.ChatAdmin


class RiwayatDisetujui : Fragment() {
    // TODO: Rename and change types of parameters
    private var thNamee: String? = null
    private var thTopikk: String? = null
    private var thLayanan: String? = null
    private var thTanggal: String? = null
    private var thOffOrOnn: String? = null
    private var thDescTopikk: String? = null

    private var thNoPendaftaran: String? = null
    private var thNoRuid: String? = null

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(name: String, topiks: String, offOrOns:String, descTopiks:String, tanggal:String, layanan:String,noPendaftaran:String?, rUid:String?) =
            RiwayatDisetujui().apply {
                arguments = Bundle().apply {
                    putString("NAME", name)
                    putString("TOPIk", topiks)
                    putString("OFFORON", offOrOns)
                    putString("DESCPROBlEM", descTopiks)
                    putString("NOPENDAFTARAN", noPendaftaran)
                    putString("TANGGAL",tanggal)
                    putString("LAYANAN",layanan)
                    putString("RUID",rUid)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thNamee = it.getString("NAME")
            thTopikk = it.getString("TOPIk")
            thOffOrOnn = it.getString("OFFORON")
            thDescTopikk = it.getString("DESCPROBlEM")
            thNoPendaftaran = it.getString("NOPENDAFTARAN")
            thTanggal = it.getString("TANGGAL")
            thLayanan = it.getString("LAYANAN")
            thNoRuid = it.getString("RUID")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_riwayat_disetujui, container, false)

        val ppImg = view.findViewById<ImageView>(R.id.imageViewProfileDes)
        val descriptionTextView = view.findViewById<TextView>(R.id.deskripsTopik)
        val toggleTextView = view.findViewById<TextView>(R.id.toggleTextView)
        val chat = view.findViewById<Button>(R.id.chat)

        val nama = view.findViewById<TextView>(R.id.nama)
        val topik = view.findViewById<TextView>(R.id.topik)
        val tanggal = view.findViewById<TextView>(R.id.tanggal)
        val offOrOn = view.findViewById<TextView>(R.id.offoron)

        nama.text = "${thNamee}"
        topik.text = "${thTopikk}"
        tanggal.text = "${thTanggal}"
        offOrOn.text = "${thOffOrOnn}"
        descriptionTextView.text = "${thDescTopikk}"

        chat.setOnClickListener {
            val intent = Intent(requireActivity(), ChatAdmin::class.java)
            intent.putExtra("RUID", thNoRuid)
            startActivity(intent)
        }

        toggleTextView.setOnClickListener {
            if (descriptionTextView.maxLines == 2) {
                // Tampilkan seluruh teks
                descriptionTextView.maxLines = Int.MAX_VALUE
                toggleTextView.text = "Lihat lebih sedikit"
            } else {
                // Kembali ke teks pendek
                descriptionTextView.maxLines = 2
                toggleTextView.text = "Lihat lebih banyak"
            }
        }

        ppImg.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext()) // Gunakan requireContext()
            val viewD = layoutInflater.inflate(R.layout.item_detail_riwayat, null) // Inflasi layout dialog
            builder.setView(viewD) // Pasang layout ke dialog
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(requireContext().getDrawable(R.drawable.borderadius_dialog_white)) // Set background
            dialog.setCancelable(true) // Dialog tidak bisa ditutup dengan back atau klik luar
            dialog.show() // Tampilkan dialog

            viewD.findViewById<TextView>(R.id.nama).text = "Nama: ${thNamee}"
            viewD.findViewById<TextView>(R.id.topik).text = "Topik Konseling: ${thTopikk}"
            viewD.findViewById<TextView>(R.id.layanan).text = "Layanan: ${thLayanan}"
            viewD.findViewById<TextView>(R.id.tanggal).text = "Tanggal: ${thTanggal}"
            viewD.findViewById<TextView>(R.id.isiCurhat).text = "Isi Curhatan: ${thDescTopikk}"
            viewD.findViewById<TextView>(R.id.nomorPendaftaran).text = "Nomor Pendaftaran: ${thNoPendaftaran}"
        }

        return view
    }
}