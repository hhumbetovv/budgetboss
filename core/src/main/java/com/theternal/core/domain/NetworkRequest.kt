package com.theternal.core.domain

import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1

sealed class NetworkRequest<Result> {

    data class NoParams<Result>(
        val function: KSuspendFunction0<Result>
    ) : NetworkRequest<Result>() {
        override suspend fun invoke(): NetworkResult<Result> {
            return try {
                NetworkResult.Success(function.invoke())
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }

    data class WithParams<Result, Param>(
        val function: KSuspendFunction1<Param, Result>,
        val param: Param
    ) : NetworkRequest<Result>() {
        override suspend fun invoke(): NetworkResult<Result> {
            return try {
                NetworkResult.Success(function.invoke(param))
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }

    internal abstract suspend fun invoke() : NetworkResult<Result>
}