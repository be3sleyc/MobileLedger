package info.chorimeb.mobileLedger.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import info.chorimeb.mobileLedger.R
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat

@Parcelize
@Entity
data class Account(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    var name: String? = null,
    var type: String? = null,
    var balance: Double? = null,
    var notes: String? = null
) : Parcelable {

    fun getBalanceString(): String {
        return NumberFormat.getCurrencyInstance().format(balance ?: 0.0)
    }

    fun getTypeIcon(): Int {
        return when (type) {
            "credit" -> R.drawable.ic_credit
            "debit" -> R.drawable.ic_credit
            "cash" -> R.drawable.ic_cash
            "checking" -> R.drawable.ic_check
            "savings" -> R.drawable.ic_save
            "loan" -> R.drawable.ic_lend
            else -> R.drawable.ic_unknown
        }
    }
}