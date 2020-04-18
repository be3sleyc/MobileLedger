package info.chorimeb.mobileLedger.data.network.responses

import info.chorimeb.mobileLedger.data.db.entities.Transaction

data class TransactionListResponse(
    val message: String?,
    val transactions: List<Transaction>?
)