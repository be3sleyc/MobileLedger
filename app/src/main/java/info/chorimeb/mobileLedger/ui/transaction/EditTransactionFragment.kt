package info.chorimeb.mobileLedger.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.AccountName
import info.chorimeb.mobileLedger.data.db.entities.Transaction
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.databinding.FragmentEditTransactionBinding
import info.chorimeb.mobileLedger.ui.dialog.showDeleteTransactionDialog
import info.chorimeb.mobileLedger.ui.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_edit_transaction.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class EditTransactionFragment : Fragment(), TransactionListener, KodeinAware,
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    override val kodein: Kodein by kodein()

    private val factory: TransactionViewModelFactory by instance<TransactionViewModelFactory>()

    private lateinit var viewModel: TransactionViewModel

    private lateinit var transaction: Transaction
    private lateinit var user: User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<Transaction>("TRANSACTION")?.let {
            transaction = it
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
        val binding: FragmentEditTransactionBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_transaction, container, false)
        binding.transaction = transaction
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        viewModel.transactionListener = this

        viewModel.getAccountNames().observe(viewLifecycleOwner, Observer { names ->
            if (names != null) {
                val target = names.find { it.name == transaction.accountname }
                val editAccountName = names.indexOf(target)
                val nameAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                editAccountNameSpinner.adapter = nameAdapter
                editAccountNameSpinner.setSelection(editAccountName)
            }
        })

        editTransactionDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        editTransactionTime.setOnClickListener {
            val timepickerDialog = TimePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                false
            )
            timepickerDialog.show()
        }

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

        btnEditTransactionSave.setOnClickListener {
            val account = editAccountNameSpinner.selectedItem as AccountName
            val accountid = account.id
            val paydate = editTransactionDate.text.toString()
            val paytime = editTransactionTime.text.toString()
            val payee = editTransactionPayee.text.toString()
            val amount = editTransactionAmount.text.toString()
            val description = editTransactionDesc.text.toString()
            val category = if (editTransactionCategorySpinner.selectedItem.toString() == "other") {
                if (editTransactionCustomCategory.text.toString().isBlank()) ""
                else editTransactionCustomCategory.text.toString()
            } else editTransactionCategorySpinner.selectedItem.toString()
            viewModel.onSaveEdit(
                it,
                user.token!!,
                transaction.id!!,
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
        menu.findItem(R.id.action_edit_account).isVisible = false
        menu.findItem(R.id.action_add_account).isVisible = false
        menu.findItem(R.id.action_logout).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                showDeleteTransactionDialog(
                    requireContext(),
                    transaction.id!!,
                    user.token!!,
                    viewModel
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = requireContext().getString(R.string.pick_date, year, month + 1, dayOfMonth)
        editTransactionDate.text = date
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = requireContext().getString(R.string.pick_time, hourOfDay, minute)
        editTransactionTime.text = time
    }
}