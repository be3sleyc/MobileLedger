package info.chorimeb.mobileLedger.data.network.responses

import info.chorimeb.mobileLedger.data.db.entities.Account

data class AccountListResponse(
    val message: String?,
    val accounts: List<Account>?
)