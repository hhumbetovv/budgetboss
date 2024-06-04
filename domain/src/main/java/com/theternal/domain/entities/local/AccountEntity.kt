package com.theternal.domain.entities.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "accounts")
data class AccountEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("balance")
    val balance: BigDecimal,
    @ColumnInfo("note")
    val note: String?,
    @ColumnInfo("transfers")
    val transfers: List<Long> = listOf(),
)