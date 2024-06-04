
package com.theternal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theternal.data.database.dao.AccountDao
import com.theternal.data.database.dao.CurrencyDao
import com.theternal.data.database.dao.RecordDao
import com.theternal.domain.entities.converters.AmountConverter
import com.theternal.domain.entities.converters.IdListConverter
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.CurrencyEntity
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity

@Database(
    entities = [
        FinancialRecordEntity::class,
        TransferEntity::class,
        CurrencyEntity::class,
        AccountEntity::class
               ],
    version = 1,
    exportSchema = false
)
@TypeConverters(IdListConverter::class, AmountConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recordDao() : RecordDao
    abstract fun accountDao() : AccountDao
    abstract fun currencyDao() : CurrencyDao
}