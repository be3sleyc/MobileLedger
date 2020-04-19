package info.chorimeb.mobileLedger.ui.home.transactions

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import info.chorimeb.mobileLedger.util.lazyDeferred

class TransactionsViewModel(
    private val userRepository: UserRepository,
    private val repository: TransactionRepository
) : ViewModel() {

    fun getLoggedInUser() = userRepository.getUser()

    suspend fun getTransactionList(): LiveData<List<Transaction>> {
        val transaction by lazyDeferred { repository.getTransactions() }
        return transaction.await()
    }

    fun logout(token: String) = Coroutines.io {
        try {
            val response = userRepository.logout(token)
            response.message?.let {
                userRepository.deleteUser()
                return@io
            }
            println(response.message!!)
        } catch (e: ApiException) {
            println(e.message!!)
        } catch (e: NoInternetConnectionException) {
            println(e.message!!)
        }
    }
}
