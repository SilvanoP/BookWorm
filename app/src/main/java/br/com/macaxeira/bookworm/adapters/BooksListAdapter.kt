package br.com.macaxeira.bookworm.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.macaxeira.bookworm.R
import br.com.macaxeira.bookworm.models.Book
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.list_item_books.view.*

class BooksListAdapter(val mContext: Context, val mBooks: List<Book>):
        RecyclerView.Adapter<BooksListAdapter.BooksListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BooksListViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.list_item_books, parent, false)
        return BooksListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BooksListViewHolder?, position: Int) {
        holder?.bind(mBooks[position])
    }

    override fun getItemCount(): Int = mBooks.size

    inner class BooksListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(book: Book) {
            var url: String? = null
            val imageLinks = book.imageLinks
            if (imageLinks != null) {
                url = imageLinks.thumbnail
                if (imageLinks.medium != null) {
                    url = imageLinks.medium
                }
            }

            if (!TextUtils.isEmpty(url)) {
                Picasso.with(mContext).load(url)
                        .placeholder(R.drawable.error_loading_image)
                        .error(R.drawable.error_loading_image)
                        .into(itemView.itemBookCoverImage)
            } else {
                itemView.itemBookCoverImage.setImageResource(R.drawable.error_loading_image)
            }

            itemView.itemBookNameText.text = book.title
            // Only show the first two authors
            var authors = "by " + book.authors[0].toLowerCase().capitalize()
            if (book.authors.size > 1) {
                authors += ", " + book.authors[1].toLowerCase().capitalize()
            }
            itemView.itemBookAuthorText.text = authors
            itemView.itemBookRateText.text = book.averageRating.toString()
            itemView.itemBookRateBar.rating = book.averageRating.toFloat()

            //FIXME input logic for different status icons and status text
            itemView.itemBookStatusImage.setImageResource(R.drawable.ic_currently_reading)
            itemView.itemBookStatusText.text = mContext.getText(R.string.tools_book_status_name)
        }
    }
}