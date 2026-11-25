package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.Logo
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PInput
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PageHeader
import id.pamtirtakemuning.watermeter.ui.components.PhotoHero
import id.pamtirtakemuning.watermeter.ui.components.annotated
import id.pamtirtakemuning.watermeter.ui.components.bold
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.navy

@Composable
fun LoginScreen(onLogin: () -> Unit, onForgot: () -> Unit) {
    var show by remember { mutableStateOf(false) }
    var u by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var keep by remember { mutableStateOf(true) }
    var err by remember { mutableStateOf("") }
    fun submit() {
        if (u.isBlank() || p.isEmpty()) { err = "Username dan password wajib diisi."; return }
        err = ""
        onLogin()
    }
    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        PhotoHero(
            focusY = -0.2f,
            overlay = listOf(Navy.copy(alpha = 0.90f), Navy.copy(alpha = 0.45f), Navy.copy(alpha = 0.85f)),
        ) {
            Column(
                Modifier.fillMaxWidth().heightIn(min = 330.dp).statusBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                Logo(76.dp, light = true)
                PText("PAM TIRTA KEMUNING", Modifier.padding(top = 16.dp), size = 24, weight = FontWeight.Bold, letterSpacing = 0.5f, align = TextAlign.Center)
                PText("Sistem Informasi Pergantian Water Meter", Modifier.padding(top = 4.dp), size = 12, weight = FontWeight.Medium, color = Color.White.copy(alpha = 0.85f), align = TextAlign.Center)
            }
        }
        Column(
            Modifier.offset(y = (-32).dp).fillMaxWidth().background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(start = 24.dp, end = 24.dp, top = 28.dp).navigationBarsPadding(),
        ) {
            PText("LOGIN USER", size = 20, weight = FontWeight.Bold)
            PText("Silakan masuk untuk mengakses penugasan Anda.", Modifier.padding(top = 4.dp), size = 14, color = navy(0.6f))
            Column(Modifier.padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PInput(u, { u = it }, label = "Username", placeholder = "Masukkan username", icon = R.drawable.ic_user)
                PInput(p, { p = it }, label = "Password", placeholder = "Masukkan password", icon = R.drawable.ic_lock, password = !show, right = {
                    Ic(if (show) R.drawable.ic_eye_off else R.drawable.ic_eye, 18.dp, Modifier.clip(CircleShape).clickable { show = !show }, tint = navy(0.5f))
                })
            }
            if (err.isNotEmpty()) PText(err, Modifier.padding(top = 12.dp), size = 12, weight = FontWeight.Medium, color = Danger)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.clickable { keep = !keep }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(keep, { keep = it }, colors = CheckboxDefaults.colors(checkedColor = Navy))
                    PText("Ingat Saya", size = 12, weight = FontWeight.Medium)
                }
                PText("Lupa Password?", Modifier.clickable(onClick = onForgot).padding(vertical = 8.dp), size = 12, weight = FontWeight.SemiBold, color = Sky)
            }
            PButton("LOGIN", { submit() }, Modifier.padding(top = 16.dp).fillMaxWidth())
            PText("Demo: isi username dan password apa saja", Modifier.padding(top = 12.dp).fillMaxWidth(), size = 11, color = navy(0.4f), align = TextAlign.Center)
            PText("© 2026 PAM Tirta Kemuning", Modifier.padding(vertical = 24.dp).fillMaxWidth(), size = 12, color = navy(0.5f), align = TextAlign.Center)
        }
    }
}

@Composable
fun ForgotScreen(onBack: () -> Unit) {
    var v by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Color.White)) {
        PageHeader("", onBack, background = Color.White)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(80.dp).background(Ice, CircleShape), contentAlignment = Alignment.Center) {
                Ic(if (sent) R.drawable.ic_check_circle else R.drawable.ic_shield_check, 38.dp, tint = if (sent) Success else Navy)
            }
            PText(if (sent) "Permintaan Terkirim" else "Lupa Password?", Modifier.padding(top = 20.dp), size = 20, weight = FontWeight.Bold)
            if (sent) {
                PText(
                    annotated {
                        append("Permintaan reset password untuk ")
                        bold(v)
                        append(" telah diteruskan ke administrator PDAM. Anda akan dihubungi melalui nomor terdaftar.")
                    },
                    Modifier.padding(top = 8.dp), size = 14, color = navy(0.6f), align = TextAlign.Center, lineHeight = 1.6f,
                )
            } else {
                PText("Hubungi administrator PDAM untuk mengatur ulang password Anda, atau kirim permintaan melalui form di bawah.",
                    Modifier.padding(top = 8.dp), size = 14, color = navy(0.6f), align = TextAlign.Center, lineHeight = 1.6f)
                Column(Modifier.padding(top = 24.dp).fillMaxWidth()) {
                    PInput(v, { v = it }, label = "Username / NIP", placeholder = "andi.pratama", icon = R.drawable.ic_user)
                    PButton("Kirim Permintaan", { sent = true }, Modifier.padding(top = 20.dp).fillMaxWidth(), icon = R.drawable.ic_send, enabled = v.isNotBlank())
                }
            }
            PButton("Kembali ke Login", onBack, Modifier.padding(top = 12.dp).fillMaxWidth(), BtnVariant.Outline)
        }
    }
}
