package info.chorimeb.mobileLedger.data.db.entities

data class AccountName(val id: Int, val name: String) {
    override fun toString(): String {
        return name
    }
}