package com.example.projekconsultant.admin

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter2.AdapterAdminChatUser
import com.example.projekconsultant.adapter2.Message
import com.example.projekconsultant.adapter2.MessageDate
import com.example.projekconsultant.adapter2.MessageReminder
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ForumChatAdmin : BaseActivity() {
    private lateinit var adapter: AdapterAdminChatUser
    private var messages: MutableList<Any> = mutableListOf()
    private lateinit var recyclerView:RecyclerView
    private lateinit var textMsg:EditText
    private lateinit var btnSend: ImageButton

    private lateinit var namaUser: TextView
    private lateinit var db:FirebaseFirestore

    private var noPendaftaran: String? = ""
    private var lastDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_chat_admin)

        db = FirebaseFirestore.getInstance()
        namaUser = findViewById(R.id.userName)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChat)
        btnSend = findViewById(R.id.sendCht)
        textMsg = findViewById(R.id.inputMessage)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val messagesText = intent.getParcelableArrayListExtra<Message>("TEXT")
        val messagesRemind = intent.getParcelableArrayListExtra<MessageReminder>("REMINDER")
        val messagesDate = intent.getParcelableArrayListExtra<MessageDate>("DATE")
        noPendaftaran = intent.getStringExtra("NOPENDAFTARAN")

        // Tambahkan data ke messages
        val allMessages = mutableListOf<Any>()
        messagesDate?.let { allMessages.addAll(it) }
        messagesText?.let { allMessages.addAll(it) }
        messagesRemind?.let { allMessages.addAll(it) }
        
        chat(allMessages)
        adapter = AdapterAdminChatUser(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.post {
            recyclerView.scrollToPosition(adapter.itemCount - 1) // Scroll ke posisi terakhir
        }

        btnSend.setOnClickListener {
            val message = textMsg.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(noPendaftaran ?: "", "admin", message)

                messages.add(Message("admin", message, System.currentTimeMillis(), false))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                textMsg.text.clear()
                adapter.notifyDataSetChanged()
            }
        }


        namaUser.text = intent.getStringExtra("NAMAUSER")
        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            finish()
        }

        textMsg.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val rect = Rect()
                        recyclerView.rootView.getWindowVisibleDisplayFrame(rect)
                        val screenHeight = recyclerView.rootView.height
                        val keypadHeight = screenHeight - rect.bottom

                        if (keypadHeight > screenHeight * 0.15) { // Keyboard muncul
                            recyclerView.post {
                                recyclerView.scrollToPosition(adapter.itemCount - 1) // Scroll ke pesan terakhir
                            }
                        }

                        // Hapus listener untuk menghindari pemanggilan berulang
                        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    fun sendMessage(chatId: String, sender: String, messageContent: String) {
        val message = Message(
            sender = sender,
            text = messageContent,
            timestamp = System.currentTimeMillis(),
            read = false
        )

        db.collection("chats").document(chatId).collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("Chat", "Message sent successfully!")
            }.addOnFailureListener { e ->
                Log.e("Chat", "Failed to send message: ${e.message}")
            }
    }

    private fun chat(allMessages: MutableList<Any>) {
        allMessages.sortedBy { item ->
            when (item) {
                is Message -> item.timestamp
                is MessageReminder -> item.timestamp
                else -> 0L
            }
        }.forEach { item ->
            when (item) {
                is Message -> {
                    Log.d("Chat", item.toString())
                    sendMsgDate(item.timestamp)
                    messages.add(item)
                }
                is MessageReminder -> {
                    Log.d("Chat", item.toString())
                    sendMsgDate(item.timestamp)
                    messages.add(item)
                }
            }
        }
    }

    private fun sendMsgDate(timestamp : Long){
        val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(
            Date(timestamp as Long)
        )

        if (messageDate != lastDate) {
            messages.add(
                MessageDate(messageDate)
            )
            lastDate = messageDate
        }
    }
}























//private fun chat(datas: Any?) {
//    val dataList = datas as? List<Map<String, Any>> ?: emptyList() // Cek tipe dan beri default empty list
//
//    dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
//        when {
//            item.containsKey("sender") && item.containsKey("text") -> {
//                // Menangani pesan
//                val sender = item["sender"]
//                val content = item["text"]
//                val timestamp = item["timestamp"]
//                val read = item["read"]
//                sendMsgDate(timestamp as Long)
//
//                messages?.add(
//                    Message(sender.toString(), content.toString(), timestamp as Long, read as Boolean)
//                )
//            }
//
//            item.containsKey("tanggalPesan") && item.containsKey("layanan") -> {
//                // Menangani reminder
//                val sender = item["sender"]
//                val date = item["tanggalPesan"]
//                val method = item["layanan"]
//                val timestamp = item["timestamp"]
//                val read = item["read"]
//
//                sendMsgDate(timestamp as Long)
//
//                messages?.add(
//                    MessageReminder(sender.toString(), date.toString(), method.toString(), timestamp as Long, read as Boolean)
//                )
//            }
//        }
//    }
//}




