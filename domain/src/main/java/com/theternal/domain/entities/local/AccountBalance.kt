package com.theternal.domain.entities.local

import androidx.room.ColumnInfo
import java.math.BigDecimal

data class AccountBalance(
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("balance")
    val balance: BigDecimal,
)