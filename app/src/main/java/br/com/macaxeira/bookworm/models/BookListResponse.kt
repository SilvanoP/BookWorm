package br.com.macaxeira.bookworm.models

import com.google.gson.annotations.SerializedName

class BookListResponse {

    @SerializedName("selfLink")
    var link: String = ""
    @SerializedName("volumeInfo")
    var book: Book = Book()
}