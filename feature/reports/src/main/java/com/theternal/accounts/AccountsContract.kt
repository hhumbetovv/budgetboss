
package com.theternal.accounts

import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.entities.local.AccountEntity

sealed class AccountsContract {

    data class State(
        val accounts: List<AccountEntity> = listOf(),
    ) : ViewState

}