package info.chorimeb.mobileLedger.data.network.responses

import info.chorimeb.mobileLedger.data.db.entities.Account

data class AccountResponse(
    val message: String?,
    val account: Account?
)