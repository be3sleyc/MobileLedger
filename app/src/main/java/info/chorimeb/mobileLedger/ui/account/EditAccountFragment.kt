package info.chorimeb.mobileLedger.ui.account

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.databinding.FragmentEditAccountBinding
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditAccountFragment : Fragment(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()

    private lateinit var viewModel: AccountViewModel

    private lateinit var account: Account

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<Account>("ACCOUNT")?.let {
            account = it
            println("Account: $account")
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
        viewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)
    }
}