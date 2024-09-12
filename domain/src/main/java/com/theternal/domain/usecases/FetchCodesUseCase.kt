package com.theternal.domain.usecases

import android.util.Log
import com.theternal.domain.repositories.CurrencyRepository
import javax.inject.Inject

class FetchCodesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(): List<String> {
        val list = currencyRepository.fetchCodes()
        Log.d("CreateAccountViewModel", list.toString())
        return list.map {
            it.first
        }
    }
}