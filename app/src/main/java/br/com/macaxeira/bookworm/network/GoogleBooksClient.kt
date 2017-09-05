package br.com.macaxeira.bookworm.network

import br.com.macaxeira.bookworm.models.BaseResponse
import br.com.macaxeira.bookworm.models.BookListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksClient {

    @GET("books/v1/volumes")
    fun searchBooks(
            @Query("q", encoded = true) search: String,
            @Query("key") apiKey: String
    ) : Call<BaseResponse>

    @GET("books/v1/volumes/{book_id}")
    fun getBookResponse(
            @Query("key") apiKey: String
    ) : Call<BookListResponse>
}