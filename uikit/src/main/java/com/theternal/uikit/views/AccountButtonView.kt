package com.theternal.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getColor
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.show
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.uikit.R
import com.theternal.uikit.databinding.ViewAccountButtonBinding
import java.math.BigDecimal
import com.theternal.common.R.color as Colors

class AccountButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defAttrs: Int = 0,
) : ConstraintLayout(context, attrs, defAttrs) {

    private val binding = ViewAccountButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )
    private var mainColor: Int? = null
    private var label: String? = "Select Account"

    private val hintColor = getColor(Colors.hint)
    private val primaryColor = getColor(Colors.primary)
    private val dangerColor = getColor(Colors.danger)
    private val whiteColor = getColor(Colors.white)

    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.AccountButtonView, defAttrs, 0
        ).apply {

            mainColor = getColor(R.styleable.AccountButtonView_mainColor, whiteColor)

            recycle()
        }

        binding.name.text = label
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.container.setOnClickListener(l)
    }

    fun setAccount(account: AccountEntity?) {
        Log.d("LOGGER", account.toString())
        setLabel(account?.name)
        setBorder(account != null)
        setBalance(account?.balance, account?.currency)
    }

    private fun setBorder(isActive: Boolean) {
        ((binding.container.background as RippleDrawable).findDrawableByLayerId(
            android.R.id.background
        ) as GradientDrawable).apply {
            mutate()
            if(isActive) {
                setStroke(1, mainColor ?: hintColor)
            } else {
                setStroke(0, hintColor)
            }
        }
    }

    private fun setLabel(accountName: String?) {
        binding.apply {
            name.text = accountName ?: label
            name.setTextColor(
                if(accountName == null || mainColor == null) hintColor
                else mainColor!!
            )
            if(accountName == null) {
                chevronDownIcon.visibility = VISIBLE
            } else {
                chevronDownIcon.visibility = GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBalance(balance: BigDecimal?, currency: String?) {
        binding.balance.let { tv ->
            if(balance == null) {
                tv.visibility = GONE
                return
            }
            tv.visibility = VISIBLE
            tv.text = "${balance.format()} $currency"
            tv.setTextColor(
                when {
                    balance > BigDecimal.ZERO -> primaryColor
                    balance < BigDecimal.ZERO -> dangerColor
                    else -> whiteColor
                }
            )
        }
    }
}