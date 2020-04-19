package info.chorimeb.mobileLedger.ui.account

interface AccountListener {

    fun onStarted()
    fun onSuccess(response: Any)
    fun onFailure(message: String)

}