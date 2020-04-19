package info.chorimeb.mobileLedger.data.network.requests

data class TransactionRequest(
    val accountid: Int,
    val amount: String,
    val paiddate: String,
    val payee: String,
    val description: String,
    val category: String
)
