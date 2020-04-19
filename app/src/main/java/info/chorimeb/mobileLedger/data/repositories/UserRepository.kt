package info.chorimeb.mobileLedger.data.repositories

import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.SafeApiRequest
import info.chorimeb.mobileLedger.data.network.requests.LoginRequest
import info.chorimeb.mobileLedger.data.network.requests.RegisterRequest
import info.chorimeb.mobileLedger.data.network.responses.AuthResponse

class UserRepository(
    private val api: ApiService,
    private val db: AppDatabase,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : SafeApiRequest() {

    suspend fun login(email: String, password: String): AuthResponse {
        return apiRequest { api.loginUser(LoginRequest(email, password)) }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): AuthResponse {
        return apiRequest {
            api.registerUser(
                RegisterRequest(
                    firstName,
                    lastName,
                    email,
                    password
                )
            )
        }
    }

    suspend fun loadProfile(token: String) {
        accountRepository.loadAccounts(token)
        transactionRepository.loadTransactions(token)
    }

    suspend fun reloadProfile(token: String) {
        db.getAccountDao().deleteAccounts()
        accountRepository.loadAccounts(token)
        db.getTransactionDao().deleteTransactions()
        transactionRepository.loadTransactions(token)
    }

    suspend fun logout(token: String): AuthResponse {
        return apiRequest { api.logoutUser(token) }
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun deleteUser() {
        db.getUserDao().deleteUser()
        db.getAccountDao().deleteAccounts()
        db.getTransactionDao().deleteTransactions()
    }

    fun getUser() = db.getUserDao().fetch()
}