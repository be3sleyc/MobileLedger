package info.chorimeb.mobileLedger.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.SafeApiRequest
import info.chorimeb.mobileLedger.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val ONE_WEEK = 1

class AccountRepository(private val api: ApiService, private val db: AppDatabase) :
    SafeApiRequest() {

    private val accounts = MutableLiveData<List<Account>>()

    init {
        accounts.observeForever {
            saveAccounts(it)
        }
    }

    suspend fun getAccounts(token: String): LiveData<List<Account>> {
        return withContext(Dispatchers.IO) {
            fetchAccounts(token)
            db.getAccountDao().fetchAccounts()
        }
    }

    private suspend fun fetchAccounts(token: String) {
        if (isFetchNeeded()) {
            val response = apiRequest { api.getAllAccounts(token) }
            accounts.postValue(response.accounts)
        }
    }

    private fun saveAccounts(accounts: List<Account>) {
        Coroutines.io {
            db.getAccountDao().saveAllAccounts(accounts)
        }
    }

    private fun isFetchNeeded(): Boolean {
        return true
    }

}