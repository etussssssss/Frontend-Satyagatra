package com.example.projekconsultant.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.AdapterAdminChat
import com.example.projekconsultant.adapter.AdapterAdminDataChat
import com.example.projekconsultant.adapter2.AdapterAdminChatUser
import com.example.projekconsultant.adapter2.Message
import com.example.projekconsultant.adapter3sealedclass.ChatsUsers
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import androidx.appcompat.widget.SearchView



class ForumChatAdminFragment : Fragment() {
    private lateinit var db:FirebaseFirestore
    private var listenerRegistration: ListenerRegistration? = null // Menyimpan listener

    private lateinit var searchUser: SearchView
    private lateinit var loadingIndicator:ProgressBar
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: AdapterAdminChat
    private var dataAllChat:MutableList<AdapterAdminDataChat> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(noPendaftaran: ArrayList<ChatsUsers>) = ForumChatAdminFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("NO", noPendaftaran)
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_forum_chat_admin, container, false)
        val sharedDatas = ViewModelProvider(requireActivity())[SharedViewModelAdmin::class.java]

        db = FirebaseFirestore.getInstance()
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        searchUser = view.findViewById(R.id.searchView)

        recyclerView = view.findViewById(R.id.recyclerViewfc1)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Inisialisasi Adapter
        adapter = AdapterAdminChat(dataAllChat)
        recyclerView.adapter = adapter

        sharedDatas.dataChat.observe(viewLifecycleOwner){ updateDataChat ->
            // getMessages(updateDataChat)
            getMessagesNonNull(updateDataChat)
        }

        view.findViewById<ImageView>(R.id.backhomeadmin).setOnClickListener {
            // Ganti fragment dengan Home
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, HomeAdmin()).commit()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.home
        }

        searchName()

        return view
    }

    private fun searchName(){
        searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText?.lowercase() ?: ""

                // Filter data berdasarkan teks pencarian
                val filteredData = if (searchText.isNotEmpty()) {
                    dataAllChat.filter { it.pesananUser.namaUser.lowercase().contains(searchText) }
                } else {
                    dataAllChat
                }

                // Perbarui data adapter dengan hasil filter
                adapter.updateData(filteredData)
                return true
            }
        })
    }

    private fun getMessagesNonNull(datasChat: ArrayList<ChatsUsers>?) {
        loadingIndicator.visibility = View.VISIBLE // Tampilkan loading
        var completedRequests = 0
        val totalRequests = datasChat?.size ?: 0

        datasChat?.forEach { data ->
            // Cek apakah koleksi "messages" ada sebelum menambahkan listener
            val docRef = db.collection("chats").document(data.noRuid)

            // Cek apakah koleksi messages ada
            docRef.collection("messages").get().addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) { Log.d("AdminChat", "No messages found for ${data.noRuid}. No collection 'messages'.")
                        completedRequests++
                        if (completedRequests == totalRequests) {
                            loadingIndicator.visibility = View.GONE
                        }
                    } else {
                        // Jika ada koleksi messages, tambahkan listener
                        val listener = docRef.collection("messages").orderBy("timestamp").addSnapshotListener { snapshots, e ->
                                if (e != null) { Log.e("AdminChat", "Error listening to messages for ${data.noRuid}: ${e.message}")
                                    return@addSnapshotListener
                                }
                                // Perbarui data pesan
                                val messages = snapshots?.map { it.data } ?: emptyList()
                                val chatIndex = dataAllChat.indexOfFirst { it.pesananUser.noRuid == data.noRuid }

                                if (chatIndex != -1) {
                                    // Perbarui data yang sudah ada
                                    dataAllChat[chatIndex] = AdapterAdminDataChat(data, messages)
                                } else {
                                    // Tambahkan data baru jika belum ada
                                    dataAllChat.add(AdapterAdminDataChat(data, messages))
                                }
                                // Urutkan ulang berdasarkan timestamp
                                dataAllChat.sortByDescending { chat ->
                                    chat.semuaPesan.lastOrNull()?.get("timestamp") as? Long ?: 0L
                                }

                                adapter.notifyDataSetChanged()
                                completedRequests++
                                if (completedRequests == totalRequests) {
                                    loadingIndicator.visibility = View.GONE
                                }
                            }

                        // Simpan listener untuk nanti dilepas
                        listenerRegistration = listener
                    }
                }.addOnFailureListener { e ->
                    Log.e("AdminChat", "Error checking for messages collection for ${data.noRuid}: ${e.message}") }
        }

        // Jika tidak ada data untuk dimuat
        if (totalRequests == 0) {
            loadingIndicator.visibility = View.GONE
        }
    }

    //Bersihkan Data Jika Tidak Di Fragment ini
    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()  // Unregister listener saat view dihancurkan
    }

    //Referensi
    @SuppressLint("NotifyDataSetChanged")
    private fun getMessages(datasChat: ArrayList<ChatsUsers>?) {
        loadingIndicator.visibility = View.VISIBLE // Tampilkan loading
        var completedRequests = 0
        val totalRequests = datasChat?.size ?: 0

        datasChat?.forEach { data ->
            // Listener Real-Time Updates
            val listener = db.collection("chats").document(data.noRuid).collection("messages")
                .orderBy("timestamp").addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("AdminChat", "Error listening to messages for ${data.noRuid}: ${e.message}")
                        return@addSnapshotListener
                    }

                    // Perbarui data pesan
                    val messages = snapshots?.map { it.data } ?: emptyList()
                    val chatIndex = dataAllChat.indexOfFirst { it.pesananUser.noRuid == data.noRuid }

                    if (chatIndex != -1) {
                        // Perbarui data yang sudah ada
                        dataAllChat[chatIndex] = AdapterAdminDataChat(data, messages)
                    } else {
                        // Tambahkan data baru jika belum ada
                        dataAllChat.add(AdapterAdminDataChat(data, messages))
                    }

                    // Urutkan ulang berdasarkan timestamp
                    dataAllChat.sortByDescending { chat ->
                        chat.semuaPesan.lastOrNull()?.get("timestamp") as? Long ?: 0L
                    }

                    adapter.notifyDataSetChanged()
                    completedRequests++
                    if (completedRequests == totalRequests) {
                        loadingIndicator.visibility = View.GONE
                    }
                }

            // Simpan listener untuk nanti dilepas
            listenerRegistration = listener
        }

        // Jika tidak ada data untuk dimuat
        if (totalRequests == 0) {
            loadingIndicator.visibility = View.GONE
        }
    }
}









