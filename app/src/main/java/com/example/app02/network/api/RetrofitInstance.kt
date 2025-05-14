package com.example.app02.network.api


import android.content.Context
import com.example.app02.data.AuthInterceptor
import com.example.app02.data.DataStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.100.107:8080/"
    private lateinit var dataStore: DataStore
    private lateinit var apiServiceInstance: ApiService

    fun initialize(context: Context) {
        dataStore = DataStore(context)

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(dataStore))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        apiServiceInstance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val apiService: ApiService
        get() = apiServiceInstance
}

