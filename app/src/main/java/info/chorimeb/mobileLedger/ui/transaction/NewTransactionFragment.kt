package info.chorimeb.mobileLedger.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.db.entities.AccountName
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.ui.home.HomeActivity
import info.chorimeb.mobileLedger.util.getCurrentDateTime
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import info.chorimeb.mobileLedger.util.toString
import kotlinx.android.synthetic.main.fragment_new_transaction.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class NewTransactionFragment : Fragment(), TransactionListener, KodeinAware,
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    override val kodein: Kodein by kodein()

    private val factory: TransactionViewModelFactory by instance<TransactionViewModelFactory>()

    private lateinit var viewModel: TransactionViewModel

    private lateinit var user: User
    private lateinit var account: AccountName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<User>("USER")?.let {
            user = it
        }
        arguments?.getParcelable<Account>("ACCOUNT")?.let {
            account = AccountName(it.id!!, it.name!!, it.isdeleted)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        viewModel.transactionListener = this

        newTransactionDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        newTransactionTime.setOnClickListener {
            val timepickerDialog = TimePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                false
            )
            timepickerDialog.show()
        }

        viewModel.getAccountNames().observe(viewLifecycleOwner, Observer { names ->
            if (names != null) {
                val nameAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                newAccountNameSpinner.adapter = nameAdapter
                if (::account.isInitialized) {
                    newAccountNameSpinner.setSelection(names.indexOf(account))
                }
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
            progressbarNewTransaction.show()
            val account: AccountName = newAccountNameSpinner.selectedItem as AccountName
            val accountid: Int = account.id
            val paydate = if (newTransactionDate.text.toString() == "")
                getCurrentDateTime().toString("yyyy-MM-dd") else newTransactionDate.text.toString()
            val paytime = if (newTransactionTime.text.toString() == "")
                getCurrentDateTime().toString("HH:mm") else newTransactionTime.text.toString()
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
        progressbarNewTransaction.hide()
        Toast.makeText(activity, response as String, Toast.LENGTH_LONG).show()
        Intent(this.context, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailure(message: String) {
        progressbarNewTransaction.hide()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = requireContext().getString(R.string.pick_date, year, month + 1, dayOfMonth)
        newTransactionDate.text = date
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = requireContext().getString(R.string.pick_time, hourOfDay, minute)
        newTransactionTime.text = time
    }
}