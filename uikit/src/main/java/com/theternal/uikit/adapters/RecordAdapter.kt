package com.theternal.uikit.adapters

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.theternal.common.extensions.format
import com.theternal.common.extensions.show
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.domain.entities.base.RecordType.*
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.common.R.color as Colors
import com.theternal.uikit.databinding.ViewRecordItemBinding
import com.theternal.uikit.utility.getIconDrawable
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter : BaseAdapter<RecordEntity, ViewRecordItemBinding>(
    object : DiffUtil.ItemCallback<RecordEntity>() {
        override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.title == newItem.title && oldItem.amount == newItem.amount
        }
    }
) {
    override val bindingInflater: Inflater<ViewRecordItemBinding>
        get() = ViewRecordItemBinding::inflate

    override val itemBinder: Binder<RecordEntity, ViewRecordItemBinding>
        @SuppressLint("SetTextI18n")
        get() = { item, _ ->
            val type = when (item) {
                is FinancialRecordEntity -> {
                    if (item.isExpense) EXPENSE
                    else INCOME
                }
                else -> TRANSFER
            }

            icon.setImageDrawable(ContextCompat.getDrawable(
                icon.context, getIcon(type, item.title)
            ))

            title.text = item.title

            date.text = SimpleDateFormat(
                "dd.MM.yyyy", Locale.getDefault()
            ).format(item.date)

            if(item is TransferEntity) {
                sent.text = "-${item.amount.format()} ${item.senderCurrency}"
                received.text = "+${(item.amount * item.exchangeValue).format()} ${item.receiverCurrency}"
                sent.show()
                received.show()
            } else {
                val prefix = if(type == INCOME) "+" else "-"
                amount.text = "$prefix${item.amount.format()} \$"
                amount.setTextColor(ContextCompat.getColor(
                    amount.context,
                    when(type) {
                        INCOME -> Colors.primary
                        else -> Colors.danger
                    }
                ))
                amount.show()
            }
        }

    private fun getIcon(type: RecordType, title: String): Int {
        return getIconDrawable(
            when(type) {
                INCOME -> IncomeCategory.valueOf(title.uppercase())
                EXPENSE -> ExpenseCategory.valueOf(title.uppercase())
                TRANSFER -> type
            }
        )
    }
}