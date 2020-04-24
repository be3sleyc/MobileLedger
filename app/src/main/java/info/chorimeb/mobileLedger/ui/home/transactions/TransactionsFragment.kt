package info.chorimeb.mobileLedger.ui.home.transactions

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
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.ui.account.AccountActivity
import info.chorimeb.mobileLedger.ui.auth.LoginActivity
import info.chorimeb.mobileLedger.ui.transaction.TransactionActivity
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.component_fab.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TransactionsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: TransactionsViewModelFactory by instance<TransactionsViewModelFactory>()

    private lateinit var viewModel: TransactionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(TransactionsViewModel::class.java)

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {

                floatingActionButton.setOnClickListener {
                    val intent = Intent(this.context, TransactionActivity::class.java)
                    intent.putExtra("TYPE", "new")
                    intent.putExtra("USER", user)
                    startActivity(intent)
                }

                Coroutines.main {
                    viewModel.getTransactionList().observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            bindUI(user, it)
                        }
                    })
                }
            }
        })

    }

    private fun bindUI(user: User, transactionList: List<Transaction>) {
        initRecyclerView(user, transactionList.toTransactionItem())
    }

    private fun initRecyclerView(user: User, transactionItems: List<TransactionItem>) {
        val rAdapter = GroupAdapter<ViewHolder>().apply {
            setOnItemClickListener { item, _ ->
                progressbarTransactions.visibility = View.VISIBLE
                val transactionItem = item as TransactionItem
                val intent = Intent(context, TransactionActivity::class.java)
                intent.putExtra("TYPE", "edit")
                intent.putExtra("TRANSACTION", transactionItem.transaction)
                intent.putExtra("USER", user)
                progressbarTransactions.visibility = View.GONE
                startActivity(intent)
            }
            addAll(transactionItems)
        }

        transactionRecycler.apply {
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
