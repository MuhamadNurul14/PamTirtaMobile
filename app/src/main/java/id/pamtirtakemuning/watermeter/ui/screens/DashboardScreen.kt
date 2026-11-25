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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.data.User
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.Modal
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PhotoHero
import id.pamtirtakemuning.watermeter.ui.components.SearchBar
import id.pamtirtakemuning.watermeter.ui.components.Segmented
import id.pamtirtakemuning.watermeter.ui.components.StatusBadge
import id.pamtirtakemuning.watermeter.ui.theme.GrayText
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Muted
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.Warn
import id.pamtirtakemuning.watermeter.ui.theme.WarnText
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.greeting
import id.pamtirtakemuning.watermeter.util.isToday
import id.pamtirtakemuning.watermeter.util.openMaps
import id.pamtirtakemuning.watermeter.util.openWhatsApp

@Composable
private fun LabeledValue(label: String, value: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified, mono: Boolean = false) {
    Column(modifier) {
        PText(label, size = 10, color = navy(0.5f), lineHeight = 1.3f)
        PText(value, size = 12, weight = FontWeight.SemiBold, color = color, mono = mono, lineHeight = 1.3f)
    }
}

@Composable
private fun AssignmentCard(a: Assignment, onDetail: () -> Unit) {
    val ctx = LocalContext.current
    val tariff = a.tariff.split(" / ").let { "${it[0]} / ${it.getOrElse(1) { "" }.replace(" Menengah", "")}" }
    PCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                PText(a.name, size = 15, weight = FontWeight.Bold, maxLines = 1)
                PText("No. ${a.custNo}", size = 12, weight = FontWeight.Medium, color = id.pamtirtakemuning.watermeter.ui.theme.Sky)
            }
            StatusBadge(a.status)
        }
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ic(R.drawable.ic_map_pin, 14.dp, Modifier.padding(top = 2.dp), tint = navy(0.6f))
            PText(a.address, size = 12, color = navy(0.6f), lineHeight = 1.6f)
        }
        Column(Modifier.padding(top = 12.dp).fillMaxWidth().background(Ice, RoundedCornerShape(12.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row { LabeledValue("Jenis Penugasan", a.jobType.label, Modifier.weight(1f)); LabeledValue("Tarif / Golongan", tariff, Modifier.weight(1f)) }
            Row { LabeledValue("Alasan", a.damage, Modifier.weight(1f), WarnText); LabeledValue("No. Water Meter", a.oldSerial, Modifier.weight(1f), mono = true) }
        }
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PButton("Lokasi", { ctx.openMaps(a) }, Modifier.weight(1f), BtnVariant.Outline, small = true, icon = R.drawable.ic_navigation)
            PButton("WhatsApp", { ctx.openWhatsApp(a) }, Modifier.weight(1f), BtnVariant.Outline, small = true, icon = R.drawable.ic_message_circle)
        }
        PButton("Lihat Detail", onDetail, Modifier.padding(top = 8.dp).fillMaxWidth(), icon = R.drawable.ic_chevron_right)
    }
}

