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
                if (imageLinks.small != null) {
                    url = imageLinks.small
                } else {
                    url = imageLinks.medium
                }

                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(mContext).load(url)
                            .placeholder(R.drawable.error_loading_image)
                            .error(R.drawable.error_loading_image)
                            .into(itemView.itemBookCoverImage)
                }
            }

            if (TextUtils.isEmpty(url)) {
                itemView.itemBookCoverImage.setImageResource(R.drawable.error_loading_image)
            }
            itemView.itemBookNameText.text = book.title
        }
    }
}