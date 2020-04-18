package info.chorimeb.mobileLedger.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.databinding.ActivityRegisterBinding
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import info.chorimeb.mobileLedger.util.toast
import kotlinx.android.synthetic.main.activity_register.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RegisterActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: AuthViewModelFactory by instance<AuthViewModelFactory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRegisterBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)
        val viewModel = ViewModelProvider(this, factory)
            .get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this

        actionBar?.setDisplayHomeAsUpEnabled(true)

        txtNewPassword.setOnEditorActionListener(){ _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    viewModel.onRegisterClick(this.btnRegister)
            }
            false
        }
    }

    override fun onStarted() {
        progressBarRegister.show()
    }

    override fun onSuccess(response: Any) {
        progressBarRegister.hide()
        toast(response as String)
        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailure(message: String) {
        progressBarRegister.hide()
        toast("Fail:$message")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

}
