package br.com.macaxeira.bookworm.models

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class Book(): Parcelable {

    var title: String = ""
    var authors: List<String> = ArrayList()
    var publisher: String = ""
    var description: String = ""
    var averageRating: Double = 0.0
    var ratingsCount: Int = 0
    var imageLinks: ImageLinks? = null
    var language: String = ""
    var previewLink: String = ""

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        parcel.readStringList(authors)
        publisher = parcel.readString()
        description = parcel.readString()
        averageRating = parcel.readDouble()
        ratingsCount = parcel.readInt()
        imageLinks = parcel.readTypedObject(ImageLinks.CREATOR)
        language = parcel.readString()
        previewLink = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeString(title)
        parcel?.writeStringList(authors)
        parcel?.writeString(publisher)
        parcel?.writeString(description)
        parcel?.writeDouble(averageRating)
        parcel?.writeInt(ratingsCount)
        parcel?.writeTypedObject(imageLinks, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
        parcel?.writeString(language)
        parcel?.writeString(previewLink)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR = object : Parcelable.Creator<Book> {
            override fun createFromParcel(parcel: Parcel?): Book {
                if (parcel == null){
                    return Book()
                }
                return Book(parcel)
            }

            override fun newArray(i: Int): Array<Book> = newArray(i)
        }
    }
}