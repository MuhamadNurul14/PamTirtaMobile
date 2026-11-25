package id.pamtirtakemuning.watermeter.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ID = Locale("id", "ID")

fun fmtDate(ms: Long): String = SimpleDateFormat("d MMM yyyy", ID).format(Date(ms))
fun fmtDateTime(ms: Long): String = SimpleDateFormat("d MMM yyyy, HH:mm", ID).format(Date(ms))

fun fmtDuration(from: Long, to: Long): String {
    val min = maxOf(0L, Math.round((to - from) / 60000.0))
    if (min < 1) return "Kurang dari 1 menit"
    val d = min / 1440
    val h = (min % 1440) / 60
    val m = min % 60
    val parts = mutableListOf<String>()
    if (d > 0) parts += "$d hari"
    if (h > 0) parts += "$h jam"
    if (d == 0L && (m > 0 || h == 0L)) parts += "$m menit"
    return parts.joinToString(" ")
}

fun isToday(ms: Long): Boolean {
    val a = Calendar.getInstance().apply { timeInMillis = ms }
    val b = Calendar.getInstance()
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..10 -> "Selamat Pagi"
    in 11..14 -> "Selamat Siang"
    in 15..18 -> "Selamat Sore"
    else -> "Selamat Malam"
}

/** 6281234567801 -> +62 812-3456-7801 */
fun phoneFmt(p: String): String =
    "+" + p.replace(Regex("^(\\d{2})(\\d{3})(\\d{4})(\\d+)$"), "$1 $2-$3-$4")
