package info.chorimeb.mobileLedger.ui.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import info.chorimeb.mobileLedger.R
import kotlinx.android.synthetic.main.activity_transaction.*

class TransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        setSupportActionBar(transactionToolbar)

        val navController = Navigation.findNavController(this, R.id.TransactionFragment)

    }
}
