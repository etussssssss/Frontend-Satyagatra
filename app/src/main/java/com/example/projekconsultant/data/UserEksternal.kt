package com.example.projekconsultant.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEksternal (
    val pekerjaan:String = "",
) : Parcelable