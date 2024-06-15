package com.theternal.uikit.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.theternal.common.extensions.Colors
import com.theternal.common.extensions.hide
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
import com.theternal.uikit.databinding.ViewRecordItemBinding
import com.theternal.uikit.utility.getCategoryIcon
import com.theternal.uikit.utility.getCategoryName
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter(
    private val onItemClickListener: ((RecordEntity) -> Unit)?,
) : BaseAdapter<RecordEntity, ViewRecordItemBinding>(
    object : DiffUtil.ItemCallback<RecordEntity>() {
        override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.date == newItem.date
        }
    }
) {
    override val bindingInflater: Inflater<ViewRecordItemBinding>
        get() = ViewRecordItemBinding::inflate

    override val itemBinder: Binder<RecordEntity, ViewRecordItemBinding>
        get() = { item, _ ->

            recordContainer.setOnClickListener {
                onItemClickListener?.invoke(item)
            }

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



            title.text = getName(type, item.title, title.context)

            date.text = SimpleDateFormat(
                "dd.MM.yyyy", Locale.getDefault()
            ).format(item.date)

            if(item is TransferEntity) {
                sent.text = item.sentAmount
                received.text = item.receivedAmount
                sent.show()
                received.show()
                amount.hide()
            } else {
                sent.hide()
                received.hide()
                amount.text = (item as FinancialRecordEntity).amountText
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

    private fun getName(type: RecordType, title: String, context: Context): String {
        if(type == TRANSFER) return title
        val category = if(type == INCOME) IncomeCategory.valueOf(title.uppercase())
        else ExpenseCategory.valueOf(title.uppercase())
        return context.getString(getCategoryName(category))
    }

    private fun getIcon(type: RecordType, title: String): Int {
        return getCategoryIcon(
            when(type) {
                INCOME -> IncomeCategory.valueOf(title.uppercase())
                EXPENSE -> ExpenseCategory.valueOf(title.uppercase())
                TRANSFER -> type
            }
        )
    }
}