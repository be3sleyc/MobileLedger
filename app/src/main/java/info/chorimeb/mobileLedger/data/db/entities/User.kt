package info.chorimeb.mobileLedger.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

const val CURRENT_USER_ID = 0

@Parcelize
@Entity
data class User(
    var id: Int? = null,
    var email: String? = null,
    val givenname: String? = null,
    var surname: String? = null,
    var lastaccess: String? = null,
    var token: String? = null,
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
): Parcelable {


}