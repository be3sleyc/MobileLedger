package info.chorimeb.mobileLedger.data.network.requests

data class TransactionRequest(
    val accountid: Int,
    val paiddate: String,
    val payee: String,
    val description: String,
    val amount: String,
    val category: String
)
