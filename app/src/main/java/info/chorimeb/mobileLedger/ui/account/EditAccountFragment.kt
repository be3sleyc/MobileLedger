package info.chorimeb.mobileLedger.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.db.entities.User
import info.chorimeb.mobileLedger.databinding.FragmentEditAccountBinding
import info.chorimeb.mobileLedger.ui.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_edit_account.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditAccountFragment : Fragment(), AccountListener, KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()

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
        val binding: FragmentEditAccountBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_account, container, false)
        binding.account = account
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        viewModel.accountListener = this

        viewModel.getTypes().observe(viewLifecycleOwner, Observer { types ->
            val accountTypes = types?.plus("other") ?: listOf("other")
            val accountType = types.indexOf(account.type)
            val typeAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    accountTypes
                )
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editAccountTypeSpinner.adapter = typeAdapter
            editAccountTypeSpinner.setSelection(accountType)
            editAccountTypeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        editAccountCustomType.visibility = View.GONE
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (accountTypes[position] == "other") {
                            editAccountCustomType.visibility = View.VISIBLE
                        } else {
                            editAccountCustomType.visibility = View.GONE
                        }
                    }
                }
        })

        btnEditAccountSave.setOnClickListener {
            val name = editAccountName.text.toString()
            val type =
                if (editAccountTypeSpinner.selectedItem.toString() == "other") {
                    if (editAccountCustomType.text.toString().isBlank()) ""
                    else editAccountCustomType.text.toString()
                } else editAccountTypeSpinner.selectedItem.toString()
            val notes = editAccountNotes.text.toString()
            viewModel.onSaveEdit(it, user.token!!, account.id!!, name, type, notes)
        }

    }

    override fun onStarted() {
    }

    override fun onSuccess(response: Any) {
        Toast.makeText(activity, response as String, Toast.LENGTH_LONG).show()
        Intent(this.context, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }    }

    override fun onFailure(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}