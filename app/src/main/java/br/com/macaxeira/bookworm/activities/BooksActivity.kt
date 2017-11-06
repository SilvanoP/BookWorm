package br.com.macaxeira.bookworm.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

class BooksActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val BOOKS_STATE_KEY = "BOOKS_STATE_KEY"
    private val BOOKSHELF_CURRENTLY_READING: Long = 3
    private val BOOKSHELF_READ: Long = 4
    private val BOOKSHELF_TO_READ: Long = 2

    lateinit var mDrawerToogle: ActionBarDrawerToggle

    var mBooks: MutableList<Book> = mutableListOf()
    var mBookshelf: Long = BOOKSHELF_CURRENTLY_READING
    var mUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        val toolbar: Toolbar = findViewById(R.id.bookActivityToolbar)
        setSupportActionBar(toolbar)

        mDrawerToogle = ActionBarDrawerToggle(this, bookActivityDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
        bookActivityDrawer.addDrawerListener(mDrawerToogle)
        bookActivityNavigationView.setNavigationItemSelectedListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

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
            val call = client.getBooksFromBookshelf(userId, mBookshelf, BuildConfig.GOOGLE_API_KEY)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.navDrawerRead && !mBookshelf.equals(BOOKSHELF_READ)) {
            mBookshelf = BOOKSHELF_READ
            searchBooks()
        } else if (item?.itemId == R.id.navDrawerCurrentlyReading
                && !mBookshelf.equals(BOOKSHELF_CURRENTLY_READING)) {
            mBookshelf = BOOKSHELF_CURRENTLY_READING
            searchBooks()
        } else if (item?.itemId == R.id.navDrawerToRead && !mBookshelf.equals(BOOKSHELF_TO_READ)) {
            mBookshelf = BOOKSHELF_TO_READ
            searchBooks()
        }

        bookActivityDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateUi() {
        if (mBooks.size == 0) {
            booksListRecyclerView.visibility = View.GONE
            bookActivityEmptyList.visibility = View.VISIBLE
        } else {
            booksListRecyclerView.visibility = View.VISIBLE
            bookActivityEmptyList.visibility = View.GONE
            booksListRecyclerView.adapter = BooksListAdapter(this, mBooks)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelableArrayList(BOOKS_STATE_KEY, ArrayList<Book>(mBooks))
    }
}
