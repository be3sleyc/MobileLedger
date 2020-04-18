package info.chorimeb.mobileLedger.ui.home.transactions

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
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.ui.account.AccountActivity
import info.chorimeb.mobileLedger.ui.auth.AuthListener
import info.chorimeb.mobileLedger.ui.auth.LoginActivity
import info.chorimeb.mobileLedger.ui.transaction.TransactionActivity
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.TopSpacingItemDecoration
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import kotlinx.android.synthetic.main.component_fab.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TransactionsFragment : Fragment(), AuthListener, KodeinAware {

    override val kodein by kodein()

    private val factory: TransactionsViewModelFactory by instance<TransactionsViewModelFactory>()

    private lateinit var viewModel: TransactionsViewModel

    private var isFabOpen = false

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TransactionsViewModel::class.java)
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
                val intent = Intent(this.context, TransactionActivity::class.java)
                startActivity(intent)
                Toast.makeText(this.context, "Add Transaction Activity", Toast.LENGTH_SHORT).show()
            }

            fabaddaccount.setOnClickListener {
                val intent = Intent(this.context, AccountActivity::class.java)
                startActivity(intent)
                Toast.makeText(this.context, "Add Account Activity", Toast.LENGTH_SHORT).show()
            }
        }

        progressbarTransactions.show()
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                val token = user.token!!
                Coroutines.main {
                    viewModel.getTransactionList(token).observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            bindUI(it)
                        }
                    })
                }
            }
        })
    }

    private fun bindUI(transactionList: List<Transaction>) {
        progressbarTransactions.hide()
        initRecyclerView(transactionList.toTransactionItem())
    }

    private fun initRecyclerView(transactionItems: List<TransactionItem>) {
        val rAdapter = GroupAdapter<ViewHolder>().apply {
            setOnItemClickListener(onItemClick)
            addAll(transactionItems)
        }

        transactionRecycler.apply {
            setHasFixedSize(true)
            val topSpacingItemDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingItemDecoration)
            adapter = rAdapter
        }
    }

    private fun List<Transaction>.toTransactionItem(): List<TransactionItem> {
        return this.map {
            TransactionItem(it)
        }
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        Toast.makeText(this.context, item.toString(), Toast.LENGTH_LONG).show()
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
        progressbarTransactions.show()
    }

    override fun onSuccess(response: Any) {
        progressbarTransactions.hide()
    }

    override fun onFailure(message: String) {
        progressbarTransactions.hide()
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }
}
