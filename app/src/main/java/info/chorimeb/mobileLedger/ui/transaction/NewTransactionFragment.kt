package info.chorimeb.mobileLedger.ui.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.AccountName
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.ui.home.HomeActivity
import info.chorimeb.mobileLedger.util.getCurrentDateTime
import info.chorimeb.mobileLedger.util.toString
import kotlinx.android.synthetic.main.fragment_new_transaction.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NewTransactionFragment : Fragment(), TransactionListener, KodeinAware {

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

        viewModel.transactionListener = this

        newTransactionDate.setText(getCurrentDateTime().toString("yyyy-MM-dd"))
        newTransactionTime.setText(getCurrentDateTime().toString("HH:mm"))

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

        btnNewTransactionSave.setOnClickListener {
            val account: AccountName  = newAccountNameSpinner.selectedItem as AccountName
            val accountid: Int = account.id
            val paydate = newTransactionDate.text.toString()
            val paytime = newTransactionTime.text.toString()
            val payee = newTransactionPayee.text.toString()
            val description = newTransactionDesc.text.toString()
            val amount = newTransactionAmount.text.toString()
            val category = if (newTransactionCategorySpinner.selectedItem.toString() == "other") {
                if (newTransactionCustomCategory.text.toString().isBlank()) ""
                else newTransactionCustomCategory.text.toString()
            } else newTransactionCategorySpinner.selectedItem.toString()
            viewModel.onSaveNew(
                it,
                user.token!!,
                accountid,
                paydate,
                paytime,
                payee,
                description,
                amount,
                category
            )
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess(response: Any) {
        Toast.makeText(activity, response as String, Toast.LENGTH_LONG).show()
        Intent(this.context, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailure(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}