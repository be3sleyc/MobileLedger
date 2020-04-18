package info.chorimeb.mobileLedger.ui.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.databinding.FragmentEditTransactionBinding
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditTransactionFragment : Fragment(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: TransactionViewModelFactory by instance<TransactionViewModelFactory>()

    private lateinit var viewModel: TransactionViewModel

    private lateinit var transaction: Transaction

    override fun onAttach(context: Context) {
        println("Attach!")
        super.onAttach(context)
        arguments?.getParcelable<Transaction>("TRANSACTION")?.let {
            transaction = it
            println("Transaction: $transaction")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("CreateView!")
        val binding: FragmentEditTransactionBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_transaction, container, false)
        binding.transaction = transaction
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("view created!")
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)
    }
}