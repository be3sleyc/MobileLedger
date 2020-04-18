package info.chorimeb.mobileLedger.data.network.requests

data class TransactionRequest(
    val accountid: Int,
    val amount: Double,
    val paiddate: String,
    val payee: String,
    val description: String
)
