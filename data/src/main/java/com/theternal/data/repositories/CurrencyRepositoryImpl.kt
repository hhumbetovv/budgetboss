package com.theternal.data.repositories

import com.theternal.data.database.dao.CurrencyDao
import com.theternal.data.services.CurrencyService
import com.theternal.domain.entities.local.CurrencyEntity
import com.theternal.domain.repositories.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val currencyService: CurrencyService,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {
    override fun saveCurrency(label: String, value: BigDecimal) {
        currencyDao.addCurrency(CurrencyEntity(label, value))
    }

    override fun saveCurrencyList(currencyList: Map<String, BigDecimal>) {
        currencyList.forEach{ (label, value) ->
            currencyDao.addCurrency(CurrencyEntity(label, value))
        }
    }

    override fun getCurrencyList(): Flow<List<CurrencyEntity>> {
        return currencyDao.getCurrencyList()
    }

    override fun getCurrencyListAsync(): List<CurrencyEntity> {
        return currencyDao.getCurrencyListAsync()
    }

    override suspend fun fetchCurrencyList(): List<String> {
        return currencyService.getCurrencyList()
    }

    override suspend fun exchange(from: String, to: String?): BigDecimal {
        if(from == to || (from == "USD" && to == null)) {
            return BigDecimal.ONE
        }
        return currencyService.exchange(from, to ?: "USD")
    }
}