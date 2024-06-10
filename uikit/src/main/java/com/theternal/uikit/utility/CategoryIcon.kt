package com.theternal.uikit.utility

import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.uikit.R

fun <T : Enum<*>> getCategoryIcon(category: T): Int {
    return when(category) {
        IncomeCategory.SALARY -> R.drawable.ic_salary
        IncomeCategory.INVESTMENTS -> R.drawable.ic_money_bag
        IncomeCategory.FREELANCE -> R.drawable.ic_laptop
        IncomeCategory.PENSION -> R.drawable.ic_card
        IncomeCategory.AWARDS -> R.drawable.ic_trophy
        ExpenseCategory.SHOPPING -> R.drawable.ic_shopping_cart
        ExpenseCategory.FOOD -> R.drawable.ic_food
        ExpenseCategory.ENTERTAINMENT -> R.drawable.ic_entertainment
        ExpenseCategory.EDUCATION -> R.drawable.ic_book
        ExpenseCategory.TRANSPORTATION -> R.drawable.ic_transport
        ExpenseCategory.HEALTH -> R.drawable.ic_health
        ExpenseCategory.GIFT -> R.drawable.ic_gift
        RecordType.TRANSFER -> R.drawable.ic_transfer
        else -> R.drawable.ic_dots
    }
}