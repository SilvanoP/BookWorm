package br.com.macaxeira.bookworm.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import br.com.macaxeira.bookworm.BuildConfig

import br.com.macaxeira.bookworm.R
import br.com.macaxeira.bookworm.adapters.BooksListAdapter
import br.com.macaxeira.bookworm.models.SearchBookBaseResponse
import br.com.macaxeira.bookworm.models.Book
import br.com.macaxeira.bookworm.network.GoogleBooksClient
import br.com.macaxeira.bookworm.services.ServiceGenerator
import br.com.macaxeira.bookworm.utils.Constants
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_books.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BooksActivity : AppCompatActivity() {

    private val BOOKS_STATE_KEY = "BOOKS_STATE_KEY"

    var mBooks: MutableList<Book> = mutableListOf()
    var mUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        title = getText(R.string.my_books)

        booksListRecyclerView.layoutManager = LinearLayoutManager(this)

        mUserId = intent.getStringExtra(Constants.INTENT_USER_ID_EXTRA)

        if (savedInstanceState!= null) {
            mBooks = savedInstanceState.getParcelableArrayList(BOOKS_STATE_KEY)
        }

        if (mBooks.size > 0) {
            updateUi()
        } else {
            searchBooks()
        }
    }

    private fun searchBooks() {
        val userId = mUserId
        if (userId != null) {
            val client = ServiceGenerator.createService(GoogleBooksClient::class.java)

            //val call = client.searchBooks("lord+rings", BuildConfig.GOOGLE_API_KEY)
            val call = client.getBooksFromBookshelf(userId, 3, BuildConfig.GOOGLE_API_KEY)
            Log.i("HELLOOOOOOOOOOOOOOOOOO", "Call ${call.request().url()}")
            val callback = object : Callback<SearchBookBaseResponse> {
                override fun onResponse(call: Call<SearchBookBaseResponse>?, responseSearchBook: Response<SearchBookBaseResponse>?) {
                    val baseResponse = responseSearchBook?.body()
                    if (baseResponse != null) {
                        mBooks.clear()
                        for (bookList in baseResponse.books) {
                            mBooks.add(bookList.book)
                        }
                        updateUi()
                    } else {
                        Toast.makeText(this@BooksActivity, R.string.toast_empty_books, Toast.LENGTH_SHORT).show()
                        Log.e("BooksActivity", "Error ${responseSearchBook?.code()}: ${Gson().toJson(responseSearchBook)}")
                    }
                }

                override fun onFailure(call: Call<SearchBookBaseResponse>?, t: Throwable?) {
                    t?.printStackTrace()
                }
            }

            call.enqueue(callback)
        }
    }

    fun updateUi() {
        booksListRecyclerView.adapter = BooksListAdapter(this, mBooks)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelableArrayList(BOOKS_STATE_KEY, ArrayList<Book>(mBooks))
    }
}
