package info.chorimeb.mobileLedger.ui.account

import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class AccountViewModel(
    private val userRepository: UserRepository,
    private val repository: AccountRepository
) : ViewModel() {
    // TODO: Implement the ViewModel
}
