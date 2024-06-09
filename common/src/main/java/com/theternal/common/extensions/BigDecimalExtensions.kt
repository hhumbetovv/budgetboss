package com.theternal.common.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln


/**
 * Extension function to format a BigDecimal value into a human-readable string.
 *
 * Examples:
 * - 999.999 -> "1000"
 * - 1500 -> "1.5K"
 * - 1500000 -> "1.5M"
 * - 1500000000 -> "1.5B"
 * - -1500000 -> "-1.5M"
 *
 * @receiver The BigDecimal value to be formatted.
 * @return A formatted string representing the value in a human-readable format.
 */
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