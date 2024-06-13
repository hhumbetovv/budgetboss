package com.theternal.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.extensions.format

class MigrationFrom1To2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transferRecords ADD COLUMN sentAmount TEXT NOT NULL DEFAULT '0'")
        db.execSQL("ALTER TABLE transferRecords ADD COLUMN receivedAmount TEXT NOT NULL DEFAULT '0'")

        createSentAmount(db)

        createReceivedAmount(db)
    }

    private fun createSentAmount(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT id, amount, senderCurrency FROM transferRecords")
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex("id")
            if(idIndex == -1) return
            val id = cursor.getLong(idIndex)
            val amountIndex = cursor.getColumnIndex("amount")
            if(amountIndex == -1) return
            val amount = cursor.getString(amountIndex).toBigDecimal()
            val senderCurrencyIndex = cursor.getColumnIndex("senderCurrency")
            if(senderCurrencyIndex == -1) return
            val senderCurrency = cursor.getString(senderCurrencyIndex)

            val sentAmount = "$MINUS${amount.format()} $senderCurrency"

            val updateQuery = "UPDATE transferRecords SET sentAmount = '$sentAmount' WHERE id = $id"
            db.execSQL(updateQuery)
        }
        cursor.close()
    }

    private fun createReceivedAmount(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT id, amount, receiverCurrency, exchangeValue FROM transferRecords")
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex("id")
            if(idIndex == -1) return
            val id = cursor.getLong(idIndex)

            val amountIndex = cursor.getColumnIndex("amount")
            if(amountIndex == -1) return
            val amount = cursor.getString(amountIndex).toBigDecimal()

            val senderCurrencyIndex = cursor.getColumnIndex("receiverCurrency")
            if(senderCurrencyIndex == -1) return
            val senderCurrency = cursor.getString(senderCurrencyIndex)

            val exchangeValueIndex = cursor.getColumnIndex("exchangeValue")
            if(exchangeValueIndex == -1) return
            val exchangeValue = cursor.getString(exchangeValueIndex).toBigDecimal()

            val receivedAmount = "$PLUS${(amount * exchangeValue).format()} $senderCurrency"

            val updateQuery = "UPDATE transferRecords SET receivedAmount = '$receivedAmount' WHERE id = $id"
            db.execSQL(updateQuery)
        }
        cursor.close()
    }
}