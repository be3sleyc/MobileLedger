package info.chorimeb.mobileLedger.ui.home.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class AccountsViewModelFactory(
    private val userRepository: UserRepository,
    private val repository: AccountRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountsViewModel::class.java)) {
            return AccountsViewModel(userRepository, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}