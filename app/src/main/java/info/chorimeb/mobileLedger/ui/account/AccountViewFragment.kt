package info.chorimeb.mobileLedger.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.databinding.FragmentAccountViewBinding
import info.chorimeb.mobileLedger.ui.auth.LoginActivity
import info.chorimeb.mobileLedger.ui.home.transactions.TransactionItem
import info.chorimeb.mobileLedger.ui.transaction.TransactionActivity
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.TopSpacingItemDecoration
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import kotlinx.android.synthetic.main.fragment_account_view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AccountViewFragment : Fragment(), AccountListener, KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()

    private lateinit var viewModel: AccountViewModel

    private lateinit var account: Account
    private lateinit var user: User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<Account>("ACCOUNT")?.let {
            account = it
        }
        arguments?.getParcelable<User>("USER")?.let {
            user = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding: FragmentAccountViewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_account_view, container, false)
        binding.account = account
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        viewModel.accountListener = this

        floatingTransactionButton.setOnClickListener {
            val intent = Intent(this.context, TransactionActivity::class.java)
            intent.putExtra("TYPE", "new")
            intent.putExtra("ACCOUNT", account)
            intent.putExtra("USER", user)
            startActivity(intent)
        }

        progressbarAccountTransactions.show()
        if (account.name != null) {
            Coroutines.main {
                viewModel.getAccountTransactionList(account.name as String)
                    .observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            if (it.isNotEmpty()) {
                                bindUI(it)
                            } else {
                                progressbarAccountTransactions.hide()
                                accountTransactionsRecycler.visibility = View.GONE
                                blankAccountTransactions.visibility = View.VISIBLE
                            }
                        }
                    })
            }
        }
    }

    private fun bindUI(transactionList: List<Transaction>) {
        initRecyclerView(transactionList.toTransactionItem())
    }

    private fun initRecyclerView(transactionItems: List<TransactionItem>) {
        val rAdapter = GroupAdapter<ViewHolder>().apply {
            setOnItemClickListener { item, _ ->
                val transactionItem = item as TransactionItem
                val intent = Intent(context, TransactionActivity::class.java)
                intent.putExtra("TYPE", "edit")
                intent.putExtra("TRANSACTION", transactionItem.transaction)
                intent.putExtra("USER", user)
                startActivity(intent)
            }
            addAll(transactionItems)
        }
        progressbarAccountTransactions.hide()
        accountTransactionsRecycler.apply {
            setHasFixedSize(true)
            val topSpacingItemDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingItemDecoration)
            layoutManager = LinearLayoutManager(this.context)
            adapter = rAdapter
        }
    }

    private fun List<Transaction>.toTransactionItem(): List<TransactionItem> {
        return this.map {
            TransactionItem(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
        menu.findItem(R.id.action_delete).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_account -> {
                val intent = Intent(this.context, AccountActivity::class.java)
                intent.putExtra("TYPE", "edit")
                intent.putExtra("ACCOUNT", account)
                intent.putExtra("USER", user)
                startActivity(intent)

                true
            }
            R.id.action_add_account -> {
                val intent = Intent(this.context, AccountActivity::class.java)
                intent.putExtra("TYPE", "new")
                intent.putExtra("USER", user)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                user.token?.let { it -> viewModel.logout(it) }
                Intent(this.context, LoginActivity::class.java).also {
                    it.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onStarted() {
        TODO("Not yet implemented")
    }

    override fun onSuccess(response: Any) {
        TODO("Not yet implemented")
    }

    override fun onFailure(message: String) {
        TODO("Not yet implemented")
    }
}
