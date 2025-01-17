package com.example.projekconsultant.admin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataRiwayatSelesaiAdmin (
    val nomorPendaftaranUser:String = "",
    val namaUser:String = "",
    val namaKonselor:String = "",
    val topikKonsultasi:String = "",
    val tanggalSelesaiUser:String = "",
    val layananOnlineOrOfflineUser:String = "",
    val layananKonsultasi:String = "Janji Tatap Muka",
    val curhatanUser:String = "",
    val typeUser:String = "",
    val umur:Int = 0,
    val gender:String = "",
    val profesi:String = "",
    val fakultas:String = "",
    val status:String = "",
    val domisili:String = "",
) : Parcelable