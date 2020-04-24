package info.chorimeb.mobileLedger.ui.home.accounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import info.chorimeb.mobileLedger.util.lazyDeferred

class AccountsViewModel(
    private val userRepository: UserRepository,
    private val repository: AccountRepository
) : ViewModel() {

    fun getLoggedInUser() = userRepository.getUser()

    suspend fun getAccountList(): LiveData<List<Account>> {
        val account by lazyDeferred { repository.getAccounts() }
        return account.await()
    }

    fun logout(token: String) = Coroutines.io {
        try {
            val response = userRepository.logout(token)
            response.message?.let {
                println(it)
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