@Composable
fun DashboardScreen(list: List<Assignment>, user: User, onDetail: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf("Semua") }
    var bell by remember { mutableStateOf(false) }
    fun count(s: Status) = list.count { it.status == s }
    val fresh = list.filter { it.status == Status.Belum && isToday(it.assignedAt) }
    val counts = mapOf("Semua" to list.size, "Belum" to count(Status.Belum), "Proses" to count(Status.Proses), "Selesai" to count(Status.Selesai))
    val shown = list.filter { (tab == "Semua" || it.status.label == tab) && (it.name + it.custNo).contains(q, ignoreCase = true) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        PhotoHero(shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)) {
            Column(Modifier.fillMaxWidth().statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 64.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.teknisi_andi), "Foto teknisi", Modifier.size(44.dp).clip(CircleShape).border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape), contentScale = ContentScale.Crop)
                    Column(Modifier.weight(1f)) {
                        PText("${greeting()}, ${user.name.split(" ").first()}", size = 12, color = Color.White.copy(alpha = 0.7f))
                        PText(user.name, size = 16, weight = FontWeight.Bold, maxLines = 1)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Ic(R.drawable.ic_building, 11.dp, tint = Color.White.copy(alpha = 0.7f))
                            PText(user.branch, size = 11, color = Color.White.copy(alpha = 0.7f), lineHeight = 1.3f)
                        }
                    }
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.12f)).clickable { bell = true }, contentAlignment = Alignment.Center) {
                        Ic(R.drawable.ic_bell, 20.dp)
                        if (fresh.isNotEmpty()) Box(Modifier.align(Alignment.TopEnd).size(20.dp).background(Warn, CircleShape), contentAlignment = Alignment.Center) {
                            PText("${fresh.size}", size = 10, weight = FontWeight.Bold, color = Color.White, lineHeight = 1f)
                        }
                    }
                }
                PText("PENUGASAN PERGANTIAN WATER METER", Modifier.padding(top = 20.dp), size = 11, weight = FontWeight.SemiBold, color = Color(0xFF8FD3F7), letterSpacing = 1.5f)
            }
        }
        Row(Modifier.offset(y = (-32).dp).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            data class Stat(val label: String, val sub: String, val v: Int, val icon: Int, val bg: Color, val fg: Color)
            listOf(
                Stat("Belum", "Menunggu konfirmasi", count(Status.Belum), R.drawable.ic_clipboard_check, Muted.copy(alpha = 0.15f), GrayText),
                Stat("Proses", "Sedang dikerjakan", count(Status.Proses), R.drawable.ic_clock, Warn.copy(alpha = 0.15f), WarnText),
                Stat("Selesai", "Pekerjaan tuntas", count(Status.Selesai), R.drawable.ic_check_circle2, Success.copy(alpha = 0.12f), Success),
            ).forEach { s ->
                PCard(Modifier.weight(1f), padding = PaddingValues(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(32.dp).background(s.bg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Ic(s.icon, 16.dp, tint = s.fg) }
                        PText("${s.v}", size = 24, weight = FontWeight.Bold, lineHeight = 1f)
                    }
                    PText(s.label, Modifier.padding(top = 8.dp), size = 12, weight = FontWeight.Bold)
                    PText(s.sub, size = 10, weight = FontWeight.Medium, color = navy(0.5f), lineHeight = 1.2f)
                }
            }
        }
        Column(Modifier.offset(y = (-16).dp).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PText("Daftar Penugasan", size = 16, weight = FontWeight.Bold)
            SearchBar(q, { q = it })
            Segmented(listOf("Semua", "Belum", "Proses", "Selesai"), tab, { tab = it }, counts)
            if (shown.isEmpty()) PCard { PText("Tidak ada penugasan ditemukan.", Modifier.fillMaxWidth().padding(vertical = 24.dp), size = 14, color = navy(0.5f), align = TextAlign.Center) }
            else shown.forEach { AssignmentCard(it) { onDetail(it.id) } }
        }
        Spacer(Modifier.navigationBarsPadding().height(80.dp))
    }

    if (bell) Modal({ bell = false }) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Ic(R.drawable.ic_bell, 18.dp, tint = id.pamtirtakemuning.watermeter.ui.theme.Sky)
            PText("Notifikasi", size = 16, weight = FontWeight.Bold)
        }
        Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (fresh.isEmpty()) PText("Tidak ada penugasan baru.", size = 14, color = navy(0.6f))
            fresh.forEach { a ->
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Ice).clickable { bell = false; onDetail(a.id) }.padding(12.dp)) {
                    PText("Penugasan baru · ${a.assignNo}", size = 12, weight = FontWeight.SemiBold, color = id.pamtirtakemuning.watermeter.ui.theme.Sky)
                    PText(a.name, size = 14, weight = FontWeight.Bold)
                    PText(a.damage, size = 12, color = navy(0.6f))
                }
            }
        }
        PButton("Tutup", { bell = false }, Modifier.padding(top = 16.dp).fillMaxWidth(), BtnVariant.Outline)
    }
}
