package info.chorimeb.mobileLedger.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import info.chorimeb.mobileLedger.data.db.entities.CURRENT_USER_ID
import info.chorimeb.mobileLedger.data.db.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User): Long

    @Query("select * from User where uid = $CURRENT_USER_ID")
    fun fetch(): LiveData<User>

    @Query("delete from User where uid = $CURRENT_USER_ID")
    fun deleteUser(): Int

}