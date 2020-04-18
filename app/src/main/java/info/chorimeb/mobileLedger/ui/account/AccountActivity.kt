package info.chorimeb.mobileLedger.ui.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        setSupportActionBar(accountToolbar)

        val navController = Navigation.findNavController(this, R.id.accountFragment)

        supportActionBar?.title = "Edit Account"
        if (intent.getStringExtra("TYPE") == "old") {
            val account = intent.getParcelableExtra<Account>("ACCOUNT")

        } else {
            supportActionBar?.title = "New Account"
        }
    }
}
