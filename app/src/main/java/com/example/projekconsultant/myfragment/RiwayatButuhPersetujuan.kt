package com.example.projekconsultant.myfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.ViewModelProvider
import com.example.projekconsultant.R
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.example.projekconsultant.sideactivity.ChatAdmin
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class RiwayatButuhPersetujuan : Fragment() {
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
        @JvmStatic
        fun newInstance(name: String, topiks: String, offOrOns:String, descTopiks:String, tanggal:String, layanan:String, noPendaftaran:String?, rUid:String?) =
            RiwayatButuhPersetujuan().apply {
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

    private lateinit var db: FirebaseFirestore
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_riwayat_butuh_persetujuan, container, false)

        db = FirebaseFirestore.getInstance()

        val ppImg = view.findViewById<ImageView>(R.id.imageViewProfileDes)
        val descriptionTextView = view.findViewById<TextView>(R.id.deskripsTopik)
        val toggleTextView = view.findViewById<TextView>(R.id.toggleTextView)
        //val chat = view.findViewById<View>(R.id.chat)
        val chat = view.findViewById<Button>(R.id.chat)
        val btnBatal = view.findViewById<Button>(R.id.buttonBatal)

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

        progbar = view.findViewById(R.id.loading_progbar)

        btnBatal.setOnClickListener {
            // Dialog Alasan Batal (Layer 1)
            val builderRadioDialog = AlertDialog.Builder(requireContext()) // Gunakan requireContext()
            val viewRadioDialog = layoutInflater.inflate(R.layout.item_batalpenjadwalan_povuser, null) // Inflasi layout dialog
            builderRadioDialog.setView(viewRadioDialog) // Pasang layout ke dialog
            val dialogbuilderRadiaDialog = builderRadioDialog.create()

            // Mengatur ukuran wrap_content untuk dialog
            dialogbuilderRadiaDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Set background untuk dialog
            dialogbuilderRadiaDialog.window?.setBackgroundDrawable(requireContext().getDrawable(R.drawable.borderadius_dialog_white))
            dialogbuilderRadiaDialog.setCancelable(true) // Dialog dapat ditutup dengan back atau klik luar
            dialogbuilderRadiaDialog.show() // Tampilkan dialog

            val radioGroup = viewRadioDialog.findViewById<RadioGroup>(R.id.radioGroupAlasan)

            viewRadioDialog.findViewById<Button>(R.id.btn_konfirmasi).setOnClickListener {
                val selectedId = radioGroup.checkedRadioButtonId
                val radioButton = viewRadioDialog.findViewById<RadioButton>(selectedId)

                if (selectedId != -1) {
                    dialogbuilderRadiaDialog.dismiss()
                    progbar.visibility = View.VISIBLE
                    val alasan = radioButton.text.toString()


                    // Dialog Yakin Batal (Layer 2)
                    val builderYakin = AlertDialog.Builder(requireContext()) // Gunakan requireContext()
                    val viewYakinDialog = layoutInflater.inflate(R.layout.item_yakin_batal_jadwal_user, null) // Inflasi layout dialog
                    builderYakin.setView(viewYakinDialog) // Pasang layout ke dialog
                    val dialogbuilderYakin = builderYakin.create()

                    // Mengatur ukuran wrap_content untuk dialog
                    dialogbuilderYakin.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    // Set background untuk dialog
                    dialogbuilderYakin.window?.setBackgroundDrawable(requireContext().getDrawable(R.drawable.borderadius_dialog_white))
                    dialogbuilderYakin.setCancelable(false) // Dialog dapat ditutup dengan back atau klik luar
                    dialogbuilderYakin.show() // Tampilkan dialog

                    viewYakinDialog.findViewById<Button>(R.id.btn_no).setOnClickListener {
                        progbar.visibility = View.GONE
                        dialogbuilderYakin.dismiss()
                    }

                    viewYakinDialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                        dialogbuilderYakin.dismiss()

                        //
                        // Dialog Berhasil Batal (Layer 3)
                        val builderBerhasil = AlertDialog.Builder(requireContext()) // Perbaikan disini
                        val viewBerhasil = layoutInflater.inflate(R.layout.item_berhasil_1, null) // Inflasi layout dialog
                        builderBerhasil.setView(viewBerhasil) // Pasang layout ke dialog
                        val dialogBerhasil = builderBerhasil.create() // Perbaikan disini dengan menggunakan builder2.create()

                        // Mengatur ukuran wrap_content untuk dialog
                        dialogBerhasil.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        // Set background untuk dialog
                        dialogBerhasil.window?.setBackgroundDrawable(requireContext().getDrawable(R.drawable.borderadius_dialog_white))
                        dialogBerhasil.setCancelable(false) // Dialog dapat ditutup dengan back atau klik luar

                        Toast.makeText(requireContext(), "Alasan Dipilih: $alasan", Toast.LENGTH_SHORT).show()

                        //Delete Chat
                        deleteChat("${thNoRuid}")
                        // Delete Pendaftaran
                        //deletePendaftaran("${thNoRuid}") (Logic Lain)
                        deletePendaftaran("${thNoPendaftaran}")

                        Handler().postDelayed({
                            progbar.visibility = View.GONE
                            dialogBerhasil.show() // Tampilkan dialog kedua dengan benar

                            // Ke Pilih Penjadwalan
                            viewBerhasil.findViewById<Button>(R.id.btn_pilihjadwal).setOnClickListener {
                                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Penjadwalan()).commit()
                                requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.checked
                                dialogBerhasil.dismiss()
                            }

                            // Ke Home
                            viewBerhasil.findViewById<Button>(R.id.btn_home).setOnClickListener {
                                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home()).commit()
                                requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.home
                                dialogBerhasil.dismiss()
                            }
                        }, 3000)
                    }
                } else {
                    Toast.makeText(requireContext(), "Pilih alasan terlebih dahulu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    private lateinit var progbar:ProgressBar

    private fun deleteChat(rUid: String){
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        // Perbarui status berdasarkan perubahan secara real time menggunakan shared view model (live data front end)
        sharedViewModel.riwayatStatusButuhPersetujuan.value = null
        sharedViewModel.riwayatStatusDisetujui.value = null
        sharedViewModel.riwayatTopik.value = null
        sharedViewModel.riwayatLayanan.value = null
        sharedViewModel.riwayatTanggal.value = null
        sharedViewModel.riwayatOffOrOn.value = null
        sharedViewModel.riwayatDescTopik.value = null
        sharedViewModel.riwayatNoPendaftaran.value = null
        // Tidak Bisa Mendaftar
        sharedViewModel.statusPendaftaran.value = false

        //Delete Chat
        val messagesRef = db.collection("chats").document(rUid).collection("messages")
        messagesRef.get().addOnSuccessListener { querySnapshot ->
            val batch = db.batch()
            for (document in querySnapshot) {
                batch.delete(document.reference)
            }

            batch.commit().addOnSuccessListener {
                Log.d("Firestore Butuh Persetujuan", "Successfully deleted all messages in chats for: Butuh Persetujuan $")
            }.addOnFailureListener { e ->
                Log.e("Firestore Butuh Persetujuan", "Failed to delete messages in chats for: Butuh Persetujuan $", e)
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore Butuh Persetujuan", "Failed to fetch messages for chats: Butuh Persetujuan $", e)
        }
    }

    private fun deletePendaftaran(noPendaftaran: String,){
        //Delete Pendaftaran
        db.collection("daftarKonseling").whereEqualTo("nomorPendaftaran", noPendaftaran)
            .get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        db.collection("daftarKonseling").document(document.id).delete().addOnSuccessListener {
                            Log.d("Firestore Butuh Persetujuan", "Successfully deleted document: ${document.id}")
                        }.addOnFailureListener { e ->
                            Log.e("Firestore Butuh Persetujuan", "Failed to delete document: ${document.id}", e)
                        }
                    }
                } else {
                    Log.d("Firestore Butuh Persetujuan", "No documents found for nomorPendaftaran: $noPendaftaran")
                }
            }.addOnFailureListener { e ->
                Log.e("Firestore Butuh Persetujuan", "Failed to fetch documents for nomorPendaftaran: $noPendaftaran", e)
            }
    }
}
