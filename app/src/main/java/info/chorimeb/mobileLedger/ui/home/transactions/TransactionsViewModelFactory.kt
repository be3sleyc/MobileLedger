package info.chorimeb.mobileLedger.ui.home.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class TransactionsViewModelFactory(
    private val userRepository: UserRepository,
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            return TransactionsViewModel(userRepository, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}