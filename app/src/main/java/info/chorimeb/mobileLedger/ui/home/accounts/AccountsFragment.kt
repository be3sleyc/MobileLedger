package info.chorimeb.mobileLedger.ui.home.accounts

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.ui.account.AccountActivity
import info.chorimeb.mobileLedger.ui.auth.LoginActivity
import info.chorimeb.mobileLedger.ui.transaction.TransactionActivity
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.TopSpacingItemDecoration
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import kotlinx.android.synthetic.main.component_fab.*
import kotlinx.android.synthetic.main.fragment_accounts.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AccountsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: AccountsViewModelFactory by instance<AccountsViewModelFactory>()

    private lateinit var viewModel: AccountsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(AccountsViewModel::class.java)

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {

                floatingActionButton.setOnClickListener {
                    val intent = Intent(this.context, TransactionActivity::class.java)
                    intent.putExtra("TYPE", "new")
                    intent.putExtra("USER", user)
                    startActivity(intent)
                }

                progressbarAccounts.show()

                Coroutines.main {
                    viewModel.getAccountList().observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            bindUI(user, it)
                        }
                    })
                }
            }
        })
    }

    private fun bindUI(user: User, accountList: List<Account>) {
        progressbarAccounts.hide()
        initRecyclerView(user, accountList.toAccountItem())
    }

    private fun initRecyclerView(user: User, accountItems: List<AccountItem>) {
        val rAdapter = GroupAdapter<ViewHolder>().apply {
            setOnItemClickListener { item, _ ->
                progressbarAccounts.visibility = View.VISIBLE
                val accountItem = item as AccountItem
                val intent = Intent(context, AccountActivity::class.java)
                intent.putExtra("TYPE", "view")
                intent.putExtra("ACCOUNT", accountItem.account)
                intent.putExtra("USER", user)
                progressbarAccounts.visibility = View.GONE
                startActivity(intent)
            }
            addAll(accountItems)
        }

        accountRecycler.apply {
            setHasFixedSize(true)
            val topSpacingItemDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingItemDecoration)
            layoutManager = LinearLayoutManager(this.context)
            adapter = rAdapter
        }
    }

    private fun List<Account>.toAccountItem(): List<AccountItem> {
        return this.map {
            AccountItem(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
        menu.findItem(R.id.action_delete).isVisible = false
        menu.findItem(R.id.action_edit_account).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_account -> {
                viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                    if (user != null) {
                        val intent = Intent(this.context, AccountActivity::class.java)
                        intent.putExtra("TYPE", "new")
                        intent.putExtra("USER", user)
                        startActivity(intent)
                    }
                })
                true
            }
            R.id.action_logout -> {
                viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                    if (user != null) {
                        user.token?.let { it -> viewModel.logout(it) }
                        Intent(this.context, LoginActivity::class.java).also {
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        }
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
