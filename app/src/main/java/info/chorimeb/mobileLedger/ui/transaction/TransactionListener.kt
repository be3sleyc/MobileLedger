package info.chorimeb.mobileLedger.ui.transaction

interface TransactionListener {

    fun onStarted()
    fun onSuccess(response: Any)
    fun onFailure(message: String)

}