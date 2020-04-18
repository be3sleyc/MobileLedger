package info.chorimeb.mobileLedger.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import info.chorimeb.mobileLedger.util.convertFromUTCFormat
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat

@Parcelize
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    var accountname: String? = null,
    var paiddate: String? = null,
    var payee: String? = null,
    var description: String? = null,
    var amount: Double? = null,
    var category: String? = null
): Parcelable {
    fun getAmountString(): String {
        return NumberFormat.getCurrencyInstance().format(amount ?: 0.0)
    }
    fun getPaiddateString(): String? {
        return convertFromUTCFormat(this.paiddate)
    }
}