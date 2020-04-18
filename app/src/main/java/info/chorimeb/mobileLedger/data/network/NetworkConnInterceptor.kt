package info.chorimeb.mobileLedger.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import info.chorimeb.mobileLedger.util.NoInternetConnectionException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnInterceptor(context: Context) : Interceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoInternetConnectionException("Must be connected to Internet")
        }
        return chain.proceed(chain.request())
    }

    private fun isConnected(): Boolean {
        val cm =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            if (nc != null) {
                return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
        }
        println("network connection false")
        return false
    }

}