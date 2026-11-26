package id.pamtirtakemuning.watermeter.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import id.pamtirtakemuning.watermeter.AppViewModel
import id.pamtirtakemuning.watermeter.From
import id.pamtirtakemuning.watermeter.Route
import id.pamtirtakemuning.watermeter.Tab
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.data.technician
import id.pamtirtakemuning.watermeter.ui.components.BottomNav
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.screens.DashboardScreen
import id.pamtirtakemuning.watermeter.ui.screens.DetailScreen
import id.pamtirtakemuning.watermeter.ui.screens.ExecuteScreen
import id.pamtirtakemuning.watermeter.ui.screens.ForgotScreen
import id.pamtirtakemuning.watermeter.ui.screens.HelpScreen
import id.pamtirtakemuning.watermeter.ui.screens.HistoryScreen
import id.pamtirtakemuning.watermeter.ui.screens.LoginScreen
import id.pamtirtakemuning.watermeter.ui.screens.ProfileScreen
import id.pamtirtakemuning.watermeter.ui.screens.ResultScreen
import id.pamtirtakemuning.watermeter.ui.screens.SuccessScreen
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import androidx.compose.ui.graphics.Color

@Composable
fun PamApp(vm: AppViewModel) {
    val route = vm.route
    val tab = vm.tab
    val list = vm.list

    BackHandler(enabled = route != Route.Login && !(route == Route.Main && tab == Tab.Tugas)) { vm.back() }
    LaunchedEffect(vm.message) {
        if (vm.message.isNotEmpty()) { kotlinx.coroutines.delay(2200); vm.clearToast() }
    }

    // Ikon status bar: terang di atas latar navy (Login, Dashboard, Profile), gelap di latar lain
    val darkTop = route == Route.Login || (route == Route.Main && tab != Tab.Riwayat)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as android.app.Activity).window
        val c = WindowCompat.getInsetsController(window, view)
        c.isAppearanceLightStatusBars = !darkTop
        c.isAppearanceLightNavigationBars = true
    }

    Box(Modifier.fillMaxSize().background(if (route == Route.Login || route is Route.Success) Color.White else Mist)) {
        when (route) {
            Route.Login -> LoginScreen(onLogin = { vm.main() }, onForgot = { vm.go(Route.Forgot) })
            Route.Forgot -> ForgotScreen(onBack = { vm.go(Route.Login) })
            is Route.Detail, is Route.Execute, is Route.Result, is Route.Review -> {
                val id = when (route) { is Route.Detail -> route.id; is Route.Execute -> route.id; is Route.Result -> route.id; else -> "" }
                val a = vm.get(id)
                val toDetail = { vm.go(Route.Detail(a.id)) }
                // halaman tahap hanya bisa dibuka sesuai status; selain itu kembali ke detail
                val allowed = route is Route.Detail ||
                    (route is Route.Execute && (a.status == Status.Proses || a.status == Status.Ditolak)) ||
                    (route is Route.Result && (a.status == Status.Validasi || a.status == Status.Selesai) && a.report != null)
                if (!allowed || route is Route.Detail) {
                    DetailScreen(
                        a, onBack = { vm.main() },
                        onConfirm = { photo, conds, notes -> vm.confirm(a.id, photo, conds, notes) },
                        onExecute = { vm.go(Route.Execute(a.id)) },
                        onResult = { vm.go(Route.Result(a.id, From.Detail)) },
                    )
                } else if (route is Route.Execute) {
                    ExecuteScreen(
                        a, technician, onBack = toDetail,
                        onDraft = { vm.saveDraft(a.id, it) },
                        onSubmit = { vm.submit(a.id, it) },
                        toast = vm::toast,
                    )
                } else if (route is Route.Result) {
                    val from = route.from
                    ResultScreen(a, onBack = { if (from == From.Detail) toDetail() else vm.main(if (from == From.Riwayat) Tab.Riwayat else Tab.Tugas) })
                }
            }
            is Route.Success -> SuccessScreen(vm.get(route.id), onResult = { vm.go(Route.Result(route.id, From.Success)) }, onHome = { vm.main() })
            Route.Help -> HelpScreen(onBack = { vm.main(Tab.Profil) }, toast = vm::toast)
            Route.Main -> {
                when (tab) {
                    Tab.Tugas -> DashboardScreen(list, technician, onDetail = { vm.go(Route.Detail(it)) })
                    Tab.Riwayat -> HistoryScreen(list, onDetail = { vm.go(Route.Result(it, From.Riwayat)) })
                    Tab.Profil -> ProfileScreen(
                        technician,
                        doneCount = list.count { it.status == Status.Selesai },
                        activeCount = list.count { it.status != Status.Selesai },
                        onLogout = { vm.logout() },
                        onHelp = { vm.go(Route.Help) },
                    )
                }
                BottomNav(tab, onChange = { vm.selectTab(it) }, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }

        if (vm.message.isNotEmpty()) {
            PText(
                vm.message,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp)).background(Navy, RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 12.dp),
                size = 12, weight = FontWeight.SemiBold, color = Color.White, align = TextAlign.Center,
            )
        }
    }
}
