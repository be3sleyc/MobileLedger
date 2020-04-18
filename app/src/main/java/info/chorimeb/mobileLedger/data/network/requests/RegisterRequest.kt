package info.chorimeb.mobileLedger.data.network.requests

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)