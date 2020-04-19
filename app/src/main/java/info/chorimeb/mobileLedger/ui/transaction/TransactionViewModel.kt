package info.chorimeb.mobileLedger.ui.transaction

import android.view.View
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class TransactionViewModel(
    private val userRepository: UserRepository,
    private val repository: TransactionRepository
) : ViewModel() {

    fun getAccountNames() = repository.getAccountNames()
    fun getCategories() = repository.getCategories()

    fun onClickSaveTransaction(view: View) {

    }

    fun onClickAddTransaction(view: View) {

    }
}
