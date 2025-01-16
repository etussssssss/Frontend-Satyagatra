package com.example.projekconsultant.adapter

import android.os.Parcelable
import com.example.projekconsultant.adapter3sealedclass.ChatsUsers
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class AdapterAdminDataChat(
    val pesananUser:ChatsUsers,
    val semuaPesan: List<Map<String, Any>>
)