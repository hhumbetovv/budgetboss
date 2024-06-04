package com.theternal.core.domain

sealed class NetworkResult<T>(
    val isSuccess: Boolean,
    private val _data: T? = null,
    private val _exception: Exception? = null,
) {

    val getData: T
        get() {
            if (!isSuccess) {
                throw IllegalStateException("Call isn't successful")
            }
            return _data!!
        }

    val getException: Exception
        get() {
            if (isSuccess) {
                throw IllegalStateException("Call is successful, There is no Exception")
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
