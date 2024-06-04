package com.theternal.domain.entities.local

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity
import java.math.BigDecimal

@Entity("currencyList")
data class CurrencyEntity(
    @PrimaryKey
    @ColumnInfo("label")
    val label: String,
    @ColumnInfo("value")
    val value: BigDecimal
)