package info.chorimeb.mobileLedger.data.network.responses

import info.chorimeb.mobileLedger.data.db.entities.Transaction

class TransactionResponse (
    val message: String?,
    val transaction: Transaction?
)