package info.chorimeb.mobileLedger.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class AccountViewModelFactory(
    private val userRepository: UserRepository,
    private val repository: AccountRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(userRepository, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}