package info.chorimeb.mobileLedger.ui.account

import android.view.View
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository

class AccountViewModel(
    private val userRepository: UserRepository,
    private val repository: AccountRepository
) : ViewModel() {

    var accountListener: AccountListener? = null

    fun getTypes() = repository.getTypes()
    
}
