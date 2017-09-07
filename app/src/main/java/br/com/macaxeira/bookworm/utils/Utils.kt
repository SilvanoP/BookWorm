package br.com.macaxeira.bookworm.utils

import android.content.Context
import android.net.ConnectivityManager

class Utils {

    companion object {
        fun isOnline(context: Context) : Boolean {
            val connection = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connection.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}