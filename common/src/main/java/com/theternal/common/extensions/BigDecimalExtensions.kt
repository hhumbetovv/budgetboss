package com.theternal.common.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln

fun BigDecimal.format(): String {
    val sign = if(this.signum() < 0) "-" else ""
    var value = this.abs()
    if (value < BigDecimal(1000)) {
        val formattedValue = value.setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString()
        return sign + if (formattedValue.contains(".")) {
            formattedValue.trimEnd('0').trimEnd('.')
        } else formattedValue
    }
    val exp = (ln(value.toDouble()) / ln(1000.0)).toInt()
    val suffix = "KMBTPEZYRQ"[exp - 1]
    value = value.divide(BigDecimal.valueOf(1000.0).pow(exp), 2, RoundingMode.HALF_UP)
    val formattedValue = value.stripTrailingZeros().toPlainString()
    return sign + if (formattedValue.contains(".")) {
        formattedValue.trimEnd('0').trimEnd('.') + suffix
    } else {
        formattedValue + suffix
    }
}