//private val chats = listOf(
//    mapOf("read" to false, "sender" to "user", "text" to "Untuk selebih nya mungkin saya bisa bisain buat konseling", "timestamp" to 1697065244000),
//    mapOf("read" to false, "sender" to "admin", "text" to "Oke, saya akan siapin dulu ya konselor nya untuk kamu", "timestamp" to 1697465245000),
//    mapOf("read" to false, "sender" to "user", "tanggalPesan" to "30 Desember 2024", "layanan" to "Via Telepon", "timestamp" to 1697055246000),
//    mapOf("read" to false, "sender" to "user", "text" to "Halo", "timestamp" to 1657065241000),
//    mapOf("read" to false, "sender" to "admin", "text" to "Halo bisa di bantu?", "timestamp" to 1697067242000),
//    mapOf("read" to false, "sender" to "user", "text" to "Kapan ya min saya pengen banget curhat, saya bisa nya hanya tanggal 20 januari", "timestamp" to 1697065243000),
//)
//
//private val chats2 = listOf(
//    mapOf("read" to false, "sender" to "user", "tanggalPesan" to "15 Januari 2025", "layanan" to "Via Chat", "timestamp" to 1697115246000)
//)

//    val datas = listOf(AdapterAdminDataChat(
//        ChatsUsers("Wilay",""), chats),
//        AdapterAdminDataChat(ChatsUsers("Bardul",""), chats2)
//    )






