package com.theternal.domain.entities.remote

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ExchangeModel(
    @SerializedName("conversion_rate")
    val conversionRate: BigDecimal
)