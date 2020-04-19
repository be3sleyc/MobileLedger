package info.chorimeb.mobileLedger.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.SafeApiRequest
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

//    suspend fun addAccount(
//        token: String,
//        name: String,
//        type: String,
//        balance: Double,
//        notes: String
//    ): Boolean {
//        val res =  apiRequest { api.addAccount(token, NewAccountRequest(name, type, balance, notes)) }
//        if(res.message == "" ) {
//
//        }
//    }

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