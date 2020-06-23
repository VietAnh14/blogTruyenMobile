package com.vianh.blogtruyen.data.model

import android.os.Parcel
import android.os.Parcelable

data class Manga(
    val imageUrl: String,
    val link: String,
    var title: String,
    var uploadTitle: String,
    var description: String = "Still update",
    val id: Int = link.split('/')[1].toInt()): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(link)
        parcel.writeString(title)
        parcel.writeString(uploadTitle)
        parcel.writeString(description)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Manga> {
        override fun createFromParcel(parcel: Parcel): Manga {
            return Manga(parcel)
        }

        override fun newArray(size: Int): Array<Manga?> {
            return arrayOfNulls(size)
        }
    }
}