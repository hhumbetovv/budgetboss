package com.theternal.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theternal.domain.entities.local.CurrencyEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCurrency(currency: CurrencyEntity)

    @Query("SELECT * FROM currencyList")
    fun getCurrencyList(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencyList")
    fun getCurrencyListAsync(): List<CurrencyEntity>

    @Query("SELECT value FROM currencyList WHERE label=:label")
    fun getCurrencyValue(label: String): Flow<BigDecimal>
}