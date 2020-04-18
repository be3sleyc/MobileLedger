package info.chorimeb.mobileLedger.ui.home.accounts

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.ui.auth.AuthListener
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import info.chorimeb.mobileLedger.util.lazyDeferred

class AccountsViewModel(
    private val userRepository: UserRepository,
    private val repository: AccountRepository
) : ViewModel() {

    var authListener: AuthListener? = null

    fun getLoggedInUser() = userRepository.getUser()

    suspend fun getAccountList(token: String): LiveData<List<Account>> {
        val account by lazyDeferred { repository.getAccounts(token) }
        return account.await()
    }

    fun showAccount(view: View) {

    }

    fun logout(token: String) = Coroutines.io {
        try {
            val response = userRepository.logout(token)
            response.message?.let {
                userRepository.deleteUser()
                authListener?.onSuccess(it)
                return@io
            }
            authListener?.onFailure(response.message!!)
        } catch (e: ApiException) {
            authListener?.onFailure(e.message!!)
        } catch (e: NoInternetConnectionException) {
            authListener?.onFailure(e.message!!)
        }
    }

}
