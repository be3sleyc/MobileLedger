package info.chorimeb.mobileLedger.ui.home.accounts

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.ui.account.AccountActivity
import info.chorimeb.mobileLedger.ui.auth.AuthListener
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

class AccountsFragment : Fragment(), AuthListener, KodeinAware {

    override val kodein by kodein()

    private val factory: AccountsViewModelFactory by instance<AccountsViewModelFactory>()

    private lateinit var viewModel: AccountsViewModel

    private var isFabOpen = false

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(AccountsViewModel::class.java)
        viewModel.authListener = this

        val fabOpen = AnimationUtils.loadAnimation(this.context, R.anim.fab_add_menu)
        val fabClose = AnimationUtils.loadAnimation(this.context, R.anim.fab_close_menu)
        val fabRotCW = AnimationUtils.loadAnimation(this.context, R.anim.rotate_clockwise)
        val fabRotCCW = AnimationUtils.loadAnimation(this.context, R.anim.rotate_counterclockwise)

        floatingActionButton.setOnClickListener {
            isFabOpen = if (isFabOpen) {
                fabaddaccount.startAnimation(fabClose)
                fabaddtransaction.startAnimation(fabClose)
                floatingActionButton.startAnimation(fabRotCW)
                false
            } else {
                fabaddaccount.startAnimation(fabOpen)
                fabaddtransaction.startAnimation(fabOpen)
                floatingActionButton.startAnimation(fabRotCCW)
                fabaddaccount.isClickable
                fabaddtransaction.isClickable
                true
            }

            fabaddtransaction.setOnClickListener {
                fabaddaccount.startAnimation(fabClose)
                fabaddtransaction.startAnimation(fabClose)
                floatingActionButton.startAnimation(fabRotCW)
                val intent = Intent(this.context, TransactionActivity::class.java)
                startActivity(intent)
                Toast.makeText(this.context, "Add Transaction Activity", Toast.LENGTH_SHORT).show()
            }

            fabaddaccount.setOnClickListener {
                fabaddaccount.startAnimation(fabClose)
                fabaddtransaction.startAnimation(fabClose)
                floatingActionButton.startAnimation(fabRotCW)
                val intent = Intent(this.context, AccountActivity::class.java)
                startActivity(intent)
                Toast.makeText(this.context, "Add Account Activity", Toast.LENGTH_SHORT).show()
            }
        }

        progressbarAccounts.show()
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                val token = user.token!!
                Coroutines.main {
                    viewModel.getAccountList(token).observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            bindUI(it)
                        }
                    })
                }
            }
        })
    }

    private fun bindUI(accountList: List<Account>) {
        progressbarAccounts.hide()
        initRecyclerView(accountList.toAccountItem())
    }

    private fun initRecyclerView(accountItems: List<AccountItem>) {
        val rAdapter = GroupAdapter<ViewHolder>().apply {
            setOnItemClickListener(onItemClick)
            addAll(accountItems)
        }

        accountRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            val topSpacingItemDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingItemDecoration)
            adapter = rAdapter
        }
    }

    private fun List<Account>.toAccountItem(): List<AccountItem> {
        return this.map {
            AccountItem(it)
        }
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        val accountItem = item as AccountItem
        val intent = Intent(this.context, AccountActivity::class.java)
        intent.putExtra("TYPE", "old")
        intent.putExtra("ACCOUNT", accountItem.account)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    override fun onStarted() {
        progressbarAccounts.show()
    }

    override fun onSuccess(response: Any) {
        progressbarAccounts.hide()
    }

    override fun onFailure(message: String) {
        progressbarAccounts.hide()
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }
}