package info.chorimeb.mobileLedger.ui.auth

import android.content.Intent
import android.util.Patterns
import android.view.View
import androidx.lifecycle.ViewModel
import info.chorimeb.mobileLedger.R
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.util.ApiException
import info.chorimeb.mobileLedger.util.Coroutines
import info.chorimeb.mobileLedger.util.NoInternetConnectionException

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    var email: String? = null
    var password: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var newEmail: String? = null
    var newPassword: String? = null

    var authListener: AuthListener? = null

    fun getLoggedInUser() = repository.getUser()

    fun onLoginClick(view: View) {
        authListener?.onStarted()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_login))
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_email))
            return
        }

        Coroutines.main {
            try {
                val response = repository.login(email!!.trim(), password!!)

                response.user?.let {
                    repository.saveUser(it)
                    repository.loadProfile(it.token!!)
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(response.message!!)
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetConnectionException) {
                authListener?.onFailure(e.message!!)
            }
        }
    }

    fun showRegisterActivity(view: View) {
        Intent(view.context, RegisterActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onRegisterClick(view: View) {
        authListener?.onStarted()

        if (firstName.isNullOrBlank()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_blank_firstname))
            return
        }
        if (lastName.isNullOrBlank()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_blank_lastname))
            return
        }
        if (newEmail.isNullOrEmpty()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_email))
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail!!).matches()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_email))
            return
        }
        if (newPassword.isNullOrBlank()) {
            // error
            authListener?.onFailure(view.context.getString(R.string.error_password))
            return
        } else if (newPassword!!.length < 8) {
            authListener?.onFailure(view.context.getString(R.string.error_newPassword))
            return
        }

        Coroutines.io {
            try {
                val response = repository.register(firstName!!, lastName!!, newEmail!!, newPassword!!)
                response.message?.let {
                    authListener?.onSuccess(it)
                    return@io
                }
                authListener?.onFailure(response.message!!)
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetConnectionException) {
                authListener?.onFailure(e.message!!)
            }
        }
    }

}