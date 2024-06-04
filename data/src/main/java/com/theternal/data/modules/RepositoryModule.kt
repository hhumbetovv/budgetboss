package com.theternal.data.modules

import com.theternal.data.repositories.AccountRepositoryImpl
import com.theternal.data.repositories.CurrencyRepositoryImpl
import com.theternal.data.repositories.RecordRepositoryImpl
import com.theternal.domain.repositories.AccountRepository
import com.theternal.domain.repositories.CurrencyRepository
import com.theternal.domain.repositories.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindRecordRepository(
        recordRepositoryImpl: RecordRepositoryImpl
    ): RecordRepository

    @Binds
    fun bindCurrencyRepository(
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository

    @Binds
    fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository
}