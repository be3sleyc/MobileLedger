package info.chorimeb.mobileLedger.ui.account

import android.view.View
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException

class AccountViewModel(
    private val repository: AccountRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var accountListener: AccountListener? = null

    fun getTypes() = repository.getTypes()

    fun onSaveEdit(view: View, token: String, id: Int, name: String, type: String, notes: String) {
        if (view.id == R.id.btnEditAccountSave) {
            Coroutines.main {
                try {
                    val response = repository.editAccount(token, id, name, type, notes)
                    response.message?.let {
                        Coroutines.io {
                            userRepository.reloadProfile(token)
                        }
                        accountListener?.onSuccess(it)
                        return@main
                    }
                    accountListener?.onFailure(response.message!!)
                } catch (e: ApiException) {
                    accountListener?.onFailure(e.message!!)
                } catch (e: NoInternetConnectionException) {
                    accountListener?.onFailure(e.message!!)
                }
            }
        }
    }

    fun onSaveNew(
        view: View,
        token: String,
        name: String,
        type: String,
        balance: String,
        notes: String
    ) {
        if (view.id == R.id.btnNewAccountSave) {
            Coroutines.main {
                try {
                    val response = repository.newAccount(token, name, type, balance, notes)
                    response.message?.let {
                        Coroutines.io {
                            userRepository.reloadProfile(token)
                        }
                        accountListener?.onSuccess(it)
                        return@main
                    }
                    accountListener?.onFailure(response.message!!)
                } catch (e: ApiException) {
                    accountListener?.onFailure(e.message!!)
                } catch (e: NoInternetConnectionException) {
                    accountListener?.onFailure(e.message!!)
                }
            }
        }
    }
}
