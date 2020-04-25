package info.chorimeb.mobileLedger.ui.account

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
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.ui.home.HomeActivity
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import kotlinx.android.synthetic.main.fragment_new_account.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NewAccountFragment : Fragment(), AccountListener, KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()

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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_new_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        viewModel.accountListener = this

        viewModel.getTypes().observe(viewLifecycleOwner, Observer { types ->
            val accountTypes = types?.plus("other") ?: listOf("other")
            val typeAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    accountTypes
                )
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newAccountTypeSpinner.adapter = typeAdapter
            newAccountTypeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        newAccountCustomType.visibility = View.GONE
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (accountTypes[position] == "other") {
                            newAccountCustomType.visibility = View.VISIBLE
                        } else {
                            newAccountCustomType.visibility = View.GONE
                        }
                    }
                }
        })

        btnNewAccountSave.setOnClickListener {
            progressbarNewAccount.show()
            val name = newAccountName.text.toString()
            if (name.isBlank()) {
                onFailure(getString(R.string.error_blank_account_name))
                return@setOnClickListener
            }
            val type = if (newAccountTypeSpinner.selectedItem.toString() == "other") {
                if (newAccountCustomType.text.toString().isBlank()) ""
                else newAccountCustomType.text.toString()
            } else newAccountTypeSpinner.selectedItem.toString()
            val balance = newAccountBalance.text.toString()
            val notes = newAccountNotes.text.toString()
            viewModel.onSaveNew(it, user.token!!, name, type, balance, notes)
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess(response: Any) {
        progressbarNewAccount.hide()
        Toast.makeText(context, response as String, Toast.LENGTH_LONG).show()
        Intent(this.context, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailure(message: String) {
        progressbarNewAccount.hide()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}