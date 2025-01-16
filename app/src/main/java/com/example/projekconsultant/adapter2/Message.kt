package com.example.projekconsultant.adapter2

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Message(
    val sender: String = "",
    val text: String = "",
    val timestamp: Long,
    val read:Boolean,
) : Parcelable
