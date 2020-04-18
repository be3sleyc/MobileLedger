package info.chorimeb.mobileLedger.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.SafeApiRequest
import info.chorimeb.mobileLedger.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val ONE_WEEK = 1

class TransactionRepository(private val api: ApiService, private val db: AppDatabase) :
    SafeApiRequest() {

    private val transactions = MutableLiveData<List<Transaction>>()

    init {
        transactions.observeForever {
            saveTransactions(it)
        }
    }

    suspend fun getTransactions(token: String): LiveData<List<Transaction>> {
        return withContext(Dispatchers.IO) {
            fetchTransactions(token)
            db.getTransactionDao().fetchTransactions()
        }
    }

    private suspend fun fetchTransactions(token: String) {
        if (isFetchNeeded()) {
            val response = apiRequest { api.getAllTransactions(token) }
            transactions.postValue(response.transactions)
        }
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        Coroutines.io {
            db.getTransactionDao().saveAllTransactions(transactions)
        }
    }

    private fun isFetchNeeded(): Boolean {
        return true
    }
}