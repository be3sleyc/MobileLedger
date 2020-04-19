package info.chorimeb.mobileLedger.ui.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.databinding.FragmentEditTransactionBinding
import kotlinx.android.synthetic.main.fragment_edit_transaction.*
import kotlinx.android.synthetic.main.fragment_edit_transaction.editTransactionCategorySpinner
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditTransactionFragment : Fragment(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: TransactionViewModelFactory by instance<TransactionViewModelFactory>()

    private lateinit var viewModel: TransactionViewModel

    private lateinit var transaction: Transaction
    private lateinit var user: User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<Transaction>("TRANSACTION")?.let {
            transaction = it
            println("Transaction: $transaction")
        }
        arguments?.getParcelable<User>("USER")?.let {
            user = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEditTransactionBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_transaction, container, false)
        binding.transaction = transaction
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        viewModel.getAccountNames().observe(viewLifecycleOwner, Observer { names ->
            if (names != null) {
                val editAccountName = names.indexOf(transaction.accountname)
                val nameAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                editAccountNameSpinner.adapter = nameAdapter
                editAccountNameSpinner.setSelection(editAccountName)
            }
        })
        viewModel.getCategories().observe(viewLifecycleOwner, Observer { categories ->
            val transactionCategories = categories?.plus("other") ?: listOf("other")
            val editCategory = categories.indexOf(transaction.category)
            val categoryAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    transactionCategories
                )
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editTransactionCategorySpinner.adapter = categoryAdapter
            editTransactionCategorySpinner.setSelection(editCategory)
            editTransactionCategorySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        editTransactionCustomCategory.visibility = View.GONE
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (transactionCategories[position] == "other") {
                            editTransactionCustomCategory.visibility = View.VISIBLE
                        } else {
                            editTransactionCustomCategory.visibility = View.GONE
                        }
                    }
                }
        })
    }
}