package br.com.macaxeira.bookworm.services

import android.text.TextUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {

    companion object {
        val API_BASE_URL = "https://www.googleapis.com/"

        private val httpClient = OkHttpClient.Builder()

        private val builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        fun <S> createService(serviceClass: Class<S>): S = createService(serviceClass, null)

        fun <S> createService(serviceClass: Class<S>, authToken: String?): S {
            if (!TextUtils.isEmpty(authToken)) {
                // TODO create interceptor when needed
            }

            val retrofit = builder.build()
            return retrofit.create(serviceClass)
        }
    }
}