package com.example.projekconsultant.adapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdminDataPendaftaranUser(
    val offon: String,
    val nama: String,
    val topik: String
) : Parcelable