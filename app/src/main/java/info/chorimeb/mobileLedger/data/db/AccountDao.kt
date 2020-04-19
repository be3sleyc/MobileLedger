package info.chorimeb.mobileLedger.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import info.chorimeb.mobileLedger.data.db.entities.Account
import info.chorimeb.mobileLedger.data.db.entities.AccountName

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllAccounts(accounts: List<Account>)

    @Query("select * from Account")
    fun fetchAccounts(): LiveData<List<Account>>

    @Query("select * from Account where id=:id")
    fun fetchAccount(id: Int): LiveData<Account>

    @Query("select id, name from Account order by name")
    fun fetchAccountNames(): LiveData<List<AccountName>>

    @Query("select distinct type from Account order by type")
    fun fetchAccountTypes(): LiveData<List<String>>

    @Query("delete from Account where id > 0")
    fun deleteAccounts(): Int
}