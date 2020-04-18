package info.chorimeb.mobileLedger.ui.home.transactions

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.ui.auth.AuthListener
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import info.chorimeb.mobileLedger.util.lazyDeferred

class TransactionsViewModel(
    private val userRepository: UserRepository,
    private val repository: TransactionRepository
) : ViewModel() {

    var authListener: AuthListener? = null

    fun getLoggedInUser() = userRepository.getUser()

    suspend fun getTransactionList(token: String): LiveData<List<Transaction>> {
        val transaction by lazyDeferred { repository.getTransactions(token) }
        return transaction.await()
    }

    fun showTransaction(view: View) {

    }

    fun logout(token: String) = Coroutines.main {
        try {
            val response = userRepository.logout(token)
            response.message?.let {
                authListener?.onSuccess(it)
                userRepository.deleteUser()
                return@main
            }
            authListener?.onFailure(response.message!!)
        } catch (e: ApiException) {
            authListener?.onFailure(e.message!!)
        } catch (e: NoInternetConnectionException) {
            authListener?.onFailure(e.message!!)
        }
    }
}
