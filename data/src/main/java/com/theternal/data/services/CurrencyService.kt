package com.theternal.data.services

import com.theternal.domain.entities.remote.CodesModel
import com.theternal.domain.entities.remote.ExchangeModel
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyService {

    @GET("codes")
    suspend fun getCodes(): CodesModel

    @GET("pair/{from}/{to}")
    suspend fun exchange(
        @Path("from") from: String,
        @Path("to") to: String,
    ): ExchangeModel
}