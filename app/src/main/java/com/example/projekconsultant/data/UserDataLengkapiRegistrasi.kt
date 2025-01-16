package com.example.projekconsultant.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDataLengkapiRegistrasi (
    val type:String = "",
    val nama:String = "",
    val gender:String = "",
    val domisili:String = "",
    val nomortelepon:String = "",
    val tanggalLahir:String = "",
    val status:String = "",
    val selectTopik:String = "",
) : Parcelable