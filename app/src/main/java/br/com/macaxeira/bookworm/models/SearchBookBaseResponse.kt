package br.com.macaxeira.bookworm.models

import com.google.gson.annotations.SerializedName

class SearchBookBaseResponse {

    @SerializedName("items")
    var books: List<BookListResponse> = ArrayList()
}