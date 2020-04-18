package info.chorimeb.mobileLedger.data.network

import info.chorimeb.mobileLedger.data.network.requests.*
import info.chorimeb.mobileLedger.data.network.responses.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    // User Object Calls
    @POST("users/register")
    suspend fun registerUser(@Body registerReq: RegisterRequest): Response<AuthResponse>

    @PUT("users/login")
    suspend fun loginUser(@Body loginReq: LoginRequest): Response<AuthResponse>

    @PUT("users/logout")
    suspend fun logoutUser(@Header("auth-token") token: String): Response<AuthResponse>

    // Account Object Calls
    @GET("accounts/")
    suspend fun getAllAccounts(@Header("auth-token") token: String): Response<AccountListResponse>

//    @GET("accounts/{id}")
//    suspend fun getAccount(@Header("auth-token") token: String): Response<AccountResponse>

    @PUT("accounts/{id}/edit")
    suspend fun editAccount(
        @Header("auth-token") token: String,
        @Path("id", encoded = false) id: Int,
        @Body editAccountReq: EditAccountRequest
    ): Response<AccountResponse>

    @POST("accounts/add")
    suspend fun addAccount(
        @Header("auth-token") token: String,
        @Body newAccountReq: NewAccountRequest
    ): Response<AccountResponse>

    // Transaction Object Calls
    @GET("transactions/")
    suspend fun getAllTransactions(@Header("auth-token") token: String): Response<TransactionListResponse>

//    @GET("transactions/{id}")
//    suspend fun getTransaction(
//        @Header("auth-token") token: String,
//        @Path("id", encoded = false) id: Int
//    ): Response<TransactionResponse>
//
//    @GET("transactions/range/{start}/{stop}")
//    suspend fun getTransactionsRange(
//        @Header("auth-token") token: String,
//        @Path("start", encoded = false) start: String,
//        @Path("stop", encoded = false) stop: String
//    ): Response<TransactionListResponse>
//
//    @GET("transactions/categories/{category}")
//    suspend fun getCategoryTransactions(
//        @Header("auth-token") token: String,
//        @Path("category", encoded = false) category: String
//    ): Response<TransactionListResponse>
//
//    @GET("transactions/categories/{category}/range/{start}/{stop}")
//    suspend fun getCategoryTransactionsRange(
//        @Header("auth-token") token: String,
//        @Path("category", encoded = false) category: String,
//        @Path("start", encoded = false) start: String,
//        @Path("stop", encoded = false) stop: String
//    ): Response<TransactionListResponse>
//
//    @GET("transactions/payees/{payee}")
//    suspend fun getPayeeTransactions(
//        @Header("auth-token") token: String,
//        @Path("payee", encoded = false) payee: String
//    ): Response<TransactionListResponse>
//
//    @GET("transactions/payees/{payee}/range/{start}/{stop}")
//    suspend fun getPayeeTransactionsRange(
//        @Header("auth-token") token: String,
//        @Path("payee", encoded = false) payee: String,
//        @Path("start", encoded = false) start: String,
//        @Path("stop", encoded = false) stop: String
//    ): Response<TransactionListResponse>
//
//    @GET("transactions/accounts/{account}")
//    suspend fun getAccountTransactions(
//        @Header("auth-token") token: String,
//        @Path("account", encoded = false) accountId: Int
//    ): Response<TransactionListResponse>
//
//    @GET("transactions/accounts/{account}/range/{start}/{stop}")
//    suspend fun getAccountTransactionsRange(
//        @Header("auth-token") token: String,
//        @Path("account", encoded = false) accountId: Int,
//        @Path("start", encoded = false) start: String,
//        @Path("stop", encoded = false) stop: String
//    ): Response<TransactionListResponse>

    @GET("transactions/categorylist")
    suspend fun getTransactionCategories(@Header("auth-token") token: String): Response<CategoryListResponse>

    @PUT("transactions/{id}/edit/")
    suspend fun editTransaction(
        @Header("auth-token") token: String,
        @Path("id", encoded = false) id: Int,
        @Body editTransactionReq: TransactionRequest
    ): Response<TransactionResponse>

    @POST("transactions/log")
    suspend fun addTransaction(
        @Header("auth-token") token: String,
        @Body newTransactionReq: TransactionRequest
    ): Response<TransactionResponse>


    companion object {

        private const val BASE_URL = "http://192.168.1.7/api/"

        operator fun invoke(networkConnInterceptor: NetworkConnInterceptor): ApiService {

            val client = OkHttpClient.Builder().addInterceptor(networkConnInterceptor).build()

            return Retrofit
                .Builder()
                .baseUrl(this.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }
    }
}