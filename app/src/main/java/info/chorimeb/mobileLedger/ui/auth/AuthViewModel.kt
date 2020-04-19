package info.chorimeb.mobileLedger.ui.auth

import android.content.Intent
import android.util.Patterns
import android.view.View
import androidx.lifecycle.ViewModel
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
            authListener?.onFailure("Bad Email or Password")
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            // error
            authListener?.onFailure("Bad Email")
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
            authListener?.onFailure("First name is required")
            return
        }
        if (lastName.isNullOrBlank()) {
            // error
            authListener?.onFailure("Last name is required")
            return
        }
        if (newEmail.isNullOrEmpty()) {
            // error
            authListener?.onFailure("A valid email is required")
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail!!).matches()) {
            // error
            authListener?.onFailure("A Vaalid email is required")
            return
        }
        if (newPassword.isNullOrBlank()) {
            // error
            authListener?.onFailure("Bad password")
            return
        }

        Coroutines.main {
            try {
                val response = repository.register(firstName!!, lastName!!, newEmail!!, newPassword!!)
                response.message?.let {
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

}