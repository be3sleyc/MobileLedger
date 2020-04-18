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

    @Query("select * from `Transaction`")
    fun fetchTransactions(): LiveData<List<Transaction>>

    @Query("select * from `Transaction` where id=:id")
    fun fetchTransaction(id: Int): LiveData<Transaction>

    @Query("delete from `Transaction` where id > 0")
    fun deleteTransactions(): Int
}