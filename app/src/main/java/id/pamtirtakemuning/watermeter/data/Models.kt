package id.pamtirtakemuning.watermeter.data

import com.google.gson.annotations.SerializedName

enum class Status(val label: String) {
    @SerializedName("Belum") Belum("Belum"),
    @SerializedName("Proses") Proses("Proses"),
    @SerializedName("Validasi") Validasi("Menunggu Validasi"),
    @SerializedName("Ditolak") Ditolak("Ditolak"),
    @SerializedName("Selesai") Selesai("Selesai")
}

enum class JobType(val label: String) {
    @SerializedName("Penggantian") Penggantian("Penggantian"),
    @SerializedName("Perbaikan") Perbaikan("Perbaikan")
}

enum class KondisiFisikWmLama(val apiValue: String, val label: String) {
    @SerializedName("normal") Normal("normal", "Normal"),
    @SerializedName("macet") Macet("macet", "Macet"),
    @SerializedName("kaca_pecah") KacaPecah("kaca_pecah", "Kaca Pecah"),
    @SerializedName("berembun_parah") BerembunParah("berembun_parah", "Berembun Parah"),
    @SerializedName("bodi_korosi") BodiKorosi("bodi_korosi", "Bodi Korosi");

    companion object {
        fun fromApi(value: String?): KondisiFisikWmLama =
            entries.find { it.apiValue.equals(value, ignoreCase = true) } ?: Normal
    }
}

object Options {
    val conditionKeys = listOf("Kondisi Segel", "Kondisi Body", "Kondisi Tutup", "Kondisi Kaca", "Kondisi Mesin")
    val conditionOpts = listOf("Baik", "Rusak", "Tidak Ada")
    const val POSITION_KEY = "Posisi Water Meter"
    val positionOpts = listOf("Di dalam bangunan", "Di luar bangunan", "Di dalam box meter", "Posisi lainnya")
    val brands = listOf("Itron", "Sensus", "Zenner", "Amico", "Elster", "Onda")
    val meterTypes = listOf("Multi Jet", "Single Jet", "Volumetrik", "Ultrasonik")
}

/** Tahap 1 - bukti & pemeriksaan kondisi WM lama sebelum pengerjaan. */
data class Confirmation(
    @SerializedName("oldPhoto") val oldPhoto: String? = null,
    @SerializedName("kondisiFisik") val kondisiFisik: KondisiFisikWmLama = KondisiFisikWmLama.Normal,
    @SerializedName("notes") val notes: String = "",
    @SerializedName("at") val at: Long
)

/**
 * Tahap 2 - hasil eksekusi pemasangan water meter baru.
 * Kelas yang sama dipakai sebagai draft form (doneAt = 0 selama belum dikirim).
 */
data class Report(
    @SerializedName("brand") val brand: String = "",
    @SerializedName("meterType") val meterType: String = "",
    @SerializedName("newSerial") val newSerial: String = "",
    @SerializedName("standLast") val standLast: String = "",
    @SerializedName("standNow") val standNow: String = "",
    @SerializedName("digits") val digits: String = "",
    @SerializedName("seal") val seal: String = "",
    @SerializedName("kondisiFisik") val kondisiFisik: String = "Normal",
    @SerializedName("notes") val notes: String = "",
    @SerializedName("newPhoto") val newPhoto: String? = null,
    @SerializedName("signature") val signature: String? = null,
    @SerializedName("doneAt") val doneAt: Long = 0L,
)

data class Assignment(
    @SerializedName("id") val id: String,
    @SerializedName("assignNo") val assignNo: String,
    @SerializedName("name") val name: String,
    @SerializedName("custNo") val custNo: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("branch") val branch: String,
    @SerializedName("tariff") val tariff: String,
    @SerializedName("jobType") val jobType: JobType,
    @SerializedName("damage") val damage: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("oldSerial") val oldSerial: String,
    @SerializedName("assignedAt") val assignedAt: Long,
    @SerializedName("officer") val officer: String,
    @SerializedName("status") val status: Status,
    @SerializedName("notes") val notes: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("confirmation") val confirmation: Confirmation? = null,
    @SerializedName("report") val report: Report? = null,
    @SerializedName("draft") val draft: Report? = null,
    @SerializedName("rejectionReason") val rejectionReason: String? = null,
)

data class User(
    @SerializedName("name") val name: String,
    @SerializedName("nip") val nip: String,
    @SerializedName("username") val username: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: String,
    @SerializedName("role") val role: String,
    @SerializedName("branch") val branch: String,
)
