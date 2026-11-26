package id.pamtirtakemuning.watermeter.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/** Penyimpanan lokal penugasan (SharedPreferences + JSON) & integrasi API Client. */
class AssignmentStore(context: Context) {
    private val prefs = context.getSharedPreferences("pam-tirta-kemuning", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val apiService = ApiClient.apiService

    fun load(): List<Assignment> = try {
        val type = object : TypeToken<List<Assignment>>() {}.type
        gson.fromJson<List<Assignment>>(prefs.getString(KEY, null), type)?.takeIf { it.isNotEmpty() } ?: seedAssignments()
    } catch (e: Exception) {
        seedAssignments()
    }

    fun save(list: List<Assignment>) {
        prefs.edit().putString(KEY, gson.toJson(list)).apply()
    }

    /**
     * Mengambil data penugasan secara asynchronous dari API melalui [ApiClient] dan menggabungkan dengan data penggantian-wm.
     * Jika gagal (koneksi/server offline), fallback ke data lokal [load].
     */
    suspend fun fetchAssignmentsFromApi(): List<Assignment> = withContext(Dispatchers.IO) {
        try {
            val penugasanResp = apiService.getPenugasan()
            val penggantianResp = apiService.getPenggantianWm()
            val penugasanList = penugasanResp.data
            val penggantianList = penggantianResp.dataWrapper.data

            val penggantianMap = penggantianList.associateBy { it.idPenugasan }

            val assignments = penugasanList.map { p ->
                val repl = penggantianMap[p.idPenugasan]
                val status = when {
                    repl == null || (p.status == "pending" && repl.tahapPenggantian == null) -> Status.Belum
                    repl.statusValidasi == "ditolak" -> Status.Ditolak
                    repl.statusValidasi == "disetujui" || p.status == "selesai" -> Status.Selesai
                    repl.tahapPenggantian == "menunggu_validasi" && (repl.statusValidasi == "menunggu" || repl.statusValidasi == null) -> Status.Validasi
                    repl.tahapPenggantian == "data_wm_lama" || p.status == "proses" -> Status.Proses
                    else -> Status.Belum
                }

                val confirmation = if (repl?.pathImageWmLama != null || repl?.tahapPenggantian != null) {
                    Confirmation(
                        oldPhoto = repl.pathImageWmLama,
                        kondisiFisik = KondisiFisikWmLama.fromApi(repl.kondisiFisikWmLama),
                        notes = "",
                        at = System.currentTimeMillis()
                    )
                } else null

                val report = if (repl?.noSeriWmBaru != null) {
                    Report(
                        brand = repl.merk?.merkWm ?: "Itron",
                        newSerial = repl.noSeriWmBaru,
                        standLast = repl.standWmLama?.toString() ?: "0",
                        standNow = repl.standAwalWmBaru?.toString() ?: "0",
                        newPhoto = repl.pathImageWmBaru,
                        doneAt = System.currentTimeMillis()
                    )
                } else null

                Assignment(
                    id = p.idPenugasan,
                    assignNo = p.nomorSpk,
                    name = p.pelanggan?.nama ?: "Pelanggan",
                    custNo = p.pelanggan?.idPelangganPdam ?: "-",
                    phone = "628120000000",
                    address = p.pelanggan?.alamat ?: "-",
                    branch = p.unit?.namaUnit ?: "Cabang Kuningan",
                    tariff = p.pelanggan?.tarif?.namaTarif ?: "R1 / Rumah Tangga",
                    jobType = JobType.Penggantian,
                    damage = p.alasan ?: "Pergantian",
                    reason = p.alasan ?: "-",
                    oldSerial = repl?.noSeriWmLama ?: "WM-LAMA-001",
                    assignedAt = System.currentTimeMillis(),
                    officer = p.teknisi?.fullname ?: "Teknisi",
                    status = status,
                    notes = p.alasan ?: "",
                    lat = -6.9759,
                    lng = 108.4838,
                    confirmation = confirmation,
                    report = report,
                    rejectionReason = repl?.catatanValidasi
                )
            }

            if (assignments.isNotEmpty()) {
                save(assignments)
                assignments
            } else {
                load()
            }
        } catch (e: Exception) {
            load()
        }
    }

    suspend fun postDataWmLama(idPenugasan: String, noSeri: String, stand: String, kondisiFisik: KondisiFisikWmLama, imagePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(imagePath)
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image_wm_lama", file.name, reqFile)
            val resp = apiService.postDataWmLama(
                idPenugasan,
                noSeri.toRequestBody("text/plain".toMediaTypeOrNull()),
                stand.toRequestBody("text/plain".toMediaTypeOrNull()),
                kondisiFisik.apiValue.toRequestBody("text/plain".toMediaTypeOrNull()),
                body
            )
            resp.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun postSelesaikan(idPenugasan: String, noSeriBaru: String, standBaru: String, tgl: String, idMerk: String, imagePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(imagePath)
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image_wm_baru", file.name, reqFile)
            val resp = apiService.postSelesaikan(
                idPenugasan,
                noSeriBaru.toRequestBody("text/plain".toMediaTypeOrNull()),
                standBaru.toRequestBody("text/plain".toMediaTypeOrNull()),
                tgl.toRequestBody("text/plain".toMediaTypeOrNull()),
                idMerk.toRequestBody("text/plain".toMediaTypeOrNull()),
                body
            )
            resp.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun patchPerbaiki(idPenugasan: String, noSeriBaru: String, standBaru: String, tgl: String, idMerk: String, imagePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(imagePath)
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image_wm_baru", file.name, reqFile)
            val resp = apiService.patchPerbaiki(
                idPenugasan,
                noSeriBaru.toRequestBody("text/plain".toMediaTypeOrNull()),
                standBaru.toRequestBody("text/plain".toMediaTypeOrNull()),
                tgl.toRequestBody("text/plain".toMediaTypeOrNull()),
                idMerk.toRequestBody("text/plain".toMediaTypeOrNull()),
                body
            )
            resp.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    private companion object {
        const val KEY = "assignments-v7"
    }
}