//@SuppressLint("NotifyDataSetChanged")
//private fun getMessages(datasChat: ArrayList<ChatsUsers>?) {
//    loadingIndicator.visibility = View.VISIBLE // Tampilkan loading
//    var completedRequests = 0
//    val totalRequests = datasChat?.size ?: 0
//
//    datasChat?.forEach { data ->
//        db.collection("chats").document(data.noPendaftaran).collection("messages").orderBy("timestamp")
//            .get().addOnSuccessListener { messagesSnapshot ->
//                val messages = messagesSnapshot.map { it.data } // Dapatkan data pesan
//                dataAllChat.add(AdapterAdminDataChat(data, messages))
//                adapter.notifyDataSetChanged()
//
//
//                completedRequests++
//                if (completedRequests == totalRequests) {
//                    loadingIndicator.visibility = View.GONE
//                }
//
//            }.addOnFailureListener { e ->
//                Log.e("AdminChat", "Error fetching messages for ${data.noPendaftaran}: ${e.message}")
//
//                completedRequests++
//                if (completedRequests == totalRequests) {
//                    loadingIndicator.visibility = View.GONE
//                }
//            }
//    }
//
//    // Jika tidak ada data untuk dimuat
//    if (totalRequests == 0) {
//        loadingIndicator.visibility = View.GONE
//    }
//}








//Data Tes
//  adapter = AdapterAdminChat(datas)


//private val chats = listOf(
//    mapOf("read" to true,"sender" to "user", "content" to "Untuk selebih nya mungkin saya bisa bisain buat konseling", "timestamp" to 1697065244000),
//    mapOf("read" to true,"sender" to "admin", "content" to "Oke, saya akan siapin dulu ya konselor nya untuk kamu", "timestamp" to 1697465245000),
//    mapOf("read" to true,"sender" to "user", "date" to "30 Desember 2024", "method" to "Via Telepon", "timestamp" to 1697055246000),
//    mapOf("read" to true,"sender" to "user", "content" to "Halo", "timestamp" to 1657065241000),
//    mapOf("read" to true,"sender" to "admin", "content" to "Halo bisa di bantu?", "timestamp" to 1697067242000),
//    mapOf("read" to true,"sender" to "user", "content" to "Kapan ya min saya pengen banget curhat, saya bisa nya hanya tanggal 20 januari", "timestamp" to 1697065243000),
//)
//private val chats2 = listOf(
//    mapOf("read" to true, "sender" to "user", "date" to "15 Januari 2025", "method" to "Via Chat", "timestamp" to 1697115246000)
//)


//val datas = listOf(AdapterAdminDataChat(
//    ChatsUsers("Wilay",""), chats),
//    AdapterAdminDataChat(ChatsUsers("Bardul",""), chats2))



//        mapOf("read" to true, "sender" to "user", "content" to "Apakah ada waktu untuk bicara?", "timestamp" to 1697125244000),
//        mapOf("read" to true, "sender" to "admin", "content" to "Tentu, saya akan cek jadwalnya.", "timestamp" to 1697525245000),


//        getLatestMessage(chats)
//fun getLatestMessage(chats: List<Map<String, Any>>): String {
//    val latestChat = chats.maxByOrNull { it["timestamp"] as Long }
//    var remindChat:String = if(butuhPersetujuan) "Butuh Persetujuan ${latestChat?.get("date")}"
//    else if(diSetujui) "Di Setujui ${latestChat?.get("date")}"
//    else TODO()
//
//    return latestChat?.get("content") as? String ?: "${remindChat}"
//}

//            if (nomorPendaftaran != null) {
//                listenerRegistration = db.collection("chats")
//                    .document(nomorPendaftaran!!)
//                    .collection("messages")
//                    .orderBy("timestamp")
//                    .addSnapshotListener { snapshot, e ->
//                        if (e != null) {
//                            Log.e("ChatFragment", "Listen failed.", e)
//                            return@addSnapshotListener
//                        }
//
//                        if (snapshot != null) {
//                            messages.clear()
//                            for (document in snapshot.documents) {
//                                val message = document.toObject(Message::class.java)
//                                if (message != null) {
//                                    messages.add(message)
//                                    dataChat.add(AdapterAdminDataChat("",""))
//                                }
//                            }
//                            adapter.notifyDataSetChanged()
//                            recyclerView.scrollToPosition(messages.size - 1)
//                        }
//                    }
//            }




