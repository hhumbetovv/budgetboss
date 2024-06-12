
package com.theternal.domain.entities.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theternal.domain.interfaces.RecordEntity
import java.math.BigDecimal

@Entity(tableName = "financialRecords")
data class FinancialRecordEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,
    @ColumnInfo("title")
    override val title: String,
    @ColumnInfo("amount")
    override val amount: BigDecimal,
    @ColumnInfo("amountText")
    val amountText: String,
    @ColumnInfo("date")
    override val date: Long,
    @ColumnInfo("note")
    override val note: String? = null,
    @ColumnInfo("isExpense")
    val isExpense: Boolean,
) : RecordEntity