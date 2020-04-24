package info.chorimeb.mobileLedger.ui.transaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import info.chorimeb.mobileLedger.R
import kotlinx.android.synthetic.main.activity_transaction.*

class TransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        setSupportActionBar(transactionToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (findViewById<FragmentContainerView>(R.id.transactionFragmentContainer) != null) {

            if (savedInstanceState != null) {
                return
            }

            if (intent.getStringExtra("TYPE") == "edit") {
                supportActionBar?.title = "Edit Transaction"
                // inflate edit transaction frag with intent extras as arguments
                val editTransaction = EditTransactionFragment()
                editTransaction.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.transactionFragmentContainer, editTransaction).commit()
            } else {
                supportActionBar?.title = "New Transaction"
                // inflate newTransactionFragment
                val newTransaction = NewTransactionFragment()
                newTransaction.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.transactionFragmentContainer, newTransaction).commit()
            }
        }
    }
}
