package com.theternal.accounts

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.reports.ReportsFragmentDirections
import com.theternal.reports.databinding.ViewAccountItemBinding
import java.math.BigDecimal
import com.theternal.common.R.color as Colors

class AccountAdapter : BaseAdapter<AccountEntity, ViewAccountItemBinding>(
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
    override val bindingInflater: Inflater<ViewAccountItemBinding>
        get() = ViewAccountItemBinding::inflate

    override val itemBinder: Binder<AccountEntity, ViewAccountItemBinding>
        @SuppressLint("SetTextI18n")
        get() = { account, _ ->
            container.setOnClickListener {
                Navigation.findNavController(container).navigate(
                    ReportsFragmentDirections.toAccountDetails(account.id)
                )
            }
            name.text = account.name
            balance.text = account.displayBalance(true)

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