//    private fun chat(datas: Any?) {
//        val dataList = datas as? List<Map<String, Any>> ?: emptyList() // Cek tipe dan beri default empty list
//
//        dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
//            when {
//                item.containsKey("sender") && item.containsKey("text") -> {
//                    // Menangani pesan
//                    val sender = item["sender"]
//                    val content = item["text"]
//                    val timestamp = item["timestamp"]
//                    sendMsgDate(timestamp as Long)
//
//                    messages?.add(
//                        Message(sender.toString(), content.toString(), timestamp as Long)
//                    )
//                }
//
//                item.containsKey("tanggalPesan") && item.containsKey("layanan") -> {
//                    // Menangani reminder
//                    val sender = item["sender"]
//                    val date = item["tanggalPesan"]
//                    val method = item["layanan"]
//                    val timestamp = item["timestamp"]
//                    sendMsgDate(timestamp as Long)
//
//                    messages?.add(
//                        MessageReminder(sender.toString(), date.toString(), method.toString(), timestamp as Long)
//                    )
//                }
//            }
//        }
//    }



//private fun chat(datas: MutableList<Any>?) {
//    val tempMessages = mutableListOf<Any>() // List sementara
//
//    datas?.forEach { item ->
//        when (item) {
//            is Message -> {
//                sendMsgDate(item.timestamp)
//                tempMessages.add(item)
//            }
//            is MessageReminder -> {
//                sendMsgDate(item.timestamp)
//                tempMessages.add(item)
//            }
//        }
//    }
//
//    messages?.addAll(tempMessages) // Tambahkan elemen setelah iterasi selesai
//}


//        val messagesText = this.intent.getSerializableExtra("CHATS") as? ArrayList<Any>  // Mengambil data Serial

//val messageDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(
//    Date(timestamp as Long)
//)
//
//// Cek apakah tanggal berbeda dengan pesan sebelumnya
//if (messageDate != lastDate) {
//    messages?.add(
//        MessageDate(messageDate)
//    )
//    lastDate = messageDate
//}

//private val data = listOf(
//    mapOf("sender" to "user", "content" to "Untuk selebih nya mungkin saya bisa bisain buat konseling", "timestamp" to 1697065244000),
//    mapOf("sender" to "admin", "content" to "Oke, saya akan siapin dulu ya konselor nya untuk kamu", "timestamp" to 1697465245000),
//    mapOf("sender" to "user", "date" to "30 Desember 2024", "method" to "Via Telepon", "timestamp" to 1697055246000),
//    mapOf("sender" to "user", "content" to "Halo", "timestamp" to 1657065241000),
//    mapOf("sender" to "admin", "content" to "Halo bisa di bantu?", "timestamp" to 1697067242000),
//    mapOf("sender" to "user", "content" to "Kapan ya min saya pengen banget curhat, saya bisa nya hanya tanggal 20 januari", "timestamp" to 1697065243000),
//)


//            if(textMsg.text.isNotEmpty()){
//                recyclerView.post {
//                    recyclerView.scrollToPosition(adapter.itemCount - 1) // Scroll ke posisi terakhir
//                }
//                Log.d("MASUK", "${textMsg.text}")
//                textMsg.text.clear()
//            }


//        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
//            val rect = Rect()
//            recyclerView.rootView.getWindowVisibleDisplayFrame(rect)
//
//            val screenHeight = recyclerView.rootView.height
//            val keypadHeight = screenHeight - rect.bottom
//
//            // Jika keyboard muncul (keyboardHeight lebih besar dari 100dp, misalnya)
//            if (keypadHeight > screenHeight * 0.15) {
//                recyclerView.post {
//                    recyclerView.scrollToPosition(adapter.itemCount - 1) // Scroll ke posisi terakhir
//                }
//            }
//        }



//        textMsg.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {}
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                recyclerView.post {
//                    recyclerView.scrollToPosition(adapter.itemCount - 1) // Scroll ke pesan terakhir
//                }
//            }
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })