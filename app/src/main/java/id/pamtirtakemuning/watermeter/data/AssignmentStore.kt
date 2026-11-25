package id.pamtirtakemuning.watermeter.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
     * Mengambil data penugasan secara asynchronous dari API melalui [ApiClient].
     * Jika sukses dan data tidak kosong, simpan ke lokal dan kembalikan data baru.
     * Jika gagal (misal koneksi/placeholder URL), fallback ke data lokal [load].
     */
    suspend fun fetchAssignmentsFromApi(): List<Assignment> = withContext(Dispatchers.IO) {
        try {
            val remoteData = apiService.getAssignments()
            if (remoteData.isNotEmpty()) {
                save(remoteData)
                remoteData
            } else {
                load()
            }
        } catch (e: Exception) {
            load()
        }
    }

    private companion object {
        // v3: alur bertahap (Belum -> Proses -> Selesai) dengan dokumentasi awal terpisah
        const val KEY = "assignments-v3"
    }
}
