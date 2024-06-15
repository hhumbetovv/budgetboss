package com.theternal.select_account

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.theternal.add_record.databinding.ViewSelectAccountBinding
import com.theternal.common.extensions.Colors
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.local.AccountEntity
import java.math.BigDecimal

class SelectAccountAdapter(
    val onItemClickListener: (AccountEntity) -> Unit
) : BaseAdapter<AccountEntity, ViewSelectAccountBinding>(
    object : DiffUtil.ItemCallback<AccountEntity>() {
        override fun areItemsTheSame(oldItem: AccountEntity, newItem: AccountEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AccountEntity, newItem: AccountEntity): Boolean {
            return oldItem.balance == newItem.balance &&
                    oldItem.name == newItem.name
        }
    }
) {
    override val bindingInflater: Inflater<ViewSelectAccountBinding>
        get() = ViewSelectAccountBinding::inflate

    override val itemBinder: Binder<AccountEntity, ViewSelectAccountBinding>
        get() = { account, _ ->
            container.setOnClickListener {
                onItemClickListener(account)
            }
            name.text = account.name
            balance.text = account.displayBalance()
            balance.setTextColor(
                ContextCompat.getColor(
                    balance.context,
                    when {
                        account.balance > BigDecimal.ZERO -> Colors.primary
                        account.balance < BigDecimal.ZERO -> Colors.danger
                        else -> Colors.white
                    }
                )
            )
        }
}