package com.example.projekconsultant.admin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataPendaftaran (
    val uuid:String = "",
    val nomorPendaftaran:String = "",

    val nomorTeleponUser:String = "",
    val nama:String = "",
    val umur:String = "",
    val gender:String = "",
    val type:String = "",

    val profesi:String = "",
    val fakultas:String = "",


    val PilihTanggal:String = "",
    val PilihLayananKonsultasi:String = "",
    val PilihTopikKonsultasi:String = "",
    val IsiCurhatan:String = "",

    var ButuhPersetujuan:Boolean? = false,
    var DiSetujui:Boolean? = false,

    var onoff:String = "",
    var konselor:String = ""
) : Parcelable