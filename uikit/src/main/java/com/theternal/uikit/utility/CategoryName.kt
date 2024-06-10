package com.theternal.uikit.utility

import com.theternal.common.extensions.Strings
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType

fun <T : Enum<*>> getCategoryName(category: T): Int {
    return when(category) {
        IncomeCategory.SALARY -> Strings.salary
        IncomeCategory.INVESTMENTS -> Strings.investments
        IncomeCategory.FREELANCE -> Strings.freelance
        IncomeCategory.PENSION -> Strings.pension
        IncomeCategory.AWARDS -> Strings.awards
        ExpenseCategory.SHOPPING -> Strings.shopping
        ExpenseCategory.FOOD -> Strings.food
        ExpenseCategory.ENTERTAINMENT -> Strings.entertainment
        ExpenseCategory.EDUCATION -> Strings.education
        ExpenseCategory.TRANSPORTATION -> Strings.transportation
        ExpenseCategory.HEALTH -> Strings.health
        ExpenseCategory.GIFT -> Strings.gift
        RecordType.TRANSFER -> Strings.transfer
        RecordType.INCOME -> Strings.income
        RecordType.EXPENSE -> Strings.expense
        else -> Strings.others
    }
}