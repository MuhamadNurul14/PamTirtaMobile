package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PageScaffold
import id.pamtirtakemuning.watermeter.ui.components.SearchBar
import id.pamtirtakemuning.watermeter.ui.components.Segmented
import id.pamtirtakemuning.watermeter.ui.components.StatusBadge
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.fmtDate

@Composable
fun HistoryScreen(list: List<Assignment>, onDetail: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    var range by remember { mutableStateOf("Semua") }
    var status by remember { mutableStateOf("Semua") }
    val done = list.filter { it.status == Status.Selesai && it.report != null }
    val now = System.currentTimeMillis()
    val shown = done
        .filter { (it.name + it.custNo).contains(q, ignoreCase = true) }
        .filter { range == "Semua" || (now - it.report!!.doneAt) / 86_400_000.0 <= (if (range == "7 Hari") 8 else 31) }
        .sortedByDescending { it.report!!.doneAt }

    PageScaffold("RIWAYAT PENUGASAN", bar = { Spacer(Modifier.navigationBarsPadding().height(80.dp)) }) {
        PCard(color = Navy, contentColor = Color.White, borderColor = null) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Ic(R.drawable.ic_badge_check, 26.dp) }
                Column {
                    PText("Total Pekerjaan Selesai", size = 11, color = Color.White.copy(alpha = 0.7f))
                    PText("${done.size}", size = 30, weight = FontWeight.Bold, lineHeight = 1.15f)
                }
            }
        }
        SearchBar(q, { q = it })
        Segmented(listOf("Semua", "7 Hari", "30 Hari"), range, { range = it })
        Segmented(listOf("Semua", "Selesai"), status, { status = it })
        if (shown.isEmpty()) PCard { PText("Belum ada riwayat yang sesuai.", Modifier.fillMaxWidth().padding(vertical = 24.dp), size = 14, color = navy(0.5f), align = TextAlign.Center) }
        shown.forEach { a ->
            val r = a.report!!
            PCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        PText(a.name, size = 15, weight = FontWeight.Bold, maxLines = 1)
                        PText("No. ${a.custNo}", size = 12, weight = FontWeight.Medium, color = Sky)
                    }
                    StatusBadge(Status.Selesai)
                }
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ic(R.drawable.ic_map_pin, 14.dp, Modifier.padding(top = 2.dp), tint = navy(0.6f))
                    PText(a.address, size = 12, color = navy(0.6f), lineHeight = 1.5f)
                }
                Row(Modifier.padding(top = 12.dp).fillMaxWidth().background(Ice, RoundedCornerShape(12.dp)).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Cell("Tgl Selesai", fmtDate(r.doneAt), Modifier.weight(1f))
                    Cell("Merk Baru", r.brand, Modifier.weight(1f))
                    Cell("Seri Baru", r.newSerial, Modifier.weight(1f), mono = true, size = 11)
                }
                PButton("Lihat Hasil Pengerjaan", { onDetail(a.id) }, Modifier.padding(top = 12.dp).fillMaxWidth(), BtnVariant.Outline, small = true, icon = R.drawable.ic_file_text)
            }
        }
    }
}

@Composable
private fun Cell(label: String, value: String, modifier: Modifier, mono: Boolean = false, size: Int = 12) {
    Column(modifier) {
        PText(label, size = 10, color = navy(0.5f), lineHeight = 1.3f)
        PText(value, size = size, weight = FontWeight.SemiBold, mono = mono, lineHeight = 1.3f)
    }
}
