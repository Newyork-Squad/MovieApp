package com.karrar.movieapp.data

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LanguageInterceptor(
    private val languageProvider: suspend () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val languageCode = runBlocking {
            when (languageProvider()) {
                "ar" -> "ar"
                else -> "en"
            }
        }

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("language", languageCode)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
