package info.chorimeb.mobileLedger.data.db.entities

data class AccountName(val id: Int, val name: String, val isdeleted: Int) {
    override fun toString(): String {
        return if (isdeleted == 1) {
            "$name - closed"
        } else {
            name
        }
    }
}