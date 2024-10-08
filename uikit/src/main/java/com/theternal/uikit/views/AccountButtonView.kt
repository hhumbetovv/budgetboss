package com.theternal.uikit.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.getColor
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
    private var label: String? = context.getString(Strings.select_account)

    private val hintColor = getColor(Colors.hint)
    private val primaryColor = getColor(Colors.primary)
    private val dangerColor = getColor(Colors.danger)
    private val textColor = getColor(Colors.text)

    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.AccountButtonView, defAttrs, 0
        ).apply {

            mainColor = getColor(R.styleable.AccountButtonView_mainColor, textColor)

            recycle()
        }

        binding.name.text = label
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.accountContainer.setOnClickListener(l)
    }

    fun setAccount(account: AccountEntity?) {
        setLabel(account?.name)
        setBorder(account != null)
        setBalance(account)
    }

    private fun setBorder(isActive: Boolean) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = resources.displayMetrics.density * 16 + 0.5f
        gradientDrawable.setColor(getColor(Colors.container))
        if(isActive) {
            val width = (resources.displayMetrics.density + 0.5f).toInt()
            gradientDrawable.setStroke(width, mainColor ?: hintColor)
        } else {
            gradientDrawable.setStroke(0, hintColor)
        }

        val colors = ColorStateList.valueOf(getColor(Colors.ripple))
        val rippleDrawable = RippleDrawable(colors, gradientDrawable, null)

        binding.accountContainer.background = rippleDrawable
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

    private fun setBalance(account: AccountEntity?) {
        binding.balance.let { tv ->
            if(account == null) {
                tv.visibility = GONE
                return
            }
            tv.visibility = VISIBLE
            tv.text = account.displayBalance(true)
            tv.setTextColor(
                when {
                    account.balance > BigDecimal.ZERO -> primaryColor
                    account.balance < BigDecimal.ZERO -> dangerColor
                    else -> textColor
                }
            )
        }
    }
}