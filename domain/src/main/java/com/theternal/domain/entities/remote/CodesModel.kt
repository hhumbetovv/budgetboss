package com.theternal.domain.entities.remote

import com.google.gson.annotations.SerializedName

data class CodesModel(
    @SerializedName("supported_codes")
    val supportedCodes: List<List<String>>
)