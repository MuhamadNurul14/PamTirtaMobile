package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.Options
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.InfoRow
import id.pamtirtakemuning.watermeter.ui.components.InfoRowC
import id.pamtirtakemuning.watermeter.ui.components.KvRow
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PageScaffold
import id.pamtirtakemuning.watermeter.ui.components.PhotoThumb
import id.pamtirtakemuning.watermeter.ui.components.PhotoViewer
import id.pamtirtakemuning.watermeter.ui.components.Section
import id.pamtirtakemuning.watermeter.ui.components.StatusBadge
import id.pamtirtakemuning.watermeter.ui.components.annotated
import id.pamtirtakemuning.watermeter.ui.components.bold
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.fmtDate
import id.pamtirtakemuning.watermeter.util.fmtDateTime
import id.pamtirtakemuning.watermeter.util.fmtDuration
import java.io.File

/** Layar sukses setelah Konfirmasi Penyelesaian. */
@Composable
fun SuccessScreen(a: Assignment, onResult: () -> Unit, onHome: () -> Unit) {
    val r = a.report
    Column(Modifier.fillMaxSize().background(Color.White).statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.padding(top = 64.dp).size(112.dp).background(Success.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
                Box(Modifier.size(80.dp).shadow(8.dp, CircleShape, ambientColor = Success, spotColor = Success).background(Success, CircleShape), contentAlignment = Alignment.Center) {
                    Ic(R.drawable.ic_check_circle2, 44.dp, tint = Color.White)
                }
            }
            PText("Pengerjaan Selesai", Modifier.padding(top = 24.dp), size = 20, weight = FontWeight.Bold)
            PText(
                annotated {
                    append("Data ${a.jobType.label.lowercase()} water meter berhasil dikirim dan status penugasan kini ")
                    bold("Selesai")
                    append(".")
                },
                Modifier.padding(top = 8.dp), size = 14, color = navy(0.6f), align = TextAlign.Center, lineHeight = 1.5f,
            )
            PCard(Modifier.padding(top = 24.dp), color = Mist, shadow = false, borderColor = null) {
                InfoRow("Nomor Penugasan", a.assignNo)
                InfoRow("Nama Pelanggan", a.name)
                InfoRow("Waktu Selesai", r?.doneAt?.let { fmtDateTime(it) } ?: "-")
                val start = a.confirmation?.at
                if (start != null && r != null && r.doneAt > 0) InfoRow("Durasi Pengerjaan", fmtDuration(start, r.doneAt))
                InfoRowC("Status") { StatusBadge(Status.Selesai, Modifier.padding(top = 2.dp)) }
            }
        }
        Column(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PButton("Lihat Hasil Pengerjaan", onResult, Modifier.fillMaxWidth(), icon = R.drawable.ic_file_text)
            PButton("Kembali ke Daftar Tugas", onHome, Modifier.fillMaxWidth(), BtnVariant.Outline)
        }
    }
}

