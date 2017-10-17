package br.com.macaxeira.bookworm.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(val mToken: String?): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder().header("Authorization", "Bearer " + mToken)

        val request = builder.build()
        return chain.proceed(request)
    }
}