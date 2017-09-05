package br.com.macaxeira.bookworm.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import br.com.macaxeira.bookworm.BuildConfig

import br.com.macaxeira.bookworm.R
import br.com.macaxeira.bookworm.adapters.BooksListAdapter
import br.com.macaxeira.bookworm.models.BaseResponse
import br.com.macaxeira.bookworm.models.Book
import br.com.macaxeira.bookworm.network.GoogleBooksClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_books.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BooksActivity : AppCompatActivity() {

    val BOOKS_STATE_KEY = "BOOKS_STATE_KEY"

    var mBooks: MutableList<Book> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        booksListRecyclerView.layoutManager = LinearLayoutManager(this)

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
        val retrofit = Retrofit.Builder().baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val client = retrofit.create(GoogleBooksClient::class.java)

        val call = client.searchBooks("lord+rings", BuildConfig.GOOGLE_API_KEY)
        val callback = object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
                val baseResponse = response?.body()
                if (baseResponse != null) {
                    mBooks.clear()
                    Log.i("BooksActivity", "Number of books found ${baseResponse.books.size}")
                    for (bookList in baseResponse.books) {
                        mBooks.add(bookList.book)
                    }
                    updateUi()
                } else {
                    Toast.makeText(this@BooksActivity, "Nops! ${response?.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("BooksActivity", "Error ${response?.code()}: ${Gson().toJson(response)}")
                }
            }

            override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
                t?.printStackTrace()
            }
        }

        call.enqueue(callback)
    }

    fun updateUi() {
        Log.i("BooksActivity", "Number of books to show ${mBooks.size}")
        booksListRecyclerView.adapter = BooksListAdapter(this, mBooks)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelableArrayList(BOOKS_STATE_KEY, ArrayList<Book>(mBooks))
    }
}