/** Tahap 3: Hasil Pengerjaan (hanya lihat / arsip). */
@Composable
fun ResultScreen(a: Assignment, onBack: () -> Unit) {
    val r = a.report ?: return
    val c = a.confirmation
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    val times = listOf(
        Triple(R.drawable.ic_calendar, "Mulai", c?.let { fmtDateTime(it.at) } ?: "-"),
        Triple(R.drawable.ic_check_circle2, "Selesai", if (r.doneAt > 0) fmtDateTime(r.doneAt) else "-"),
        Triple(R.drawable.ic_timer, "Durasi", if (c != null && r.doneAt > 0) fmtDuration(c.at, r.doneAt) else "-"),
    )
    PageScaffold("HASIL PENGERJAAN", onBack) {
        PCard {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).background(Success.copy(alpha = 0.12f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Ic(R.drawable.ic_check_circle2, 24.dp, tint = Success) }
                Column(Modifier.weight(1f)) {
                    PText("${a.assignNo} · ${a.jobType.label}", size = 11, color = navy(0.5f))
                    PText(a.name, size = 16, weight = FontWeight.Bold, maxLines = 1)
                    PText("No. ${a.custNo}", size = 12, color = Sky)
                }
                StatusBadge(Status.Selesai)
            }
        }
        JobStepper(Status.Selesai)

        PCard {
            Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(R.drawable.ic_clock, 16.dp, tint = Sky)
                PText("Waktu Pengerjaan", size = 14, weight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                times.forEach { (icon, label, value) ->
                    Column(Modifier.weight(1f).background(Ice, RoundedCornerShape(16.dp)).padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Ic(icon, 11.dp, tint = navy(0.5f))
                            PText(label, size = 10, weight = FontWeight.Medium, color = navy(0.5f), lineHeight = 1.3f)
                        }
                        PText(value, Modifier.padding(top = 2.dp), size = 11, weight = FontWeight.Bold, lineHeight = 1.3f)
                    }
                }
            }
        }

        Section("Informasi Penugasan", R.drawable.ic_file_text) {
            InfoRow("Jenis Penugasan", "${a.jobType.label} Water Meter")
            InfoRowC("Alasan") {
                PText(a.damage, size = 14, weight = FontWeight.SemiBold, lineHeight = 1.3f)
                PText(a.reason, Modifier.padding(top = 2.dp), size = 12, color = navy(0.6f), lineHeight = 1.6f)
            }
            InfoRow("Tanggal Penugasan", fmtDate(a.assignedAt))
            InfoRow("Petugas / Cabang", "${a.officer} · ${a.branch}")
        }
        Section("Informasi Pelanggan", R.drawable.ic_user_round) {
            InfoRow("Alamat", a.address, R.drawable.ic_map_pin)
            InfoRow("Tarif / Golongan", a.tariff, R.drawable.ic_tag)
            InfoRow("Cabang", a.branch, R.drawable.ic_building)
        }

        PCard {
            PText("Dokumentasi Awal · Water Meter Lama", size = 14, weight = FontWeight.Bold)
            PText("No. seri ${a.oldSerial}" + (c?.let { " · ${fmtDateTime(it.at)}" } ?: ""), Modifier.padding(bottom = 12.dp), size = 11, color = navy(0.5f), lineHeight = 1.4f)
            PhotoThumb("Foto Water Meter Lama", c?.oldPhoto) { preview = it }
        }

        Section("Data Water Meter Baru", R.drawable.ic_gauge) {
            KvRow("Merk / Tipe", "${r.brand} · ${r.meterType}")
            KvRow("Nomor Seri", r.newSerial, mono = true)
            KvRow("Stand Lalu / Sekarang", "${r.standLast} / ${r.standNow} m³")
            KvRow("Jumlah Digit", r.digits)
            KvRow("Nomor Segel", r.seal, mono = true)
        }
        PCard {
            PText("Foto Water Meter Baru", Modifier.padding(bottom = 12.dp), size = 14, weight = FontWeight.Bold)
            PhotoThumb("Hasil Pemasangan", r.newPhoto) { preview = it }
        }

        Section("Hasil Pemeriksaan Kondisi", R.drawable.ic_clipboard_check) {
            (Options.conditionKeys + Options.POSITION_KEY).forEach { k ->
                KvRow(k.replace("Kondisi ", ""), r.conditions[k] ?: "-", color = if (r.conditions[k] == "Rusak") Danger else Color.Unspecified)
            }
            InfoRow("Catatan Teknisi", r.notes.ifEmpty { "-" })
        }

        PCard {
            PText("Tanda Tangan Teknisi", Modifier.padding(bottom = 8.dp), size = 14, weight = FontWeight.Bold)
            Box(Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, navy(0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                if (r.signature != null) AsyncImage(File(r.signature), "Tanda tangan teknisi", Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                else Text(a.officer, color = navy(0.7f), fontSize = 30.sp, fontFamily = FontFamily.Cursive, textAlign = TextAlign.Center)
            }
            PText("${a.officer} · Teknisi", Modifier.padding(top = 4.dp).fillMaxWidth(), size = 12, color = navy(0.6f), align = TextAlign.Center)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            Ic(R.drawable.ic_lock, 12.dp, tint = navy(0.5f))
            PText("Hasil pengerjaan bersifat arsip dan tidak dapat diedit.", size = 11, color = navy(0.5f))
        }
    }
    preview?.let { PhotoViewer(it) { preview = null } }
}
