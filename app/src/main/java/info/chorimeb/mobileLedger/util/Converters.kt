package info.chorimeb.mobileLedger.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Throws(ParseException::class)
fun convertFromUTCFormat(dateStr: String?): String? {
    val utc = TimeZone.getTimeZone("UTC")
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val destFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    sourceFormat.timeZone = utc
    return if (dateStr != null) {
        val convertedDate: Date = sourceFormat.parse(dateStr)
        destFormat.format(convertedDate)
    } else {
        null
    }


}