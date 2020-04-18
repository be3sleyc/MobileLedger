package info.chorimeb.mobileLedger.ui.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

            if (intent.getStringExtra("TYPE") == "old") {
                println("setting title")
                supportActionBar?.title = "Edit Transaction"
                // inflate edit transaction frag with intent extras as arguments
                println("setting fragment")
                val editTransaction = EditTransactionFragment()
                println("inflating fragment")
                editTransaction.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.transactionFragmentContainer, editTransaction).commit()
            } else {
                println("setting title")
                supportActionBar?.title = "New Transaction"
                // inflate newTransactionFragment
                println("setting fragment")
                val newTransaction = NewTransactionFragment()
                println("inflating fragment")
                supportFragmentManager.beginTransaction()
                    .add(R.id.transactionFragmentContainer, newTransaction).commit()
            }
        }
    }
}
