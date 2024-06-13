package com.theternal.domain.interfaces

interface Logger {
    fun log(event: String, vararg params: Pair<String, String>)
}