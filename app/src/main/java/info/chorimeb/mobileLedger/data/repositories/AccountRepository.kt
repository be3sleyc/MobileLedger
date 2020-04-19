package info.chorimeb.mobileLedger.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.SafeApiRequest
import info.chorimeb.mobileLedger.data.network.requests.EditAccountRequest
import info.chorimeb.mobileLedger.data.network.requests.NewAccountRequest
import info.chorimeb.mobileLedger.data.network.responses.AccountResponse
import info.chorimeb.mobileLedger.util.Coroutines

class AccountRepository(private val api: ApiService, private val db: AppDatabase) :
    SafeApiRequest() {

    private val accounts = MutableLiveData<List<Account>>()

    init {
        accounts.observeForever {
            saveAccounts(it)
        }
    }

    suspend fun loadAccounts(token: String) = fetchAccounts(token)

    fun getAccounts(): LiveData<List<Account>> = db.getAccountDao().fetchAccounts()

    fun getTypes() = db.getAccountDao().fetchAccountTypes()

    suspend fun editAccount(
        token: String,
        id: Int,
        name: String,
        type: String,
        notes: String
    ): AccountResponse {
        return apiRequest { api.editAccount(token, id, EditAccountRequest(name, type, notes)) }
    }

    suspend fun newAccount(
        token: String,
        name: String,
        type: String,
        balance: String,
        notes: String
    ): AccountResponse {
        return apiRequest { api.addAccount(token, NewAccountRequest(name, type, balance, notes)) }
    }

    private suspend fun fetchAccounts(token: String) {
        val response = apiRequest { api.getAllAccounts(token) }
        accounts.postValue(response.accounts)
    }

    private fun saveAccounts(accounts: List<Account>) {
        Coroutines.io {
            db.getAccountDao().saveAllAccounts(accounts)
        }
    }
}