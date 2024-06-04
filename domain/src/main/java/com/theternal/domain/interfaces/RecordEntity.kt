package com.theternal.domain.interfaces

import java.math.BigDecimal

interface RecordEntity {
    val id: Long
    val title: String
    val amount: BigDecimal
    val date: Long
    val note: String?
}