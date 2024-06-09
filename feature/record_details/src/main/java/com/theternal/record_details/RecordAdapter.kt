package com.theternal.record_details

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import com.theternal.common.extensions.Colors
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
import com.theternal.uikit.fragments.AppBottomSheetFragment
import com.theternal.uikit.utility.getIconDrawable
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter(
    private val fragmentManager: FragmentManager,
) : BaseAdapter<RecordEntity, ViewRecordItemBinding>(
    object : DiffUtil.ItemCallback<RecordEntity>() {
        override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.date == newItem.date
        }
    }
) {
    override val bindingInflater: Inflater<ViewRecordItemBinding>
        get() = ViewRecordItemBinding::inflate

    override val itemBinder: Binder<RecordEntity, ViewRecordItemBinding>
        get() = { item, _ ->

            val bottomSheet = AppBottomSheetFragment { RecordDetailsFragment(
                item.id,
                item is TransferEntity
            ) }

            container.setOnClickListener {
                if(!bottomSheet.isAdded) {
                    bottomSheet.show(fragmentManager, "Records")
                }
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

            title.text = item.title

            date.text = SimpleDateFormat(
                "dd.MM.yyyy", Locale.getDefault()
            ).format(item.date)

            if(item is TransferEntity) {
                sent.text = item.displaySentAmount()
                received.text = item.displayReceivedAmount()
                sent.show()
                received.show()
            } else {
                amount.text = (item as FinancialRecordEntity).displayAmount()
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