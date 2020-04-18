package info.chorimeb.mobileLedger.ui.home.transactions

import com.xwray.groupie.databinding.BindableItem
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.databinding.RecyclerItemTransactionBinding

class TransactionItem(private val transaction: Transaction): BindableItem<RecyclerItemTransactionBinding>() {
    override fun getLayout() = R.layout.recycler_item_transaction

    override fun bind(viewBinding: RecyclerItemTransactionBinding, position: Int) {
        viewBinding.transaction = transaction
    }

}