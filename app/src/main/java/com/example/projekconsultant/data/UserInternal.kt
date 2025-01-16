package com.example.projekconsultant.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInternal (
    val roleinternal:String = "",
    val fakultas:String = "",
    var bagiantendik:String = "none",
) : Parcelable