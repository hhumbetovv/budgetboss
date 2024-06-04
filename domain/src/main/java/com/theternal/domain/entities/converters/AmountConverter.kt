package com.theternal.domain.entities.converters

import androidx.room.TypeConverter
import java.math.BigDecimal

class AmountConverter {
    @TypeConverter
    fun fromBigDecimalToString(value: BigDecimal): String {
        return value.toString()
    }

    @TypeConverter
    fun fromStringToBigDecimal(value: String): BigDecimal {
        return value.toBigDecimal()
    }
}