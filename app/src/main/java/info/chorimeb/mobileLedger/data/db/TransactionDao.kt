package info.chorimeb.mobileLedger.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import info.chorimeb.mobileLedger.data.db.entities.Transaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllTransactions(accounts: List<Transaction>)

    @Query("select * from `Transaction` order by paiddate desc")
    fun fetchTransactions(): LiveData<List<Transaction>>

    @Query("select * from `Transaction` where id=:id")
    fun fetchTransaction(id: Int): LiveData<Transaction>

    @Query("select * from `Transaction` where accountname=:accountname order by paiddate desc")
    fun fetchAccountTransaction(accountname: String): LiveData<List<Transaction>>

    @Query("select distinct category from `Transaction` order by category")
    fun fetchCategories(): LiveData<List<String>>

    @Query("delete from `Transaction` where id=:id")
    fun deleteTransaction(id: Int): Int

    @Query("delete from `Transaction` where id > 0")
    fun deleteTransactions(): Int
}