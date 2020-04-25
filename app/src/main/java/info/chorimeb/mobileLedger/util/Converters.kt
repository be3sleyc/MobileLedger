package info.chorimeb.mobileLedger.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone.getDefault

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Throws(ParseException::class)
fun convertFromUTCFormatDate(dateStr: String?): String? {
    val tz = getDefault()
    val utc = TimeZone.getTimeZone("UTC")
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val destFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    sourceFormat.timeZone = utc
    destFormat.timeZone = tz
    return if (dateStr != null) {
        val convertedDate: Date = sourceFormat.parse(dateStr)
        destFormat.format(convertedDate)
    } else {
        null
    }
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Throws(ParseException::class)
fun convertFromUTCFormatTime(dateStr: String?): String? {
    val tz = getDefault()
    val utc = TimeZone.getTimeZone("UTC")
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val destFormat = SimpleDateFormat("HH:mm", Locale.US)
    sourceFormat.timeZone = utc
    destFormat.timeZone = tz
    return if (dateStr != null) {
        val convertedDate: Date = sourceFormat.parse(dateStr)
        destFormat.format(convertedDate)
    } else {
        null
    }
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun convertToUTCFormat(dateStr: String?): String? {
    val tz = getDefault()
    val utc = TimeZone.getTimeZone("UTC")
    println("tz: $tz")
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    val destFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sourceFormat.timeZone = tz
    destFormat.timeZone = utc
    return if (dateStr != null) {
        val convertedDate: Date = sourceFormat.parse(dateStr)
        destFormat.format(convertedDate)
    } else {
        null
    }
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}