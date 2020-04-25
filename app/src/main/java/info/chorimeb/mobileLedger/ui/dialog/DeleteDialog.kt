package info.chorimeb.mobileLedger.ui.dialog

import android.content.Context
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.ui.account.AccountViewModel
import info.chorimeb.mobileLedger.ui.transaction.TransactionViewModel

fun showDeleteAccountDialog(context: Context, id: Int, token: String, viewModel: AccountViewModel) {
    val dialog = MaterialDialog(context)
        .noAutoDismiss()
        .customView(R.layout.dialog_delete_account)

    dialog.findViewById<TextView>(R.id.deleteDialogPositive).setOnClickListener {
        viewModel.deleteAccount(token, id)
        dialog.dismiss()
    }

    dialog.findViewById<TextView>(R.id.deleteDialogNegative).setOnClickListener {
        dialog.dismiss()
    }

    dialog.show { }
}

fun showDeleteTransactionDialog(
    context: Context,
    id: Int,
    token: String,
    viewModel: TransactionViewModel
) {
    val dialog = MaterialDialog(context)
        .noAutoDismiss()
        .customView(R.layout.dialog_delete_transaction)

    dialog.findViewById<TextView>(R.id.deleteDialogPositive).setOnClickListener {
        viewModel.deleteTransaction(token, id)
        dialog.dismiss()
    }

    dialog.findViewById<TextView>(R.id.deleteDialogNegative).setOnClickListener {
        dialog.dismiss()
    }

    dialog.show { }
}