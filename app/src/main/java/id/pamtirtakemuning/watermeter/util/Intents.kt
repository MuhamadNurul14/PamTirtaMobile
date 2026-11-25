package id.pamtirtakemuning.watermeter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import id.pamtirtakemuning.watermeter.data.Assignment

private fun Context.open(uri: String, action: String = Intent.ACTION_VIEW) {
    try {
        startActivity(Intent(action, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        Toast.makeText(this, "Tidak ada aplikasi yang dapat membuka tautan ini", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openMaps(a: Assignment) =
    open("https://www.google.com/maps/search/?api=1&query=${a.lat},${a.lng}")

fun Context.openWhatsApp(a: Assignment) = openWhatsApp(
    a.phone,
    "Selamat pagi Bapak/Ibu ${a.name}, kami petugas PAM Tirta Kemuning akan melakukan penggantian water meter (No. Pelanggan ${a.custNo}).",
)

fun Context.openWhatsApp(phone: String, text: String) = open("https://wa.me/$phone?text=${Uri.encode(text)}")
fun Context.dial(number: String) = open("tel:$number", Intent.ACTION_DIAL)
fun Context.sendMail(to: String) = open("mailto:$to", Intent.ACTION_SENDTO)
