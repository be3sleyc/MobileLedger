package info.chorimeb.mobileLedger.ui.transaction

import android.view.View
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import info.chorimeb.mobileLedger.util.convertToUTCFormat

class TransactionViewModel(
    private val repository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var transactionListener: TransactionListener? = null

    fun getAccountNames() = repository.getAccountNames()
    fun getCategories() = repository.getCategories()

    fun onSaveEdit(
        view: View,
        token: String,
        id: Int,
        accountid: Int,
        paydate: String,
        paytime: String,
        payee: String,
        description: String,
        amount: String,
        category: String
    ) {
        if (view.id == R.id.btnEditTransactionSave) {
            println("UTC Format: ${convertToUTCFormat("$paydate $paytime")}")
            Coroutines.main {
                try {
                    val response = repository.editTransaction(
                        token,
                        id,
                        accountid,
                        convertToUTCFormat("$paydate $paytime")!!,
                        payee,
                        description,
                        amount.replace("$", ""),
                        category
                    )
                    response.message?.let {
                        Coroutines.io {
                            userRepository.reloadProfile(token)
                        }
                        transactionListener?.onSuccess(it)
                        return@main
                    }
                    transactionListener?.onFailure(response.message!!)
                } catch (e: ApiException) {
                    transactionListener?.onFailure(e.message!!)
                } catch (e: NoInternetConnectionException) {
                    transactionListener?.onFailure(e.message!!)
                }
            }
        }
    }

    fun onSaveNew(
        view: View,
        token: String,
        accountid: Int,
        paydate: String,
        paytime: String,
        payee: String,
        description: String,
        amount: String,
        category: String
    ) {
        if (view.id == R.id.btnNewTransactionSave) {
            println("UTC Format: ${convertToUTCFormat("$paydate $paytime")}")
            Coroutines.main {
                try {
                    val response = repository.newTransaction(
                        token, accountid,
                        amount,
                        convertToUTCFormat("$paydate $paytime")!!,
                        payee,
                        description,
                        category
                    )
                    response.message?.let {
                        Coroutines.io {
                            userRepository.reloadProfile(token)
                        }
                        transactionListener?.onSuccess(it)
                        return@main
                    }
                    transactionListener?.onFailure(response.message!!)
                } catch (e: ApiException) {
                    transactionListener?.onFailure(e.message!!)
                } catch (e: NoInternetConnectionException) {
                    transactionListener?.onFailure(e.message!!)
                }
            }
        }
    }

    fun deleteTransaction(token: String?, id: Int?) = Coroutines.main {
        try {
            if (token != null && id != null) {
                val response = repository.deleteTransaction(token, id)
                response.message?.let {
                    Coroutines.io {
                        userRepository.reloadProfile(token)
                    }
                    transactionListener?.onSuccess(it)
                    return@main
                }
                transactionListener?.onFailure(response.message!!)
            }
        } catch (e: ApiException) {
            transactionListener?.onFailure(e.message!!)
        } catch (e: NoInternetConnectionException) {
            transactionListener?.onFailure(e.message!!)
        }
    }
}
