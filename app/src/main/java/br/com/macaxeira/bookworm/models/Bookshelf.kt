package br.com.macaxeira.bookworm.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Bookshelf() : Parcelable {

    var id: Long = 0
    @SerializedName("selfLink")
    var link: String = ""
    var title: String = ""
    var description: String = ""
    @SerializedName("volumeCount")
    var booksCount: Int = 0

    constructor(parcel: Parcel): this()  {
        id = parcel.readLong()
        link = parcel.readString()
        title = parcel.readString()
        description = parcel.readString()
        booksCount = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeLong(id)
        parcel?.writeString(link)
        parcel?.writeString(title)
        parcel?.writeString(description)
        parcel?.writeInt(booksCount)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR = object : Parcelable.Creator<Bookshelf> {
            override fun createFromParcel(parcel: Parcel): Bookshelf = Bookshelf(parcel)

            override fun newArray(i: Int): Array<Bookshelf> = newArray(i)
        }
    }
}