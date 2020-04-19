package info.chorimeb.mobileLedger.ui.transaction

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.User
import kotlinx.android.synthetic.main.fragment_new_transaction.*
import kotlinx.android.synthetic.main.recycler_item_transaction.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.Arrays.asList

class NewTransactionFragment : Fragment(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: TransactionViewModelFactory by instance<TransactionViewModelFactory>()

    private lateinit var viewModel: TransactionViewModel

    private lateinit var user: User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<User>("USER")?.let {
            user = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        viewModel.getAccountNames().observe(viewLifecycleOwner, Observer { names ->
            if (names != null) {
                val nameAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                newAccountNameSpinner.adapter = nameAdapter
            }
        })
        viewModel.getCategories().observe(viewLifecycleOwner, Observer { categories ->
            val transactionCategories = categories?.plus("other") ?: listOf("other")
            val categoryAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    transactionCategories
                )
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newTransactionCategorySpinner.adapter = categoryAdapter
            newTransactionCategorySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        newTransactionCustomCategory.visibility = View.GONE
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (transactionCategories[position] == "other") {
                            newTransactionCustomCategory.visibility = View.VISIBLE
                        } else {
                            newTransactionCustomCategory.visibility = View.GONE
                        }
                    }
                }
        })

    }
}