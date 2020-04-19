package info.chorimeb.mobileLedger.ui.home

interface HomeListener {

    fun onStarted()
    fun onSuccess(response: Any)
    fun onFailure(message: String)

}