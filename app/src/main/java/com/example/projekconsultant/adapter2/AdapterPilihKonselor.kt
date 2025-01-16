package com.example.projekconsultant.adapter2

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.ProfileKonselorHome
import com.google.firebase.firestore.FirebaseFirestore

class AdapterPilihKonselor(private val data: List<ProfileKonselorHome>,
                           private val nomorPendaftaran: String,
                           private val jadwalKonseling:String,
                           private val layananKonseling:String,
                           private val ruid:String,
                           private val onFinishCallback: () -> Unit) : RecyclerView.Adapter<AdapterPilihKonselor.Pilih>() {

    inner class Pilih(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout: LinearLayout = itemView.findViewById(R.id.main)
        val nama: TextView = itemView.findViewById(R.id.textViewName)
        val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        val btnPilih: Button = itemView.findViewById(R.id.btnPilih)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pilih {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pilih_konselor, parent, false)
        return Pilih(view)
    }

    override fun onBindViewHolder(holder: Pilih, position: Int) {
        val datas = data[position]

        holder.nama.text = datas.name
        holder.profileImage.setImageResource(datas.imageResourceId)
        holder.btnPilih.setOnClickListener {
            showPilihKonselorDialogInput(holder.itemView.context, datas, position == data.size - 1)
        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }

    private var isCustomDialogVisible = false
    private fun showPilihKonselorDialogInput(context: Context, data: ProfileKonselorHome, isLast: Boolean) {
        if (isCustomDialogVisible) return // Cegah dialog muncul berulang
        isCustomDialogVisible = true

        val dialogView: View = if (isLast) {
            LayoutInflater.from(context).inflate(R.layout.item_dialog_pilih_konselor_2, null) // Layout khusus untuk item terakhir
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_dialog_pilih_konselor, null) // Layout biasa
        }

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setCancelable(true)

        if (isLast) {
            val inputName:EditText = dialogView.findViewById(R.id.editTextKonselor)
            val btnPilih: Button = dialogView.findViewById(R.id.btn_pilih)

            btnPilih.setOnClickListener {
                if(inputName.text.trim().isNotEmpty()){
                    val updateData = mapOf(
                        "butuhPersetujuan" to false,
                        "diSetujui" to true,
                        "konselor" to "${inputName.text}"
                    )
                    updateMultipleJadwalFields(updateData, nomorPendaftaran)
                    sendMessageAdminReminderApproved(context)
                    onFinishCallback()
                    dialog.dismiss()
                }
                dialog.dismiss()
            }
        } else {
            val btnNO: Button = dialogView.findViewById(R.id.btn_tidak)
            val btnYES: Button = dialogView.findViewById(R.id.btn_pilih)

            btnYES.setOnClickListener {
                val updateData = mapOf(
                    "butuhPersetujuan" to false,
                    "diSetujui" to true,
                    "konselor" to data.name
                )
                updateMultipleJadwalFields(updateData, nomorPendaftaran)
                sendMessageAdminReminderApproved(context)
                onFinishCallback()
                dialog.dismiss()
            }

            btnNO.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            isCustomDialogVisible = false // Reset status dialog saat ditutup
        }

        dialog.show()
    }

    private lateinit var db:FirebaseFirestore
    private fun updateMultipleJadwalFields(updateData: Map<String, Any>, nomorPendaftaran: String) {
        db = FirebaseFirestore.getInstance()

        db.collection("daftarKonseling").whereEqualTo("nomorPendaftaran", nomorPendaftaran)
            .get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        // Perbarui beberapa kolom dalam dokumen ini
                        document.reference.update(updateData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Fields untuk $nomorPendaftaran berhasil diperbarui: $updateData")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Gagal memperbarui fields untuk $nomorPendaftaran", e)
                            }
                    }
                } else {
                    Log.d("Firestore", "Tidak ada jadwal dengan nomor pendaftaran $nomorPendaftaran")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal mengambil data jadwal untuk $nomorPendaftaran", e)
            }
    }

    private fun sendMessageAdminReminderApproved(context: Context){
        db = FirebaseFirestore.getInstance()

        val approved = MessageReminder(
            "admin",
            "${jadwalKonseling}",
            "${layananKonseling}",
            System.currentTimeMillis(),
            false,
            "approved"
        )

        db.collection("chats").document(ruid).collection("messages")
            .add(approved).addOnSuccessListener {
                Log.d("successful", "First Chat Admin")
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}



















//private fun showPilihKonselorDialog(context: Context, data: ProfileKonselorHome) {
//    val dialogView = LayoutInflater.from(context).inflate(R.layout.item_dialog_pilih_konselor, null)
//
//    val dialog = Dialog(context)
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.setContentView(dialogView)
//
//    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
//    dialog.window?.setGravity(Gravity.BOTTOM)
//    dialog.setCancelable(false)
//
//    val btnNO: Button = dialogView.findViewById(R.id.btn_tidak)
//    val btnYES: Button = dialogView.findViewById(R.id.btn_pilih)
//
//    btnYES.setOnClickListener {
//        val updateData = mapOf(
//            "butuhPersetujuan" to false,
//            "diSetujui" to true,
//            "konselor" to data.name
//        )
//        updateMultipleJadwalFields(updateData, nomorPendaftaran)
//
//        dialog.dismiss()
//    }
//
//    btnNO.setOnClickListener {
//        dialog.dismiss()
//    }
//
//    dialog.show()
//}



//fun updateJadwalStatus(nomorPendaftaran: String, statusBaru: String) {
//    val db = FirebaseFirestore.getInstance()
//
//    // Cari dokumen berdasarkan nomor pendaftaran
//    db.collection("jadwal")
//        .whereEqualTo("nomorPendaftaran", nomorPendaftaran)
//        .get()
//        .addOnSuccessListener { querySnapshot ->
//            if (!querySnapshot.isEmpty) {
//                // Ambil dokumen pertama (atau iterasi jika ada lebih dari satu)
//                for (document in querySnapshot.documents) {
//                    // Update status jadwal
//                    document.reference.update("status", statusBaru)
//                        .addOnSuccessListener {
//                            Log.d("Firestore", "Status berhasil diperbarui!")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("Firestore", "Gagal memperbarui status", e)
//                        }
//                }
//            } else {
//                Log.d("Firestore", "Tidak ada jadwal dengan nomor pendaftaran $nomorPendaftaran")
//            }
//        }
//        .addOnFailureListener { e ->
//            Log.e("Firestore", "Gagal mengambil data jadwal", e)
//        }
//}
