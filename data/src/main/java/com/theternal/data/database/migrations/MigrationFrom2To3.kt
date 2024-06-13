package com.theternal.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.theternal.common.constants.DOLLAR
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.extensions.format

class MigrationFrom2To3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE financialRecords ADD COLUMN amountText TEXT NOT NULL DEFAULT '0'")

        createAmountText(db)
    }

    private fun createAmountText(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT id, amount, isExpense FROM financialRecords")
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex("id")
            if(idIndex == -1) return
            val id = cursor.getLong(idIndex)

            val amountIndex = cursor.getColumnIndex("amount")
            if(amountIndex == -1) return
            val amount = cursor.getString(amountIndex).toBigDecimal()

            val isExpenseIndex = cursor.getColumnIndex("isExpense")
            if(isExpenseIndex == -1) return
            val isExpense = cursor.getInt(isExpenseIndex) > 0

            val prefix = if(isExpense) MINUS else PLUS
            val amountText = "$prefix${amount.format()} $DOLLAR"

            val updateQuery = "UPDATE financialRecords SET amountText = '$amountText' WHERE id = $id"
            db.execSQL(updateQuery)
        }
        cursor.close()
    }
}