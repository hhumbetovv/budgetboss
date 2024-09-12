package com.theternal.uikit.utility

import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.uikit.R

fun <T : Enum<*>> getCategoryIcon(category: T): Int {
    return when(category) {
        IncomeCategory.SALARY -> R.drawable.ic_money
        IncomeCategory.INVESTMENTS -> R.drawable.ic_chart_presentation
        IncomeCategory.FREELANCE -> R.drawable.ic_tick_briefcase
        IncomeCategory.PENSION -> R.drawable.ic_personal_card
        IncomeCategory.AWARDS -> R.drawable.ic_cup
        ExpenseCategory.SHOPPING -> R.drawable.ic_shopping_cart
        ExpenseCategory.FOOD -> R.drawable.ic_fork_knife
        ExpenseCategory.ENTERTAINMENT -> R.drawable.ic_music_note
        ExpenseCategory.EDUCATION -> R.drawable.ic_book
        ExpenseCategory.TRANSPORTATION -> R.drawable.ic_bus
        ExpenseCategory.HEALTH -> R.drawable.ic_health
        ExpenseCategory.GIFT -> R.drawable.ic_gift
        RecordType.TRANSFER -> R.drawable.ic_swap_horizontal
        else -> R.drawable.ic_circle_more
    }
}