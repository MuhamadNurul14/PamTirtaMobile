package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.User
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.DividedColumn
import id.pamtirtakemuning.watermeter.ui.components.IconChip
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.InfoRow
import id.pamtirtakemuning.watermeter.ui.components.Modal
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PhotoHero
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.Warn
import id.pamtirtakemuning.watermeter.ui.theme.WarnText
import id.pamtirtakemuning.watermeter.ui.theme.navy

@Composable
fun ProfileScreen(user: User, doneCount: Int, activeCount: Int, onLogout: () -> Unit, onHelp: () -> Unit) {
    var ask by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PhotoHero(shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp), focusY = 0f) {
            Column(Modifier.fillMaxWidth().statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(R.drawable.teknisi_andi), "Foto profil", Modifier.size(96.dp).clip(CircleShape).border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape), contentScale = ContentScale.Crop)
                PText(user.name, Modifier.padding(top = 12.dp), size = 18, weight = FontWeight.Bold)
                PText(user.role, size = 12, color = Color.White.copy(alpha = 0.8f))
                Row(Modifier.padding(top = 4.dp).background(Color.White.copy(alpha = 0.15f), CircleShape).padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ic(R.drawable.ic_building, 11.dp)
                    PText(user.branch, size = 11, lineHeight = 1.3f)
                }
            }
        }
        Column(Modifier.offset(y = (-32).dp).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), R.drawable.ic_check_circle2, Success.copy(alpha = 0.12f), Success, doneCount, "Tugas Selesai")
                StatCard(Modifier.weight(1f), R.drawable.ic_clock, Warn.copy(alpha = 0.15f), WarnText, activeCount, "Tugas Aktif")
            }
            PCard(padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                PText("Informasi Pengguna", Modifier.padding(top = 8.dp), size = 14, weight = FontWeight.Bold)
                DividedColumn {
                    InfoRow("NIP", user.nip, R.drawable.ic_badge_check)
                    InfoRow("Nama Lengkap", user.name, R.drawable.ic_user_round)
                    InfoRow("Username", user.username, R.drawable.ic_user)
                    InfoRow("Nomor Telepon", user.phone, R.drawable.ic_phone)
                    InfoRow("Email", user.email, R.drawable.ic_mail)
                    InfoRow("Jabatan", user.role, R.drawable.ic_tag)
                    InfoRow("Cabang PDAM", user.branch, R.drawable.ic_building)
                }
            }
            // Satu-satunya menu aktif: Bantuan
            PCard(padding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                Row(Modifier.fillMaxWidth().clickable(onClick = onHelp).padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconChip(R.drawable.ic_help, Ice, size = 40.dp, iconSize = 18.dp)
                    Column(Modifier.weight(1f)) {
                        PText("Bantuan", size = 14, weight = FontWeight.SemiBold)
                        PText("FAQ & kontak admin", size = 11, color = navy(0.5f), lineHeight = 1.3f)
                    }
                    Ic(R.drawable.ic_chevron_right, 18.dp, tint = navy(0.3f))
                }
            }
            PButton("LOGOUT", { ask = true }, Modifier.fillMaxWidth(), BtnVariant.DangerOutline, icon = R.drawable.ic_logout)
        }
        Spacer(Modifier.navigationBarsPadding().height(48.dp))
    }
    if (ask) Modal({ ask = false }) {
        PText("Logout", size = 16, weight = FontWeight.Bold)
        PText("Apakah Anda yakin ingin keluar dari aplikasi?", Modifier.padding(top = 8.dp), size = 14, color = navy(0.6f), lineHeight = 1.5f)
        Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PButton("Batal", { ask = false }, Modifier.weight(1f), BtnVariant.Outline)
            PButton("Ya, Logout", { ask = false; onLogout() }, Modifier.weight(1f), BtnVariant.Danger)
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, icon: Int, bg: Color, fg: Color, value: Int, label: String) {
    PCard(modifier, padding = PaddingValues(12.dp)) {
        Box(Modifier.size(32.dp).background(bg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Ic(icon, 16.dp, tint = fg) }
        PText("$value", Modifier.padding(top = 8.dp), size = 24, weight = FontWeight.Bold, lineHeight = 1f)
        PText(label, Modifier.padding(top = 4.dp), size = 11, color = navy(0.55f))
    }
}
