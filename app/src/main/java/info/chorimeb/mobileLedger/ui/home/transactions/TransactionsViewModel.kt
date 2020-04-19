package info.chorimeb.mobileLedger.ui.home.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.ui.home.HomeListener
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import info.chorimeb.mobileLedger.util.lazyDeferred

class TransactionsViewModel(
    private val userRepository: UserRepository,
    private val repository: TransactionRepository
) : ViewModel() {

    var homeListener: HomeListener? = null

    fun getLoggedInUser() = userRepository.getUser()

    suspend fun getTransactionList(): LiveData<List<Transaction>> {
        val transaction by lazyDeferred { repository.getTransactions() }
        return transaction.await()
    }

    fun logout(token: String) = Coroutines.main {
        try {
            val response = userRepository.logout(token)
            response.message?.let {
                homeListener?.onSuccess(it)
                userRepository.deleteUser()
                return@main
            }
            homeListener?.onFailure(response.message!!)
        } catch (e: ApiException) {
            homeListener?.onFailure(e.message!!)
        } catch (e: NoInternetConnectionException) {
            homeListener?.onFailure(e.message!!)
        }
    }
}
