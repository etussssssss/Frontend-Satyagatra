package com.example.projekconsultant.adapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelesaiDetailReviewUser(
    val nomorPendaftaran:String,
    val pengalaman:String,
    val tag:String,
    val rateStar:Int,
): Parcelable