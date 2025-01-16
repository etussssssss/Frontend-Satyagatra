package com.example.projekconsultant.adapter2

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.JadwalTanggal
import com.example.projekconsultant.adapter.JadwalTanggalAdapter
import com.example.projekconsultant.admin.DataPendaftaran
import com.example.projekconsultant.admin.PilihKonselor
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class AdapterAdminButuhPersetujuanOffline(private var data: List<DataPendaftaran>) : RecyclerView.Adapter<AdapterAdminButuhPersetujuanOffline.ButuhPersetujuanOffline>() {
    inner class ButuhPersetujuanOffline(itemView: View) : RecyclerView.ViewHolder(itemView) { // val mainLayout: LinearLayout = itemView.findViewById(R.id.main)
        val nama:TextView = itemView.findViewById(R.id.nama)
        val topik:TextView = itemView.findViewById(R.id.topik)
        val tanggal:TextView = itemView.findViewById(R.id.tanggal)
        val offorn:TextView = itemView.findViewById(R.id.offoron)

        val descriptionTextView:TextView = itemView.findViewById(R.id.deskripsTopik)
        val buttonPilihKonselor:Button = itemView.findViewById(R.id.pilih)
        val buttonReschedule:Button = itemView.findViewById(R.id.reschedule)
        val toggleTextView:TextView = itemView.findViewById(R.id.toggleTextView)

        val ppDef:ImageView = itemView.findViewById(R.id.imageViewProfileDes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButuhPersetujuanOffline {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_butuhpersetujuan_1, parent, false)
        return ButuhPersetujuanOffline(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ButuhPersetujuanOffline, position: Int) {
        val datas = data[position]
        holder.nama.text = "${datas.nama}"
        holder.topik.text = "${datas.PilihTopikKonsultasi}"
        holder.tanggal.text = "${datas.PilihTanggal}"
        holder.offorn.text = "${datas.onoff}"
        holder.descriptionTextView.text = "${datas.IsiCurhatan}"
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

        holder.buttonPilihKonselor.setOnClickListener {
            showCustomDialog(holder.itemView.context, datas)
        }

        holder.buttonReschedule.setOnClickListener {
            showRescheduleDialog(holder.itemView.context, datas)
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
            viewD.findViewById<TextView>(R.id.konselor).text = "Konselor/Peer Konselor: Butuh Persetujuan"
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
        return data.size
    }

    private var isCustomDialogVisible = false
    private var isRescheduleDialogVisible = false

    private fun showCustomDialog(context: Context, dataUser: DataPendaftaran) {
        if (isCustomDialogVisible || isRescheduleDialogVisible) return // Cegah dialog muncul berulang
        isCustomDialogVisible = true

        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet_dialog_1, null)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setCancelable(true)

        val buttonClose: Button = dialogView.findViewById(R.id.pilihkan)
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

        buttonClose.setOnClickListener {
            val intent = Intent(context, PilihKonselor::class.java)
            intent.putExtra("NOMORPENDAFTARAN", "${dataUser.nomorPendaftaran}")
            intent.putExtra("TANGGALKONSELING", "${dataUser.PilihTanggal}")
            intent.putExtra("LAYANANKONSELING", "${dataUser.PilihLayananKonsultasi}")
            intent.putExtra("RUID","${dataUser.uuid}")
            context.startActivity(intent)
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            isCustomDialogVisible = false // Reset status dialog saat ditutup
        }

        dialog.show()
    }

    private var pilihTanggal: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showRescheduleDialog(context: Context, dataUser: DataPendaftaran) {
        if (isRescheduleDialogVisible || isCustomDialogVisible) return // Cegah dialog muncul berulang
        isRescheduleDialogVisible = true

        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_reschedule_admin, null)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setCancelable(true)

        val buttonReschedule: Button = dialogView.findViewById(R.id.pilihkan)
        val nama: TextView = dialogView.findViewById(R.id.nama)
        val jadwal: TextView = dialogView.findViewById(R.id.jadwal)
        val layanan: TextView = dialogView.findViewById(R.id.layanan)
        val topik: TextView = dialogView.findViewById(R.id.topik)
        val deskripsi: TextView = dialogView.findViewById(R.id.deskripsi)
        val r: RecyclerView = dialogView.findViewById(R.id.recyclerViewJadwal1)

        nama.text = "Nama: ${dataUser.nama}"
        jadwal.text = "Jadwal Sekarang: ${dataUser.PilihTanggal}\nUbah Jadwal: ${pilihTanggal}"
        layanan.text = "Layanan Konseling: ${dataUser.PilihLayananKonsultasi}"
        topik.text = "Topik Konseling: ${dataUser.PilihTopikKonsultasi}"
        deskripsi.text = "Deskripsi Masalah:\n${dataUser.IsiCurhatan}"

        r.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale("id", "ID"))
        var currentDate = LocalDate.now()
        val datas = mutableListOf<JadwalTanggal>()
        for (i in 1 until 21) {
            currentDate = currentDate.plusDays(1)
            val dayName = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
            val formattedDate = currentDate.format(formatter)
            datas.add(JadwalTanggal(dayName, formattedDate))
        }

        val adapter = JadwalTanggalAdapter(datas) { item ->
            pilihTanggal = item?.tanngalHari ?: ""

            if ("${dataUser.PilihTanggal}".equals("$pilihTanggal", true)) {
                jadwal.text = "Jadwal Masih Sama"
                jadwal.setTextColor(context.getColor(R.color.red500))
            } else {
                jadwal.text = "Jadwal Sekarang: ${dataUser.PilihTanggal}\nUbah Jadwal: ${pilihTanggal}"
                jadwal.setTextColor(context.getColor(R.color.black))
            }
        }
        r.adapter = adapter

        buttonReschedule.setOnClickListener {
            if (pilihTanggal.isNotEmpty() && !"${dataUser.PilihTanggal}".equals("$pilihTanggal", true)) {
                val updateData = mapOf(
                    "pilihTanggal" to pilihTanggal,
                )
                updateMultipleRescheduleJadwalFields(updateData, dataUser.nomorPendaftaran)
                sendMessageAdminReminderReschedule(context, pilihTanggal, dataUser.PilihLayananKonsultasi, dataUser.uuid)
                dialog.dismiss()
            } else {
                jadwal.text = when {
                    pilihTanggal.isEmpty() -> "Silahkan Pilih Terlebih Dahulu"
                    dataUser.PilihTanggal.equals(pilihTanggal, true) -> "Jadwal Masih Sama"
                    else -> ""
                }
                jadwal.setTextColor(context.getColor(R.color.red500))
            }
        }
        dialog.setOnDismissListener {
            isRescheduleDialogVisible = false // Reset status dialog saat ditutup
        }
        dialog.show()
    }

    fun updateData(newData: List<DataPendaftaran>) {
        data = newData
        notifyDataSetChanged()
    }

    private lateinit var db: FirebaseFirestore
    private fun updateMultipleRescheduleJadwalFields(updateData: Map<String, Any>, nomorPendaftaran: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("daftarKonseling").whereEqualTo("nomorPendaftaran", nomorPendaftaran)
            .get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        // Perbarui beberapa kolom dalam dokumen ini
                        document.reference.update(updateData).addOnSuccessListener {
                                Log.d("Firestore", "Fields untuk $nomorPendaftaran berhasil diperbarui: $updateData")
                            }.addOnFailureListener { e ->
                                Log.e("Firestore", "Gagal memperbarui fields untuk $nomorPendaftaran", e)
                            }
                    }
                } else {
                    Log.d("Firestore", "Tidak ada jadwal dengan nomor pendaftaran $nomorPendaftaran")
                }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Gagal mengambil data jadwal untuk $nomorPendaftaran", e)
            }
    }

    //private fun sendMessageAdminReminderReschedule(context: Context, jadwalKonsultasi:String, layananKonsultasi:String, nomorPendaftaran: String){
    private fun sendMessageAdminReminderReschedule(context: Context, jadwalKonsultasi:String, layananKonsultasi:String, rUid: String){
        db = FirebaseFirestore.getInstance()

        val msg = MessageReminder(
            "admin",
            "${jadwalKonsultasi}",
            "${layananKonsultasi}",
            System.currentTimeMillis(),
            false,
            "reschedule"
        )

        db.collection("chats").document(rUid).collection("messages").add(msg).addOnSuccessListener {
                Log.d("successful", "First Chat Admin")
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//override fun onBindViewHolder(holder: AdapterAdminButuhPersetujuanOffline.ButuhPersetujuanOffline, position: Int) {
//    val datas = data[position]
//
//    holder.nama.text = "${datas.nama}"
//    holder.topik.text  = "${datas.PilihTopikKonsultasi}"
//    holder.tanggal.text = "${datas.PilihTanggal}"
//    holder.offorn.text = "${datas.onoff}"
//    holder.descriptionTextView.text = "${datas.IsiCurhatan}"
//
//    holder.toggleTextView.setOnClickListener {
//        if (holder.descriptionTextView.maxLines == 3) {
//            // Tampilkan seluruh teks
//            holder.descriptionTextView.maxLines = Int.MAX_VALUE
//            holder.toggleTextView.text = "Lihat lebih sedikit"
//        } else {
//            // Kembali ke teks pendek
//            holder.descriptionTextView.maxLines = 3
//            holder.toggleTextView.text = "Lihat lebih banyak"
//        }
//    }
//
//    holder.buttonPilihKonselor.setOnClickListener {
//        showCustomDialog(holder.itemView.context, datas)
//    }
//
//    holder.buttonReschedule.setOnClickListener {
//        showRescheduleDialog(holder.itemView.context, datas)
//    }
//}
//
//override fun getItemCount(): Int {
//    return  data.size
//}
//
//private fun showCustomDialog(context: Context, dataUser: DataPendaftaran) {
//    val dialogView = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet_dialog_1, null)
//
//    val dialog = Dialog(context)
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.setContentView(dialogView)
//
//    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
//    dialog.window?.setGravity(Gravity.BOTTOM)
//    dialog.setCancelable(true)
//
//    val buttonClose: Button = dialogView.findViewById(R.id.pilihkan)
//    val nama:TextView = dialogView.findViewById(R.id.nama)
//    val jadwal:TextView = dialogView.findViewById(R.id.jadwal)
//    val layanan:TextView = dialogView.findViewById(R.id.layanan)
//    val topik:TextView = dialogView.findViewById(R.id.topik)
//    val deskripsi:TextView = dialogView.findViewById(R.id.deskripsi)
//
//    nama.text = "Nama: ${dataUser.nama}"
//    jadwal.text = "Jadwal Konseling: ${dataUser.PilihTanggal}"
//    layanan.text = "Layanan Konseling: ${dataUser.PilihLayananKonsultasi}"
//    topik.text = "Topik Konseling: ${dataUser.PilihTopikKonsultasi}"
//    deskripsi.text = "Deskripsi Masalah:\n${dataUser.IsiCurhatan}"
//
//
//
//    buttonClose.setOnClickListener {
//        val intent = Intent(context, PilihKonselor::class.java)
//        intent.putExtra("NOMORPENDAFTARAN","${dataUser.nomorPendaftaran}")
//        intent.putExtra("TANGGALKONSELING","${dataUser.PilihTanggal}")
//        intent.putExtra("LAYANANKONSELING","${dataUser.PilihLayananKonsultasi}")
//        context.startActivity(intent)
//        dialog.dismiss()
//    }
//
//    dialog.show()
//}
//
//private var pilihTanggal: String = ""
//@RequiresApi(Build.VERSION_CODES.O)
//private fun showRescheduleDialog(context: Context, dataUser: DataPendaftaran) {
//    val dialogView = LayoutInflater.from(context).inflate(R.layout.item_reschedule_admin, null)
//
//    val dialog = Dialog(context)
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.setContentView(dialogView)
//
//    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
//    dialog.window?.setGravity(Gravity.BOTTOM)
//    dialog.setCancelable(true)
//
//    val buttonClose: Button = dialogView.findViewById(R.id.pilihkan)
//    val nama:TextView = dialogView.findViewById(R.id.nama)
//    val jadwal:TextView = dialogView.findViewById(R.id.jadwal)
//    val layanan:TextView = dialogView.findViewById(R.id.layanan)
//    val topik:TextView = dialogView.findViewById(R.id.topik)
//    val deskripsi:TextView = dialogView.findViewById(R.id.deskripsi)
//    val r: RecyclerView = dialogView.findViewById(R.id.recyclerViewJadwal1)
//
//
//    nama.text = "Nama: ${dataUser.nama}"
//    jadwal.text = "Jadwal Sekarang :${dataUser.PilihTanggal}\nUbah Jadwal :${pilihTanggal}"
//    layanan.text = "Layanan Konseling: ${dataUser.PilihLayananKonsultasi}"
//    topik.text = "Topik Konseling: ${dataUser.PilihTopikKonsultasi}"
//    deskripsi.text = "Deskripsi Masalah:\n${dataUser.IsiCurhatan}"
//
//    r.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//
//    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale("id", "ID"))
//    var currentDate = LocalDate.now()
//
//    val datas = mutableListOf<JadwalTanggal>()
//    for (i in 1 until 21) {
//        currentDate = currentDate.plusDays(1)
//        val dayName = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
//        val formattedDate = currentDate.format(formatter)
//
//        datas.add(JadwalTanggal(dayName, formattedDate))
//    }
//
//    val adapter = JadwalTanggalAdapter(datas) { item ->
//        pilihTanggal = item?.tanngalHari ?: ""
//
//        if("${dataUser.PilihTanggal}".equals("$pilihTanggal", true)){
//            jadwal.text = "Jadwal Masih Sama"
//            jadwal.setTextColor(context.getColor(R.color.red500))
//        }else{
//            jadwal.text = "Jadwal Sekarang :${dataUser.PilihTanggal}\nUbah Jadwal :${pilihTanggal}"
//            jadwal.setTextColor(context.getColor(R.color.black))
//        }
//    }
//    r.adapter = adapter
//
//
//    buttonClose.setOnClickListener {
//        if(pilihTanggal.isNotEmpty()){
//
//
//            dialog.dismiss()
//        }else{
//            jadwal.text = "Silahkan Pilih Terlebih Dahulu"
//            jadwal.setTextColor(context.getColor(R.color.red500))
//        }
//    }
//
//    dialog.show()
//}