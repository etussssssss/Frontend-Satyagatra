package com.example.projekconsultant.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter2.Message
import com.example.projekconsultant.adapter2.MessageDate
import com.example.projekconsultant.adapter2.MessageReminder
import com.example.projekconsultant.admin.ForumChatAdmin
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

class AdapterAdminChat(private var data: List<AdapterAdminDataChat>): RecyclerView.Adapter<AdapterAdminChat.ChatsA>() {

    // Method untuk memperbarui data di adapter
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<AdapterAdminDataChat>) {
        data = newData
        notifyDataSetChanged()  // Memberitahu adapter untuk memperbarui tampilan
    }

    inner class ChatsA(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout:LinearLayout = itemView.findViewById(R.id.main)
        val namauser: TextView = itemView.findViewById(R.id.nama)
        val message: TextView = itemView.findViewById(R.id.textmsg)
        val countMsg:TextView = itemView.findViewById(R.id.countmsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsA {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_list_chat, parent, false)
        return ChatsA(view)
    }

    override fun onBindViewHolder(holder: ChatsA, position: Int) {
        val datas = data[position]

        val butuhPersetujuanAdmin = datas.pesananUser.butuhPersetujuan
        val diSetujuiAdmin = datas.pesananUser.diSetujui

        holder.namauser.text = "${datas.pesananUser.namaUser}"
        holder.message.text  = getLatestMessage(datas.semuaPesan, butuhPersetujuanAdmin ?:false, diSetujuiAdmin?:false)

        // Hitung pesan yang belum dibaca
        val countMsg = datas.semuaPesan.count {
            it["read"] as? Boolean == false && it["sender"] as? String == "user"
        }

        if (countMsg == 0) {
            holder.countMsg.visibility = View.GONE
        } else {
            holder.countMsg.visibility = View.VISIBLE
            holder.countMsg.text = "$countMsg"
        }

        holder.mainLayout.setOnClickListener {
            val messagesText = ArrayList<Message>()
            val messagesRemind = ArrayList<MessageReminder>()
            val messagesDate = ArrayList<MessageDate>()
            val lastDate = mutableSetOf<String>()

            datas.semuaPesan.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
                when {
                    item.containsKey("sender") && item.containsKey("text") -> {
                        val sender = item["sender"] as? String ?: "Unknown"
                        val content = item["text"] as? String ?: "Tidak ada pesan"
                        val timestamp = item["timestamp"] as? Long ?: return@forEach
                        val read = item["read"] as? Boolean ?: false

                        if (!read) {
                            // Update status read di Firestore
                            updateUserMessagesStatusToRead("${datas.pesananUser.noRuid}")
                        }
                        sendMsgDate(messagesDate, timestamp, lastDate)
                        messagesText.add(Message(sender, content, timestamp, read))
                    }
                    item.containsKey("tanggalPesan") && item.containsKey("layanan") -> {
                        val sender = item["sender"] as? String ?: "Unknown"
                        val date = item["tanggalPesan"] as? String ?: ""
                        val method = item["layanan"] as? String ?: ""
                        val timestamp = item["timestamp"] as? Long ?: return@forEach
                        val read = item["read"] as? Boolean ?: false
                        val statusReminder = item["statusReminder"] as? String ?: ""

                        if (!read) {
                            // Update status read di Firestore
                            updateUserMessagesStatusToRead("${datas.pesananUser.noRuid}")
                        }
                        sendMsgDate(messagesDate, timestamp, lastDate)
                        messagesRemind.add(MessageReminder(sender, date, method, timestamp, read, statusReminder))
                    }
                }
            }

            val intent = Intent(holder.itemView.context, ForumChatAdmin::class.java)
            intent.putParcelableArrayListExtra("TEXT", messagesText)
            intent.putParcelableArrayListExtra("REMINDER", messagesRemind)
            intent.putParcelableArrayListExtra("DATE", messagesDate)

            intent.putExtra("NAMAUSER", datas.pesananUser.namaUser)
            intent.putExtra("NOPENDAFTARAN", datas.pesananUser.noRuid)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun sendMsgDate(messages: ArrayList<MessageDate>, timestamp: Long, lastDate: MutableSet<String>) {
        //val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date(timestamp))

        val messageDate = SimpleDateFormat("dd MMMM", Locale("id", "ID")).format(Date(timestamp))
        if (messageDate !in lastDate) {
            messages.add(MessageDate(messageDate))
            lastDate.add(messageDate)
        }
    }

    private fun getLatestMessage(chats: List<Map<String, Any>>, butuhPersetujuan:Boolean, diSetujui:Boolean): String {
        val latestChat = chats.maxByOrNull { it["timestamp"] as Long }
        var remindChat:String = if(butuhPersetujuan) "Butuh Persetujuan ${latestChat?.get("tanggalPesan") ?: ""}"
                                else if(diSetujui) "Di Setujui ${latestChat?.get("tanggalPesan") ?: ""}"
                                else "Pesan Terbaru"

        return latestChat?.get("text") as? String ?: "${remindChat}"
    }

    private fun updateUserMessagesStatusToRead(chatId: String) {
        val db = FirebaseFirestore.getInstance()

        // Akses koleksi pesan dalam chat
        val messagesRef = db.collection("chats").document(chatId).collection("messages")

        // Query untuk mendapatkan pesan yang belum dibaca dan dikirim oleh user
        messagesRef.whereEqualTo("sender", "user")  // Pastikan pesan dikirim oleh user
            .whereEqualTo("read", false)  // Hanya pesan yang belum dibaca
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        // Update status read menjadi true untuk pesan yang belum dibaca
                        document.reference.update("read", true).addOnSuccessListener {
                                Log.d("Firestore", "Pesan berhasil diperbarui menjadi dibaca.")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Gagal memperbarui status pesan", e)
                            }
                    }
                } else {
                    Log.d("Firestore", "Tidak ada pesan yang perlu diperbarui.")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Gagal mengambil pesan", e)
            }
    }
}
























