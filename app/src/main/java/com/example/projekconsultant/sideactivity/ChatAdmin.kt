package com.example.projekconsultant.sideactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter2.AdapterUserChatAdmin
import com.example.projekconsultant.adapter2.Message
import com.example.projekconsultant.adapter2.MessageDate
import com.example.projekconsultant.adapter2.MessageReminder
import com.example.projekconsultant.methodsNService.NetworkChangeReceiver
import com.example.projekconsultant.methodsNService.NoInternet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class ChatAdmin : BaseActivity() {
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    private var lastDate: String? = null
    private lateinit var adapter: AdapterUserChatAdmin
    private var firestoreListener: ListenerRegistration? = null
    private lateinit var recyclerView: RecyclerView
    private val messages: MutableList<Any> = mutableListOf()

    private lateinit var textMsg: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var db: FirebaseFirestore

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_admin)

        initViews()
        initFirestore()
        setupRecyclerView()
        setupListeners()

        // Inisialisasi NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                val intent = Intent(this, NoInternet::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewChat)
        textMsg = findViewById(R.id.inputMessage)
        btnSend = findViewById(R.id.sendCht)
    }

    private fun initFirestore() {
        db = FirebaseFirestore.getInstance()
        val noPendaftaran = intent.getStringExtra("RUID") ?: return
        listenToMessages(noPendaftaran)
    }

    private fun setupRecyclerView() {
        adapter = AdapterUserChatAdmin(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        })
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            val message = textMsg.text.toString().trim()
            if (message.isNotEmpty()) {
                val noPendaftaran = intent.getStringExtra("RUID") ?: return@setOnClickListener
                sendMessage(noPendaftaran, "user", message)
                textMsg.text.clear()
            }
        }

        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            finish()
        }

        textMsg.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) scrollToLastMessageOnKeyboard()
        }
    }

    private fun scrollToLastMessageOnKeyboard() {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                recyclerView.rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = recyclerView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    recyclerView.post {
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
                }
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listenToMessages(chatId: String) {
        val messageRef = db.collection("chats").document(chatId).collection("messages")
        firestoreListener = messageRef.orderBy("timestamp").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Chat", "Listen failed.", e)
                return@addSnapshotListener
            }

            snapshot?.let {
                messages.clear()
                lastDate = ""
                chat(it.documents.mapNotNull { doc -> doc.data })
                recyclerView.post {
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                    // recyclerView.scrollToPosition(messages.size - 1)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendMessage(chatId: String, sender: String, messageContent: String) {
        val message = Message(
            sender = sender,
            text = messageContent,
            timestamp = System.currentTimeMillis(),
            read = false
        )

        // Tampilkan pesan langsung di RecyclerView
        messages.add(message)
        Log.d("SEND", "${message}")
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
        adapter.notifyDataSetChanged()

        db.collection("chats").document(chatId).collection("messages")
            .add(message).addOnSuccessListener {
                Log.d("Chat", "Message sent successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Chat", "Failed to send message: ${e.message}")
            }
    }

    private fun chat(dataList: List<Map<String, Any>>) {
        dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
            when {
                item.containsKey("sender") && item.containsKey("text") -> {
                    val sender = item["sender"]
                    val content = item["text"]
                    val timestamp = item["timestamp"] as Long
                    val read = item["read"] as Boolean

                    sendMsgDate(timestamp)
                    messages.add(Message(sender.toString(), content.toString(), timestamp, read))
                }
                item.containsKey("tanggalPesan") && item.containsKey("layanan") -> {
                    val sender = item["sender"]
                    val date = item["tanggalPesan"]
                    val method = item["layanan"]
                    val timestamp = item["timestamp"] as Long
                    val read = item["read"] as Boolean
                    val statusReminder = item["statusReminder"]

                    sendMsgDate(timestamp)
                    messages.add(
                        MessageReminder(
                            sender.toString(),
                            date.toString(),
                            method.toString(),
                            timestamp,
                            read,
                            statusReminder.toString()
                        )
                    )
                }
            }
        }
    }

    private fun sendMsgDate(timestamp: Long) {
        val messageDate = dateFormatter.format(Date(timestamp))
        if (messageDate != lastDate) {
            messages.add(MessageDate(messageDate))
            lastDate = messageDate
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
        messages.clear()
        recyclerView.adapter = null
    }

    override fun onResume() {
        super.onResume()
        // Pastikan networkChangeReceiver sudah diinisialisasi
        networkChangeReceiver?.let { receiver ->
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(receiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        // Pastikan networkChangeReceiver sudah diinisialisasi
        networkChangeReceiver?.let { receiver ->
            unregisterReceiver(receiver)
        }
    }
}








//dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
//    val timestamp = item["timestamp"] as? Long ?: return@forEach
//    sendMsgDate(timestamp)
//
//    when {
//        item.containsKey("sender") && item.containsKey("text") -> {
//            messages?.add(
//                Message(
//                    sender = item["sender"].toString(),
//                    text = item["text"].toString(),
//                    timestamp = timestamp
//                )
//            )
//        }
//
//        item.containsKey("tanggalPesan") && item.containsKey("layanan") -> {
//            messages?.add(
//                MessageReminder(
//                    sender = item["sender"].toString(),
//                    tanggalPesan = item["tanggalPesan"].toString(),
//                    layanan = item["layanan"].toString(),
//                    timestamp = timestamp
//                )
//            )
//        }
//    }
//}


//firestoreListener = messageRef?.orderBy("timestamp")?.addSnapshotListener { snapshot, e ->
//    if (e != null) {
//        Log.w("Chat", "Listen failed.", e)
//        return@addSnapshotListener
//    }
//
//    if (snapshot != null && snapshot.documents.isNotEmpty()) {
//        Log.d("Firestore Chat", "Data changed: ${snapshot.documents.size}")
//        messages?.clear() // Kosongkan daftar pesan sebelum diisi ulang
//        chat(snapshot.documents.mapNotNull { it.data }) // Ubah data Firestore ke format yang diinginkan
//        adapter.notifyDataSetChanged()
//    }
//}


//        btnSend.setOnClickListener {
//            val message = textMsg.text.toString().trim()
//            if (message.isEmpty()) {
//                textMsg.text.clear()
//                Log.d("TEXT KOSONG", "${textMsg.text}")
//            } else {
//                if (noPendaftaran != null) {
//                    sendMessage(noPendaftaran, "user", textMsg.text.toString() )
//
//                    adapter.notifyDataSetChanged()
//                }
//
//                adapter.notifyItemInserted(messages!!.size - 1)
//                recyclerView.scrollToPosition(adapter.itemCount - 1)
//                textMsg.text.clear() // Mengosongkan EditText setelah mengirim
//            }
//        }


//    private fun chat(datas: Any?) {
//        Log.d("CHATTTTTTT", "Data : ${datas}")
//        val dataList = datas as? List<Map<String, Any>> ?: emptyList() // Cek tipe dan beri default empty list
//
//        dataList.sortedBy { it["timestamp"] as? Long ?: 0L }.forEach { item ->
//            when {
//                item.containsKey("sender") && item.containsKey("text") -> {
//                    // Menangani pesan
//                    val sender = item["sender"]
//                    val content = item["text"]
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
//                item.containsKey("tanggalPesan") && item.containsKey("layanan") -> {
//                    // Menangani reminder
//                    val sender = item["sender"]
//                    val date = item["tanggalPesan"]
//                    val method = item["layanan"]
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





//class ChatAdmin : BaseActivity() {
//
//    private lateinit var networkChangeReceiver: NetworkChangeReceiver
//    private lateinit var adapter: AdapterUserChatAdmin
//    private var messages: MutableList<Any> = mutableListOf() // Menyimpan pesan
//
//    override fun onResume() {
//        super.onResume()
//        // Register BroadcastReceiver
//        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(networkChangeReceiver, intentFilter)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Unregister BroadcastReceiver
//        unregisterReceiver(networkChangeReceiver)
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chat_admin)
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChat)
//
//        // Inisialisasi ViewModel dan mengambil noPendaftaran
//        val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
//        val noPendaftaran = sharedViewModel.riwayatNoPendaftaran.value
//        val db = FirebaseFirestore.getInstance()
//        val messageRef = noPendaftaran?.let { db.collection("chats").document(it).collection("messages") }
//
//        // Mendengarkan perubahan data pesan
//        messageRef?.orderBy("timestamp")?.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.w("Chat", "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            val messageList = mutableListOf<Message>()
//            snapshot?.forEach { document ->
//                val sender = document.getString("sender") ?: ""
//                val content = document.getString("content") ?: ""
//                val timestamp = document.getTimestamp("timestamp")?.toDate()?.time ?: 0L
//
//                messageList.add(Message(sender, content, timestamp))
//            }
//
//            // Update adapter dengan data baru
//            adapter.submitList(messageList)
//            adapter.notifyDataSetChanged()
//        }
//
//        // Inisialisasi RecyclerView dan Adapter
//        adapter = AdapterUserChatAdmin(messages)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//        // Inisialisasi NetworkChangeReceiver
//        networkChangeReceiver = NetworkChangeReceiver { isConnected ->
//            if (!isConnected) {
//                val intent = Intent(this, NoInternet::class.java)
//                startActivity(intent)
//            }
//        }
//
//        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
//            finish()
//        }
//    }
//
//    // Fungsi untuk mengelola data chat
//    private fun chat(datas: Any?) {
//        var lastDate: String? = null
//        val dataList = datas as? List<Map<String, Any>> ?: emptyList()
//
//        dataList.sortedBy { it["timestamp"] as? Long ?: 0L }?.forEach { item ->
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
//                        messages.add(MessageDate(messageDate))
//                        lastDate = messageDate
//                    }
//
//                    messages.add(Message(sender.toString(), content.toString(), timestamp as Long))
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
//                        messages.add(MessageDate(messageDate))
//                        lastDate = messageDate
//                    }
//
//                    messages.add(MessageReminder(sender.toString(), date.toString(), method.toString(), timestamp as Long))
//                }
//            }
//        }
//    }
//}



//private val data = listOf(
//            mapOf("sender" to "user", "content" to "Untuk selebih nya mungkin saya bisa bisain buat konseling", "timestamp" to 1697065244000),
//            mapOf("sender" to "admin", "content" to "Oke, saya akan siapin dulu ya konselor nya untuk kamu", "timestamp" to 1697465245000),
//            mapOf("sender" to "user", "date" to "30 Desember 2024", "method" to "Via Telepon", "timestamp" to 1697055246000),
//            mapOf("sender" to "user", "content" to "Halo", "timestamp" to 1657065241000),
//            mapOf("sender" to "admin", "content" to "Halo bisa di bantu?", "timestamp" to 1697067242000),
//            mapOf("sender" to "user", "content" to "Kapan ya min saya pengen banget curhat, saya bisa nya hanya tanggal 20 januari", "timestamp" to 1697065243000),
//    )




//        val data = listOf(
//
//        )
//
//        messages = mutableListOf(
//
//            Message("user","Halo",1697065241000),
//            Message("admin","Halo bisa di bantu?",1697065242000),
//            Message("user","Kapan ya min saya pengen banget curhat, saya bisa nya hanya tanggal 20 januari",1697065243000),
//            Message("user","Untuk selebih nya mungkin saya bisa bisain buat konseling",1697065244000),
//            Message("admin","Oke, saya akan siapin dulu ya konselor nya untuk kamu",1697065245000),
//
//        )
//
//        messages2 = mutableListOf(
//            MessageReminder("30 Desember 2024", "Via Telepon", 1697065246000)
//        )