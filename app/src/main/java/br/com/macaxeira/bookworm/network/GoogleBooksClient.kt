package br.com.macaxeira.bookworm.network

import br.com.macaxeira.bookworm.models.SearchBookBaseResponse
import br.com.macaxeira.bookworm.models.BookListResponse
import br.com.macaxeira.bookworm.models.BookshelfBaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksClient {

    @GET("books/v1/volumes")
    fun searchBooks(
            @Query("q", encoded = true) search: String,
            @Query("key") apiKey: String
    ) : Call<SearchBookBaseResponse>

    @GET("books/v1/volumes/{book_id}")
    fun getBookResponse(
            @Path("book_id") bookId: String,
            @Query("key") apiKey: String
    ) : Call<BookListResponse>

    @GET("books/v1/users/{user_id}/bookshelves")
    fun getBookshelfs(
            @Path("user_id") userId: String,
            @Query("key") apiKey: String
    ) : Call<BookshelfBaseResponse>

    @GET("books/v1/users/{user_id}/bookshelves/{shelf_id}/volumes")
    fun getBooksFromBookshelf(
            @Path("user_id") userId: String,
            @Path("shelf_id") shelfId: Long,
            @Query("key") apiKey: String
    ) : Call<SearchBookBaseResponse>
}