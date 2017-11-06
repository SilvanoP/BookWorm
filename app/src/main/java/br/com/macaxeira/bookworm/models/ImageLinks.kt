package br.com.macaxeira.bookworm.models

import android.os.Parcel
import android.os.Parcelable

class ImageLinks(): Parcelable {
    var smallThumbnail: String? = null
    var thumbnail: String? = null
    var small: String? = null
    var medium: String? = null
    var large: String? = null
    var extraLarge: String? = null

    constructor(parcel: Parcel?) : this() {
        smallThumbnail = parcel?.readString()
        thumbnail = parcel?.readString()
        small = parcel?.readString()
        medium = parcel?.readString()
        large = parcel?.readString()
        extraLarge = parcel?.readString()
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        smallThumbnail = parcel?.readString()
        thumbnail = parcel?.readString()
        small = parcel?.readString()
        medium = parcel?.readString()
        large = parcel?.readString()
        extraLarge = parcel?.readString()
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR = object: Parcelable.Creator<ImageLinks> {
            override fun createFromParcel(parcel: Parcel?): ImageLinks {
                if (parcel == null) {
                    return ImageLinks()
                }
                return ImageLinks(parcel)
            }

            override fun newArray(i: Int): Array<ImageLinks> = newArray(i)
        }
    }
}