package info.chorimeb.mobileLedger.ui.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import info.chorimeb.mobileLedger.R
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        setSupportActionBar(accountToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (findViewById<FragmentContainerView>(R.id.accountFragmentContainer) != null) {

            if (savedInstanceState != null) {
                return
            }

            if (intent.getStringExtra("TYPE") == "view") {
                supportActionBar?.title = "Account"
                // inflate view account frag with intent extras
                val viewAccount = AccountViewFragment()
                viewAccount.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.accountFragmentContainer, viewAccount).commit()
            } else if (intent.getStringExtra("TYPE") == "edit") {
                supportActionBar?.title = "Edit Account"
                // inflate view account frag with intent extras
                val editAccount = EditAccountFragment()
                editAccount.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.accountFragmentContainer, editAccount).commit()
            } else {
                supportActionBar?.title = "New Account"
                // inflate new account fragment
                val newAccount = NewAccountFragment()
                newAccount.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.accountFragmentContainer, newAccount).commit()
            }
        }
    }
}