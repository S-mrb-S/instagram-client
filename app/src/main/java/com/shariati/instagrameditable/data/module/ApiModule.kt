package com.shariati.instagrameditable.data.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shariati.instagrameditable.data.repository.ApiServices
import com.shariati.instagrameditable.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun BaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun ConnectionTimeOut() = 60

    @Provides
    @Singleton
    fun ProvideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun ProvideOKHttpClient() = OkHttpClient.Builder().connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(Interceptor { chain ->
            val request: Request = chain.request().newBuilder()
                //.addHeader("x-app-token", App_TOKEN)
                .build()
            chain.proceed(request)
        }).build()

    @Provides
    @Singleton
    fun ProvideRetrofit(baseUrl: String, gson: Gson, client: OkHttpClient):
            ApiServices = Retrofit.Builder().baseUrl(baseUrl).client(client)
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
        .create(ApiServices::class.java)

}