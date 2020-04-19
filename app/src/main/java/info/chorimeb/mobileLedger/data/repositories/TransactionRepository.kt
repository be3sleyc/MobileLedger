package info.chorimeb.mobileLedger.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.SafeApiRequest
import info.chorimeb.mobileLedger.util.Coroutines

class TransactionRepository(private val api: ApiService, private val db: AppDatabase) :
    SafeApiRequest() {

    private val transactions = MutableLiveData<List<Transaction>>()

    init {
        transactions.observeForever {
            saveTransactions(it)
        }
    }

    suspend fun loadTransactions(token: String) = fetchTransactions(token)

    fun getTransactions(): LiveData<List<Transaction>> = db.getTransactionDao().fetchTransactions()

    fun getAccountNames() = db.getAccountDao().fetchAccountNames()

    fun getCategories() = db.getTransactionDao().fetchCategories()

    private suspend fun fetchTransactions(token: String) {
        val response = apiRequest { api.getAllTransactions(token) }
        transactions.postValue(response.transactions)
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        Coroutines.io {
            db.getTransactionDao().saveAllTransactions(transactions)
        }
    }
}