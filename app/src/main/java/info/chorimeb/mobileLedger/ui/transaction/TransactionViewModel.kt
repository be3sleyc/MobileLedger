package info.chorimeb.mobileLedger.ui.transaction

import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class TransactionViewModel(
    private val userRepository: UserRepository,
    private val repository: TransactionRepository
) : ViewModel() {
    // TODO: Implement the ViewModel
}
