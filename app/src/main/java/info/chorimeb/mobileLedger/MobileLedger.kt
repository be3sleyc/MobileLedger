package info.chorimeb.mobileLedger

import android.app.Application
import info.chorimeb.mobileLedger.data.db.AppDatabase
import info.chorimeb.mobileLedger.data.network.ApiService
import info.chorimeb.mobileLedger.data.network.NetworkConnInterceptor
import info.chorimeb.mobileLedger.data.repositories.AccountRepository
import info.chorimeb.mobileLedger.data.repositories.TransactionRepository
import info.chorimeb.mobileLedger.data.repositories.UserRepository
import info.chorimeb.mobileLedger.ui.account.AccountViewModelFactory
import info.chorimeb.mobileLedger.ui.auth.AuthViewModelFactory
import info.chorimeb.mobileLedger.ui.home.accounts.AccountsViewModelFactory
import info.chorimeb.mobileLedger.ui.home.transactions.TransactionsViewModelFactory
import info.chorimeb.mobileLedger.ui.transaction.TransactionViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MobileLedger : Application(), KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@MobileLedger))

        bind() from singleton { NetworkConnInterceptor(instance()) }
        bind() from singleton { ApiService(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton { AccountRepository(instance(), instance()) }
        bind() from singleton { TransactionRepository(instance(), instance())}

        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { AccountsViewModelFactory(instance(), instance()) }
        bind() from provider { TransactionsViewModelFactory(instance(), instance()) }
        bind() from provider { AccountViewModelFactory(instance(), instance())}
        bind() from provider { TransactionViewModelFactory(instance(), instance()) }
    }
}