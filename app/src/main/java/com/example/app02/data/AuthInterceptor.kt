package com.example.app02.data

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val dataStore: DataStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { dataStore.getToken() }

        Log.d("AuthInterceptor", "Request URL: ${chain.request().url}")
        Log.d("AuthInterceptor", "Gửi token: $token")
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()


        }else{
            chain.request()
        }

        return chain.proceed(request)
    }
}