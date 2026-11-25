package id.pamtirtakemuning.watermeter

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.AssignmentStore
import id.pamtirtakemuning.watermeter.data.Confirmation
import id.pamtirtakemuning.watermeter.data.Report
import id.pamtirtakemuning.watermeter.data.Status
import kotlinx.coroutines.launch

enum class Tab { Tugas, Riwayat, Profil }
enum class From { Detail, Riwayat, Success }

sealed interface Route {
    data object Login : Route
    data object Forgot : Route
    data object Main : Route
    data class Detail(val id: String) : Route
    data class Execute(val id: String) : Route
    data class Success(val id: String) : Route
    data class Result(val id: String, val from: From) : Route
    data class Review(val id: String) : Route
    data object Help : Route
}

/** Menyimpan rute aktif, tab, daftar penugasan, dan pesan toast. Setiap perubahan penugasan langsung disimpan. */
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = AssignmentStore(app)

    var route by mutableStateOf<Route>(Route.Login); private set
    var tab by mutableStateOf(Tab.Tugas); private set
    var list by mutableStateOf(store.load()); private set
    private val _listLive = androidx.lifecycle.MutableLiveData(list)
    val listLive: androidx.lifecycle.LiveData<List<Assignment>> = _listLive
    var message by mutableStateOf(""); private set

    init {
        refreshFromApi()
    }

    /** Mengambil data penugasan dari API secara asynchronous. */
    fun refreshFromApi() {
        viewModelScope.launch {
            val updated = store.fetchAssignmentsFromApi()
            list = updated
            _listLive.value = list
        }
    }

    fun get(id: String): Assignment = list.first { it.id == id }
    fun toast(m: String) { message = m }
    fun clearToast() { message = "" }

    fun go(r: Route) { route = r }
    fun main(t: Tab = Tab.Tugas) { tab = t; route = Route.Main }
    fun selectTab(t: Tab) { tab = t }
    fun logout() { route = Route.Login }

    /** Tombol back sistem. Mengembalikan false bila sistem boleh menutup aplikasi. */
    fun back(): Boolean {
        when (val r = route) {
            Route.Login -> return false
            Route.Forgot -> route = Route.Login
            Route.Main -> if (tab != Tab.Tugas) tab = Tab.Tugas else return false
            is Route.Detail -> main()
            is Route.Execute -> route = Route.Detail(r.id)
            is Route.Review -> route = Route.Execute(r.id)
            is Route.Success -> main()
            is Route.Result -> when (r.from) {
                From.Detail -> route = Route.Detail(r.id)
                From.Riwayat -> main(Tab.Riwayat)
                From.Success -> main()
            }
            Route.Help -> main(Tab.Profil)
        }
        return true
    }

    private fun patch(id: String, f: (Assignment) -> Assignment) {
        list = list.map { if (it.id == id) f(it) else it }
        _listLive.value = list
        store.save(list)
    }

    /** Konfirmasi awal: simpan foto WM lama lalu status Belum -> Proses. */
    fun confirm(id: String, oldPhoto: String) {
        patch(id) { it.copy(status = Status.Proses, confirmation = Confirmation(oldPhoto, System.currentTimeMillis())) }
        toast("Konfirmasi berhasil · Status berubah menjadi Proses")
    }

    fun saveDraft(id: String, d: Report) = patch(id) { it.copy(draft = d) }

    /** Konfirmasi penyelesaian: status Proses -> Selesai. */
    fun submit(id: String, r: Report) {
        patch(id) { it.copy(status = Status.Selesai, report = r, draft = null) }
        route = Route.Success(id)
    }
}
