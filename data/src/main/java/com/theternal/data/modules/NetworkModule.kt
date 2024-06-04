package com.theternal.data.modules

import com.theternal.data.services.CurrencyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://currency-exchange.p.rapidapi.com"
    private const val TIMEOUT = 10L

    //! Interceptors
    @Provides
    @IntoSet
    fun provideHttpLogger(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @IntoSet
    fun provideApiInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()

        val newRequest = request.newBuilder()
            .addHeader("X-RapidAPI-Key", "fdb57825demsha9f52e197bdc9e4p12a2f6jsn43e509a5d5a5")
            .addHeader("X-RapidAPI-Host", "currency-exchange.p.rapidapi.com")
            .build()

        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)

        interceptors.forEach { builder.addInterceptor(it) }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        onHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(onHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyService(
        retrofit: Retrofit,
    ): CurrencyService {
        return retrofit.create(CurrencyService::class.java)
    }

}