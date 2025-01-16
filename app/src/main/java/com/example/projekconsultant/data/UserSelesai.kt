package com.example.projekconsultant.data

import android.os.Parcelable
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserSelesai (
    val selesaidata:SelesaiData,
    val selesaidetailreviewuser:SelesaiDetailReviewUser? = null
) : Parcelable