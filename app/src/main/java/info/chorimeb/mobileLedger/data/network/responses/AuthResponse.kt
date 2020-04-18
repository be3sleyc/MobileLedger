package info.chorimeb.mobileLedger.data.network.responses

import info.chorimeb.mobileLedger.data.db.entities.User

data class AuthResponse(val message: String?, val user: User?)