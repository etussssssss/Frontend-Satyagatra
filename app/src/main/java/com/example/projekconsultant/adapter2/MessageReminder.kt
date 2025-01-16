package com.example.projekconsultant.adapter2

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class MessageReminder(
    val sender: String = "",
    val tanggalPesan:String,
    val layanan:String,
    val timestamp: Long,
    val read:Boolean,
    val statusReminder:String,
) : Parcelable