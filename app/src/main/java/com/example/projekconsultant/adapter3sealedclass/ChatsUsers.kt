package com.example.projekconsultant.adapter3sealedclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatsUsers(
    val namaUser:String,
    //val noPendaftaran:String,
    val noRuid:String,
    val butuhPersetujuan: Boolean? = false,
    val diSetujui:Boolean? = false,
) : Parcelable