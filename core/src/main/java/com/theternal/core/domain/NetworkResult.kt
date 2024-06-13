package com.theternal.core.domain

import com.theternal.common.constants.CALL_FAILED_ERROR
import com.theternal.common.constants.CALL_SUCCESSFUL_ERROR

sealed class NetworkResult<T>(
    val isSuccess: Boolean,
    private val _data: T? = null,
    private val _exception: Exception? = null,
) {

    val getData: T
        get() {
            if (!isSuccess) {
                throw IllegalStateException(CALL_FAILED_ERROR)
            }
            return _data!!
        }

    val getException: Exception
        get() {
            if (isSuccess) {
                throw IllegalStateException(CALL_SUCCESSFUL_ERROR)
            }
            return _exception!!
        }

    data class Success<T>(val data: T) : NetworkResult<T>(
        true,
        data
    )

    data class Error<T>(val exception: Exception) : NetworkResult<T>(
        false,
        null,
        exception
    )
}
