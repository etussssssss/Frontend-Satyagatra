package com.example.projekconsultant.admin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataRiwayatKonselor (
    val nomorPendaftaranUser:String = "",
    val namaKonselor:String = "",
    val tanggalSelesaiUser:String = "",
) : Parcelable