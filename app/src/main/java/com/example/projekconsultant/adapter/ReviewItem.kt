package com.example.projekconsultant.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewItem(
    val nama: String,
    val rateStar: Int,
    val pengalaman:String,
    val tag: List<String>
) : Parcelable











//data class ReviewItem (
//    val nama:String = "",
//    val rate:Int = 0,
//    val tag:List<String>,
////    val tag:List<String>,
//
//)