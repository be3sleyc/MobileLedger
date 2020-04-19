package info.chorimeb.mobileLedger.data.network.requests

data class NewAccountRequest(
    val name: String,
    val type: String,
    val balance: String,
    val notes: String
)
