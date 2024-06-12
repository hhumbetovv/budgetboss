package com.theternal.domain.entities.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theternal.domain.interfaces.RecordEntity
import java.math.BigDecimal

@Entity(tableName = "transferRecords")
data class TransferEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,
    @ColumnInfo("title")
    override val title: String,
    @ColumnInfo("amount")
    override val amount: BigDecimal,
    @ColumnInfo("date")
    override val date: Long,
    @ColumnInfo("note")
    override val note: String? = null,
    @ColumnInfo("sender")
    val senderId: Long,
    @ColumnInfo("sentAmount")
    val sentAmount: String,
    @ColumnInfo("senderCurrency")
    val senderCurrency: String,
    @ColumnInfo("receiver")
    val receiverId: Long,
    @ColumnInfo("receivedAmount")
    val receivedAmount: String,
    @ColumnInfo("receiverCurrency")
    val receiverCurrency: String,
    @ColumnInfo("exchangeValue")
    val exchangeValue: BigDecimal
) : RecordEntity