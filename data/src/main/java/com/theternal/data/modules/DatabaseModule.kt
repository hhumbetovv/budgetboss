package com.theternal.data.modules

import android.content.Context
import androidx.room.Room
import com.theternal.data.database.AppDatabase
import com.theternal.data.database.dao.AccountDao
import com.theternal.data.database.dao.CurrencyDao
import com.theternal.data.database.dao.RecordDao
import com.theternal.data.database.migrations.MigrationFrom1To2
import com.theternal.data.database.migrations.MigrationFrom2To3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room
            .databaseBuilder(
                context, AppDatabase::class.java, "record-database"
            )
            .addMigrations(MigrationFrom1To2())
            .addMigrations(MigrationFrom2To3())
            .build()
    }

    @Provides
    @Singleton
    fun provideRecordDao(
        appDatabase: AppDatabase
    ): RecordDao {
        return appDatabase.recordDao()
    }

    @Provides
    @Singleton
    fun provideAccountDao(
        appDatabase: AppDatabase
    ): AccountDao {
        return appDatabase.accountDao()
    }
    @Provides
    @Singleton
    fun provideCurrencyDao(
        appDatabase: AppDatabase
    ): CurrencyDao {
        return appDatabase.currencyDao()
    }
}