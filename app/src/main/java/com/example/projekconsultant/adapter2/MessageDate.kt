package com.example.projekconsultant.adapter2

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class MessageDate(
    val tanggal: String ,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tanggal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MessageDate> {
        override fun createFromParcel(parcel: Parcel): MessageDate {
            return MessageDate(parcel)
        }

        override fun newArray(size: Int): Array<MessageDate?> {
            return arrayOfNulls(size)
        }
    }
}