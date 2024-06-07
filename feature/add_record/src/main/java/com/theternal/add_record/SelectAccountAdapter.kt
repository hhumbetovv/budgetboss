package com.theternal.add_record

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.theternal.add_record.databinding.ViewSelectAccountBinding
import com.theternal.common.extensions.format
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.local.AccountEntity
import java.math.BigDecimal
import com.theternal.common.R.color as Colors

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
        @SuppressLint("SetTextI18n")
        get() = { account, _ ->
            container.setOnClickListener {
                onItemClickListener(account)
            }
            name.text = account.name
            balance.text = "${account.balance.format()} ${account.currency}"
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