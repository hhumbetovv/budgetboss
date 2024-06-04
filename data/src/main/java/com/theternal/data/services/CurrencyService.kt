package com.theternal.data.services

import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

interface CurrencyService {

    @GET("/listquotes")
    suspend fun getCurrencyList(): List<String>

    @GET("/exchange")
    suspend fun exchange(
        @Query("from") from: String,
        @Query("to") to: String,
    ): BigDecimal
}