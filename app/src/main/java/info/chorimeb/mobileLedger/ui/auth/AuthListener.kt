package info.chorimeb.mobileLedger.ui.auth

interface AuthListener {

    fun onStarted()
    fun onSuccess(response: Any)
    fun onFailure(message: String)

}