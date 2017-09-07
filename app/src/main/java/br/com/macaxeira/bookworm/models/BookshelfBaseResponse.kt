package br.com.macaxeira.bookworm.models

import com.google.gson.annotations.SerializedName

class BookshelfBaseResponse {

    @SerializedName("items")
    var bookshelves: List<Bookshelf> = ArrayList()
}