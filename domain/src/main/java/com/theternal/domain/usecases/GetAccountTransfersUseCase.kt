package com.theternal.domain.usecases

import android.util.Log
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAccountTransfersUseCase @Inject constructor(
    private val recordRepository: RecordRepository
){
    operator fun invoke(transfers: List<Long>): Flow<List<TransferEntity>> {
        return recordRepository.getAllTransfers().map { records ->
            records.filter { transfers.contains(it.id) }
        }
    }
}