//            val parcelableMessages = messages.filterIsInstance<Parcelable>() as ArrayList<Parcelable>
//            intent.putParcelableArrayListExtra("CHATS", parcelableMessages)


//private var datasChat: MutableList<Any>? = mutableListOf()
//
//private lateinit var s: ArrayList<Message>
//
//private lateinit var messagesDates: ArrayList<MessageDate>
//private lateinit var messagesReminds: ArrayList<MessageReminder>
//private lateinit var messagesText: ArrayList<Message>

//    private fun chat(datas: Any?) {
//        var lastDate: String? = null
//        val dataList = datas as? List<Map<String, Any>> ?: emptyList() // Cek tipe dan beri default empty list
//
////        Log.d("CHAT ADMIN NOMOR PENDAFTARAN", "$datas $dataList")
//
//        dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
//            when {
//                item.containsKey("sender") && item.containsKey("content") -> {
//                    // Menangani pesan
//                    val sender = item["sender"]
//                    val content = item["content"]
//                    val timestamp = item["timestamp"]
//                    val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(
//                        Date(timestamp as Long)
//                    )
//
//                    // Cek apakah tanggal berbeda dengan pesan sebelumnya
//                    if (messageDate != lastDate) {
//                        messagesDates.add(
//                            MessageDate(messageDate)
//                        )
//                        lastDate = messageDate
//                    }
//
//                    messagesText.add(
//                        Message(sender.toString(), content.toString(), timestamp as Long)
//                    )
//                }
//
//                item.containsKey("date") && item.containsKey("method") -> {
//                    // Menangani reminder
//                    val sender = item["sender"]
//                    val date = item["date"]
//                    val method = item["method"]
//                    val timestamp = item["timestamp"]
//                    val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(
//                        Date(timestamp as Long)
//                    )
//
//                    // Cek apakah tanggal berbeda dengan pesan sebelumnya
//                    if (messageDate != lastDate) {
//                        messagesDates.add(
//                            MessageDate(messageDate)
//                        )
//                        lastDate = messageDate
//                    }
//
//                    messagesReminds.add(
//                        MessageReminder(sender.toString(), date.toString(), method.toString(), timestamp as Long)
//                    )
//                }
//            }
//        }
//    }




//chat(datas.semuaPesan)



//    private var messages: MutableList<Any>? = mutableListOf()

//    private fun chat(datas: Any?) {
//        var lastDate: String? = null
//        val dataList = datas as? List<Map<String, Any>> ?: emptyList() // Cek tipe dan beri default empty list
//
//        dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
//            when {
//                item.containsKey("sender") && item.containsKey("content") -> {
//                    // Menangani pesan
//                    val sender = item["sender"]
//                    val content = item["content"]
//                    val timestamp = item["timestamp"]
//                    val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(
//                        Date(timestamp as Long)
//                    )
//
//                    // Cek apakah tanggal berbeda dengan pesan sebelumnya
//                    if (messageDate != lastDate) {
//                        messages?.add(
//                            MessageDate(messageDate)
//                        )
//                        lastDate = messageDate
//                    }
//
//                    messages?.add(
//                        Message(sender.toString(), content.toString(), timestamp as Long)
//                    )
//                }
//
//                item.containsKey("date") && item.containsKey("method") -> {
//                    // Menangani reminder
//                    val sender = item["sender"]
//                    val date = item["date"]
//                    val method = item["method"]
//                    val timestamp = item["timestamp"]
//                    val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(
//                        Date(timestamp as Long)
//                    )
//
//                    // Cek apakah tanggal berbeda dengan pesan sebelumnya
//                    if (messageDate != lastDate) {
//                        messages?.add(
//                            MessageDate(messageDate)
//                        )
//                        lastDate = messageDate
//                    }
//
//                    messages?.add(
//                        MessageReminder(sender.toString(), date.toString(), method.toString(), timestamp as Long)
//                    )
//                }
//            }
//        }
//    }