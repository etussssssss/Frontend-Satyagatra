package com.example.projekconsultant.adapter2

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.admin.DataPendaftaran
import com.google.firebase.firestore.FirebaseFirestore

class AdapterAdminDiSetujuiOnline(private var data: List<DataPendaftaran>) : RecyclerView.Adapter<AdapterAdminDiSetujuiOnline.DiSetujuiOnline>() {
    inner class DiSetujuiOnline(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.nama)
        val topik: TextView = itemView.findViewById(R.id.topik)
        val tanggal:TextView = itemView.findViewById(R.id.tanggal)
        val offorn: TextView = itemView.findViewById(R.id.offoron)
        val descriptionTextView: TextView = itemView.findViewById(R.id.deskripsTopik)
        val selesai: Button = itemView.findViewById(R.id.pilih)
        val toggleTextView: TextView = itemView.findViewById(R.id.toggleTextView)

        val ppDef: ImageView = itemView.findViewById(R.id.imageViewProfileDes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiSetujuiOnline {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_disetujui_1, parent, false)
        return DiSetujuiOnline(view)
    }


    override fun onBindViewHolder(holder: DiSetujuiOnline, position: Int) {
        val datas = data[position]
        holder.nama.text = "${datas.nama}"
        holder.topik.text  = "${datas.PilihTopikKonsultasi}"
        holder.tanggal.text = "${datas.PilihTanggal}"
        holder.offorn.text = "${datas.onoff}"
        holder.descriptionTextView.text = "${datas.IsiCurhatan}"

        holder.selesai.setOnClickListener {
            showCustomDialog(holder.itemView.context, datas)
        }

        holder.toggleTextView.setOnClickListener {
            if (holder.descriptionTextView.maxLines == 3) {
                // Tampilkan seluruh teks
                holder.descriptionTextView.maxLines = Int.MAX_VALUE
                holder.toggleTextView.text = "Lihat lebih sedikit"
            } else {
                // Kembali ke teks pendek
                holder.descriptionTextView.maxLines = 3
                holder.toggleTextView.text = "Lihat lebih banyak"
            }
        }

        holder.ppDef.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            val inflater = LayoutInflater.from(holder.itemView.context)
            val viewD = inflater.inflate(R.layout.item_detail_riwayat_povadmin, null)

            builder.setView(viewD)
            val dialog = builder.create()

            // Mengatur ukuran dialog agar wrap_content
            dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            // Mengatur background dengan drawable
            dialog.window?.setBackgroundDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.borderadius_dialog_white)
            )

            dialog.setCancelable(true) // Dialog bisa ditutup dengan back atau klik di luar dialog
            dialog.show()

            viewD.findViewById<TextView>(R.id.nama).text = "Nama: ${datas.nama}"
            viewD.findViewById<TextView>(R.id.gender).text = "Gender: ${datas.gender}"
            viewD.findViewById<TextView>(R.id.umur).text = "Umur: ${datas.umur}"
            viewD.findViewById<TextView>(R.id.layanan).text = "Layanan: ${datas.PilihLayananKonsultasi}"
            viewD.findViewById<TextView>(R.id.nomorTelepon).text = "Nomor Telp: ${datas.nomorTeleponUser}"
            viewD.findViewById<TextView>(R.id.nomorPendaftaran).text = "Nomor Pendaftaran: ${datas.nomorPendaftaran}"
            viewD.findViewById<TextView>(R.id.konselor).text = "Konselor/Peer Konselor: ${datas.konselor}"
            //
            val instansi = viewD.findViewById<TextView>(R.id.internaloreksternal)
            val fakultas = viewD.findViewById<TextView>(R.id.fakultas)

            instansi.text = "Instansi: " + if (datas.type == "I") "Internal" else "Eksternal"

            viewD.findViewById<TextView>(R.id.profesi).text = "Profesi : ${datas.profesi}"
            fakultas.visibility = if ("${datas.type}" == "E") View.GONE else View.VISIBLE
            fakultas.text = "Fakultas : ${datas.fakultas}"

            viewD.findViewById<ImageButton>(R.id.copy).setOnClickListener {
                val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", "${datas.nomorTeleponUser}")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(holder.itemView.context, "Teks disalin ke clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }

    private var dialog: Dialog? = null
    //Dialog Custom Selesai
    private fun showCustomDialog(context: Context, dataUser: DataPendaftaran) {
        if (dialog != null && dialog!!.isShowing) return // Cek apakah dialog sedang ditampilkan
        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet_dialog_1, null)
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogView)

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.attributes?.windowAnimations = R.style.DialogAnimation
            window?.setGravity(Gravity.BOTTOM)
            setCancelable(true)
            val buttonSelesai: Button = dialogView.findViewById(R.id.pilihkan)
            val nama: TextView = dialogView.findViewById(R.id.nama)
            val jadwal: TextView = dialogView.findViewById(R.id.jadwal)
            val layanan: TextView = dialogView.findViewById(R.id.layanan)
            val topik: TextView = dialogView.findViewById(R.id.topik)
            val deskripsi: TextView = dialogView.findViewById(R.id.deskripsi)

            nama.text = "Nama: ${dataUser.nama}"
            jadwal.text = "Jadwal Konseling: ${dataUser.PilihTanggal}"
            layanan.text = "Layanan Konseling: ${dataUser.PilihLayananKonsultasi}"
            topik.text = "Topik Konseling: ${dataUser.PilihTopikKonsultasi}"
            deskripsi.text = "Deskripsi Masalah:\n${dataUser.IsiCurhatan}"
            buttonSelesai.text = "Selesai"
            buttonSelesai.setOnClickListener {
                if("${dataUser.type}" == "I"){
                    val data = mapOf(
                        "type" to "${dataUser.type}",
                        "nama" to "${dataUser.nama}",
                        "konselor" to "${dataUser.konselor}",
                        "pilihTanggal" to "${dataUser.PilihTanggal}",
                        "pilihTopikKonsultasi" to "${dataUser.PilihTopikKonsultasi}",
                        "isiCurhatan" to "${dataUser.IsiCurhatan}",
                        "onoff" to "${dataUser.onoff}",
                        "umur" to dataUser.umur.toInt(),
                        "gender" to "${dataUser.gender}",
                        "status" to "${dataUser.status}",
                        "domisili" to "${dataUser.domisili}",
                        "profesi" to "${dataUser.profesi}",
                        "fakultas" to "${dataUser.fakultas}"
                    )
                    selesaiKonselingCreateRiwayat(context, "${dataUser.uuid}", "${dataUser.nomorPendaftaran}", data)
                    dismiss()
                }

                if("${dataUser.type}" == "E"){
                    val data = mapOf(
                        "type" to "${dataUser.type}",
                        "nama" to "${dataUser.nama}",
                        "konselor" to "${dataUser.konselor}",
                        "pilihTanggal" to "${dataUser.PilihTanggal}",
                        "pilihTopikKonsultasi" to "${dataUser.PilihTopikKonsultasi}",
                        "isiCurhatan" to "${dataUser.IsiCurhatan}",
                        "onoff" to "${dataUser.onoff}",
                        "umur" to dataUser.umur.toInt(),
                        "gender" to "${dataUser.gender}",
                        "status" to "${dataUser.status}",
                        "domisili" to "${dataUser.domisili}",
                        "profesi" to "${dataUser.profesi}",
                    )
                    selesaiKonselingCreateRiwayat(context, "${dataUser.uuid}", "${dataUser.nomorPendaftaran}", data)
                    dismiss()
                }
                dismiss()
            }
            setOnDismissListener {
                dialog = null // Reset dialog saat ditutup
            }
        }
        dialog?.show()
    }

    private lateinit var db: FirebaseFirestore
    private fun selesaiKonselingCreateRiwayat(context:Context, uuidUsers:String, noPendaftaran: String,riwayatUsers: Map<String, Any>){
        db = FirebaseFirestore.getInstance()
        //Delete Chat
        val messagesRef = db.collection("chats").document(uuidUsers).collection("messages")
        messagesRef.get().addOnSuccessListener { querySnapshot ->
            val batch = db.batch()
            for (document in querySnapshot) {
                batch.delete(document.reference)
            }

            batch.commit().addOnSuccessListener {
                Log.d("Firestore", "Successfully deleted all messages in chats for: $noPendaftaran")
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Failed to delete messages in chats for: $noPendaftaran", e)
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Failed to fetch messages for chats: $noPendaftaran", e)
        }

        //Add Riwayat
        db.collection("riwayat").document(uuidUsers).collection("allRiwayat").document(noPendaftaran).set(riwayatUsers).addOnSuccessListener {
            Log.d("successful", "Adapter Selesai")
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        //Delete Pendaftaran
        db.collection("daftarKonseling").whereEqualTo("nomorPendaftaran", noPendaftaran)
            .get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        db.collection("daftarKonseling").document(document.id).delete().addOnSuccessListener {
                            Log.d("Firestore", "Successfully deleted document: ${document.id}")
                        }.addOnFailureListener { e ->
                            Log.e("Firestore", "Failed to delete document: ${document.id}", e)
                        }
                    }
                } else {
                    Log.d("Firestore", "No documents found for nomorPendaftaran: $noPendaftaran")
                }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch documents for nomorPendaftaran: $noPendaftaran", e)
            }
    }

    // Untuk Update Data yg masuk
    fun updateData(newData: List<DataPendaftaran>) {
        data = newData
        notifyDataSetChanged()
    }

    fun delete(){
//        db.collection("chats").document(noPendaftaran).collection("messages")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//            if (!querySnapshot.isEmpty) {
//                for (document in querySnapshot) {
//                    db.collection("chats").document(document.id).delete().addOnSuccessListener {
//                        Log.d("Firestore", "Successfully deleted document: ${document.id}")
//                    }.addOnFailureListener { e ->
//                        Log.e("Firestore", "Failed to delete document: ${document.id}", e)
//                    }
//                }
//            } else {
//                Log.d("Firestore", "No documents found for chats: $noPendaftaran")
//            }
//        }.addOnFailureListener { e ->
//            Log.e("Firestore", "Failed to fetch documents for chats: $noPendaftaran", e)
//        }
    }
}