//private val chats = listOf(
//    mapOf("read" to true,"sender" to "user", "content" to "Untuk selebih nya mungkin saya bisa bisain buat konseling", "timestamp" to 1697065244000),
//    mapOf("read" to true,"sender" to "admin", "content" to "Oke, saya akan siapin dulu ya konselor nya untuk kamu", "timestamp" to 1697465245000),
//    mapOf("read" to true,"sender" to "user", "date" to "30 Desember 2024", "method" to "Via Telepon", "timestamp" to 1697055246000),
//    mapOf("read" to true,"sender" to "user", "content" to "Halo", "timestamp" to 1657065241000),
//    mapOf("read" to true,"sender" to "admin", "content" to "Halo bisa di bantu?", "timestamp" to 1697067242000),
//    mapOf("read" to true,"sender" to "user", "content" to "Kapan ya min saya pengen banget curhat, saya bisa nya hanya tanggal 20 januari", "timestamp" to 1697065243000),
//)
//
//private val chats2 = listOf(
//    mapOf("read" to true, "sender" to "user", "content" to "Apakah ada waktu untuk bicara?", "timestamp" to 1697125244000),
//    mapOf("read" to true, "sender" to "admin", "content" to "Tentu, saya akan cek jadwalnya.", "timestamp" to 1697525245000),
//    mapOf("read" to true, "sender" to "user", "date" to "15 Januari 2025", "method" to "Via Chat", "timestamp" to 1697115246000)
//)
//
//
//private val datas = listOf(
//    AdapterAdminDataChat("Wily", getLatestMessage(chats), chats),
//    AdapterAdminDataChat("Bardul", getLatestMessage(chats2), chats2)
//)


//        dataChat.add(AdapterAdminDataChat("",""))
//        if (nomorPendaftaran != null) {
//            listenerRegistration = db.collection("chats")
//                .document(nomorPendaftaran!!)
//                .collection("messages")
//                .orderBy("timestamp")
//                .addSnapshotListener { snapshot, e ->
//                    if (e != null) {
//                        Log.e("ChatFragment", "Listen failed.", e)
//                        return@addSnapshotListener
//                    }
//
//                    if (snapshot != null) {
//                        messages.clear()
//                        for (document in snapshot.documents) {
//                            val message = document.toObject(Message::class.java)
//                            if (message != null) {
//                                messages.add(message)
//                            }
//                        }
//                        adapter.notifyDataSetChanged()
//                        recyclerView.scrollToPosition(messages.size - 1)
//                    }
//                }
//        }



//private fun getMessagesForUser(noPendaftaran: String) {
//    db.collection("chats").document(noPendaftaran).collection("messages").orderBy("timestamp")
//        .get().addOnSuccessListener { messagesSnapshot ->
//            val messages = messagesSnapshot.map { it.data } // Dapatkan data pesan
//            AdapterAdminDataChat(ChatsUsers("Wilay",""), messages)
////                processMessages(noPendaftaran, messages) // Proses atau tampilkan pesan
//
//        }.addOnFailureListener { e ->
//            Log.e("AdminChat", "Error fetching messages for $noPendaftaran: ${e.message}")
//        }
//}
//
//private fun processMessages(noPendaftaran: String, messages: List<Map<String, Any>>) {
//    // Contoh: Log pesan
//    Log.d("AdminChat", "Messages for $noPendaftaran:")
//    for (message in messages) {
//        val sender = message["sender"]
//        val text = message["text"]
//        val timestamp = message["timestamp"]
//
//        Log.d("AdminChat", "Sender: $sender, Text: $text, Timestamp: $timestamp")
//    }
//
//    // Tambahkan logika untuk menampilkan pesan di RecyclerView admin
//}