package info.chorimeb.mobileLedger.ui.home.accounts

import com.xwray.groupie.databinding.BindableItem
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.databinding.RecyclerItemAccountBinding

class AccountItem(val account: Account) : BindableItem<RecyclerItemAccountBinding>() {
    override fun getLayout() = R.layout.recycler_item_account

    override fun bind(viewBinding: RecyclerItemAccountBinding, position: Int) {
        viewBinding.account = account
        viewBinding.accountTypeIcon.setImageResource(account.getTypeIcon())
    }
}