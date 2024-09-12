package com.theternal.domain.repositories

import com.theternal.domain.entities.local.CurrencyEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface CurrencyRepository {
    fun saveCurrency(label: String, value: BigDecimal)
    fun saveCurrencyList(currencyList: Map<String,BigDecimal>)
    fun getCurrencyList(): Flow<List<CurrencyEntity>>
    fun getCurrencyListAsync(): List<CurrencyEntity>
    fun getCurrencyValueAsync(label: String): BigDecimal

    suspend fun fetchCodes(): List<Pair<String, String>>
    suspend fun exchange(from: String, to: String?): BigDecimal
}