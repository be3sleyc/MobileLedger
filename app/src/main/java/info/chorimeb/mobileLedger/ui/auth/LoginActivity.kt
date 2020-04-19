package info.chorimeb.mobileLedger.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.databinding.ActivityLoginBinding
import info.chorimeb.mobileLedger.ui.home.HomeActivity
import info.chorimeb.mobileLedger.util.hide
import info.chorimeb.mobileLedger.util.show
import info.chorimeb.mobileLedger.util.toast
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein: Kodein by kodein()

    private val factory: AuthViewModelFactory by instance<AuthViewModelFactory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProvider(this, factory)
            .get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                Intent(this, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        })

        txtPassword.setOnEditorActionListener{ _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    viewModel.onLoginClick(this.btnLogin)
            }
            false
        }
    }

    override fun onStarted() {
        progressBarLogin.show()
    }

    override fun onSuccess(response: Any) {
        progressBarLogin.hide()
    }

    override fun onFailure(message: String) {
        progressBarLogin.hide()
        toast(message)
    }
}
