package com.example.projekconsultant.adapter

import android.os.Parcelable
import com.example.projekconsultant.admin.DataPendaftaran
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelesaiData(
    val nomorPendaftaran: String,
    val name: String = "",
    val topik:String,
    val tanggal:String,
    val offOrOn:String,
    val isiCurhatan: String,
    val konselor:String,
) : Parcelable