package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.Options
import id.pamtirtakemuning.watermeter.data.Report
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.data.User
import id.pamtirtakemuning.watermeter.ui.components.ActionBar
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.IconChip
import id.pamtirtakemuning.watermeter.ui.components.KvRow
import id.pamtirtakemuning.watermeter.ui.components.KvRowC
import id.pamtirtakemuning.watermeter.ui.components.Modal
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PInput
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PTextArea
import id.pamtirtakemuning.watermeter.ui.components.PageScaffold
import id.pamtirtakemuning.watermeter.ui.components.PhotoCapture
import id.pamtirtakemuning.watermeter.ui.components.PhotoThumb
import id.pamtirtakemuning.watermeter.ui.components.PhotoViewer
import id.pamtirtakemuning.watermeter.ui.components.RadioButtons
import id.pamtirtakemuning.watermeter.ui.components.Section
import id.pamtirtakemuning.watermeter.ui.components.SelectField
import id.pamtirtakemuning.watermeter.ui.components.SignaturePad
import id.pamtirtakemuning.watermeter.ui.components.StatusBadge
import id.pamtirtakemuning.watermeter.ui.components.annotated
import id.pamtirtakemuning.watermeter.ui.components.bold
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.Warn
import id.pamtirtakemuning.watermeter.ui.theme.WarnText
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.fmtDateTime
import java.io.File

private data class Check(val label: String, val ok: Boolean)

/** Tahap 2: Eksekusi Pengerjaan (data WM baru, foto WM baru, tanda tangan) + ringkasan sebelum penyelesaian. */
@Composable
fun ExecuteScreen(
    a: Assignment,
    user: User,
    onBack: () -> Unit,
    onDraft: (Report) -> Unit,
    onSubmit: (Report) -> Unit,
    toast: (String) -> Unit,
) {
    var f by remember { mutableStateOf(a.draft ?: Report()) }
    var review by rememberSaveable { mutableStateOf(false) }
    var ok by rememberSaveable { mutableStateOf(false) }
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    var confirm by rememberSaveable { mutableStateOf(false) }
    var invalid by remember { mutableStateOf<List<String>?>(null) }
    var first by remember { mutableStateOf(true) }
    // simpan otomatis sebagai draft setiap ada perubahan form
    LaunchedEffect(f) { if (first) first = false else onDraft(f) }

    val allKeys = Options.conditionKeys + Options.POSITION_KEY
    val standBad = f.standLast.isNotEmpty() && f.standNow.isNotEmpty() && (f.standNow.toLongOrNull() ?: 0) < (f.standLast.toLongOrNull() ?: 0)
    val checks = listOf(
        Check("Data water meter baru sudah diisi", listOf(f.brand, f.meterType, f.newSerial, f.standLast, f.standNow, f.digits, f.seal).all { it.isNotEmpty() } && !standBad),
        Check("Pemeriksaan kondisi sudah dilakukan", allKeys.all { f.conditions[it]?.isNotEmpty() == true }),
        Check("Foto water meter baru sudah diunggah", f.newPhoto != null),
        Check("Tanda tangan teknisi sudah diberikan", f.signature != null),
    )
    val pct = Math.round(checks.count { it.ok } * 100f / checks.size)
    fun toReview() {
        val miss = checks.filter { !it.ok }.map { it.label.replace("sudah ", "belum ") }.toMutableList()
        if (standBad) miss.add(0, "Stand sekarang lebih kecil dari stand bulan lalu")
        if (miss.isNotEmpty()) { invalid = miss; return }
        ok = false
        review = true
    }
    val startedAt = a.confirmation?.at
    val oldPhoto = a.confirmation?.oldPhoto

    if (review) {
        PageScaffold(
            "RINGKASAN PENGERJAAN", { review = false },
            bar = {
                ActionBar(if (ok) "Status berubah dari Proses menjadi Selesai." else "Centang pernyataan untuk melanjutkan.") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PButton("Edit", { review = false }, variant = BtnVariant.Outline)
                        PButton("Konfirmasi Penyelesaian", { confirm = true }, Modifier.weight(1f), BtnVariant.Success, icon = R.drawable.ic_check_circle2, enabled = ok)
                    }
                }
            },
        ) {
            Row(Modifier.fillMaxWidth().background(Ice, RoundedCornerShape(24.dp)).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconChip(R.drawable.ic_clipboard_check, Color.White, Navy, size = 44.dp, iconSize = 22.dp, radius = 16.dp)
                PText(
                    annotated {
                        append("Periksa kembali seluruh data sebelum menekan ")
                        bold("Konfirmasi Penyelesaian")
                        append(". Data tidak dapat diubah setelah dikirim.")
                    },
                    Modifier.weight(1f), size = 12, color = navy(0.7f), lineHeight = 1.6f,
                )
            }
            Section("Penugasan", R.drawable.ic_file_text) {
                KvRow("Pelanggan", a.name)
                KvRow("No. Pelanggan", a.custNo)
                KvRow("Jenis / Alasan", "${a.jobType.label} · ${a.damage}")
                KvRow("Mulai Dikerjakan", startedAt?.let { fmtDateTime(it) } ?: "-")
            }
            PCard {
                PText("Dokumentasi Foto", Modifier.padding(bottom = 12.dp), size = 14, weight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { PhotoThumb("WM Lama (Awal)", oldPhoto) { preview = it } }
                    Box(Modifier.weight(1f)) { PhotoThumb("WM Baru (Hasil)", f.newPhoto) { preview = it } }
                }
            }
            Section("Data Water Meter Baru", R.drawable.ic_gauge) {
                KvRow("Merk / Tipe", "${f.brand} · ${f.meterType}")
                KvRow("Nomor Seri Baru", f.newSerial, mono = true)
                KvRow("Nomor Seri Lama", a.oldSerial, mono = true)
                KvRow("Stand Lalu / Sekarang", "${f.standLast} / ${f.standNow} m³")
                KvRow("Jumlah Digit", f.digits)
                KvRow("Nomor Segel", f.seal, mono = true)
            }
            Section("Kondisi Water Meter", R.drawable.ic_clipboard_check) {
                allKeys.forEach { k -> KvRow(k.replace("Kondisi ", ""), f.conditions[k] ?: "-", color = if (f.conditions[k] == "Rusak") Danger else Color.Unspecified) }
                if (f.notes.isNotEmpty()) KvRow("Catatan", f.notes)
            }
            PCard {
                PText("Tanda Tangan Teknisi", Modifier.padding(bottom = 8.dp), size = 14, weight = FontWeight.Bold)
                Box(Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, navy(0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    f.signature?.let { AsyncImage(File(it), "Tanda tangan teknisi", Modifier.fillMaxSize(), contentScale = ContentScale.Fit) }
                }
                PText("${user.name} · Teknisi", Modifier.padding(top = 4.dp).fillMaxWidth(), size = 12, color = navy(0.6f), align = TextAlign.Center)
            }
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, navy(0.1f), RoundedCornerShape(16.dp)).clickable { ok = !ok }.padding(end = 14.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Checkbox(ok, { ok = it }, colors = CheckboxDefaults.colors(checkedColor = Navy))
                PText("Saya menyatakan data dan dokumentasi sudah benar dan pekerjaan water meter telah selesai dilakukan.", Modifier.padding(top = 12.dp), size = 12, weight = FontWeight.Medium, lineHeight = 1.6f)
            }
        }
        preview?.let { PhotoViewer(it) { preview = null } }
        if (confirm) Modal({ confirm = false }) {
            PText("Selesaikan Penugasan?", size = 16, weight = FontWeight.Bold)
            PText(
                annotated {
                    append("Laporan akan dikirim dan status berubah menjadi ")
                    bold("Selesai")
                    append(". Data tidak dapat diubah setelah dikirim.")
                },
                Modifier.padding(top = 6.dp), size = 14, color = navy(0.6f), lineHeight = 1.5f,
            )
            Row(Modifier.padding(top = 12.dp).fillMaxWidth().background(Mist, RoundedCornerShape(16.dp)).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(Status.Proses); PText("→", color = navy(0.4f)); StatusBadge(Status.Selesai)
            }
            Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PButton("Periksa Lagi", { confirm = false }, Modifier.weight(1f), BtnVariant.Outline)
                PButton("Ya, Selesaikan", { confirm = false; onSubmit(f.copy(doneAt = System.currentTimeMillis())) }, Modifier.weight(1f), BtnVariant.Success)
            }
        }
        return
    }

    PageScaffold(
        "EKSEKUSI PENGERJAAN", onBack,
        bar = {
            ActionBar("Tinjau ringkasan sebelum konfirmasi penyelesaian.") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PButton("Draft", { onDraft(f); toast("Draft tersimpan"); onBack() }, Modifier.weight(1f), BtnVariant.Outline, icon = R.drawable.ic_save)
                    PButton("Lihat Ringkasan", { toReview() }, Modifier.weight(2.2f), icon = R.drawable.ic_clipboard_check)
                }
            }
        },
    ) {
        PCard(padding = PaddingValues(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconChip(R.drawable.ic_wrench, Warn.copy(alpha = 0.15f), WarnText, size = 44.dp, iconSize = 19.dp, radius = 16.dp)
                Column(Modifier.weight(1f)) {
                    PText(a.name, size = 14, weight = FontWeight.Bold, maxLines = 1)
                    PText("No. ${a.custNo} · ${a.jobType.label}", size = 11, color = navy(0.55f), lineHeight = 1.3f)
                }
                StatusBadge(Status.Proses)
            }
            Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(8.dp).clip(CircleShape).background(Mist)) { Box(Modifier.fillMaxHeight().fillMaxWidth(pct / 100f).background(Sky, CircleShape)) }
                PText("$pct%", size = 11, weight = FontWeight.Bold, lineHeight = 1.2f)
            }
            PText("Progres tersimpan otomatis sebagai draft.", Modifier.padding(top = 6.dp), size = 10, color = navy(0.5f), lineHeight = 1.3f)
        }
        PCard {
            Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                IconChip(R.drawable.ic_check_circle, Success.copy(alpha = 0.12f), Success)
                Column(Modifier.weight(1f)) {
                    PText("Referensi Water Meter Lama", size = 14, weight = FontWeight.Bold)
                    PText("Dokumentasi awal" + (startedAt?.let { " · ${fmtDateTime(it)}" } ?: ""), size = 11, color = navy(0.5f), lineHeight = 1.3f)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(96.dp).clip(RoundedCornerShape(16.dp)).background(Ice).clickable(enabled = oldPhoto != null) { preview = oldPhoto }, contentAlignment = Alignment.Center) {
                    if (oldPhoto != null) AsyncImage(File(oldPhoto), "Foto water meter lama", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Ic(R.drawable.ic_camera, 20.dp, tint = navy(0.35f))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Column { PText("No. Seri Lama", size = 10, color = navy(0.5f), lineHeight = 1.3f); PText(a.oldSerial, size = 12, weight = FontWeight.SemiBold, mono = true) }
                    Column { PText("Alasan ${a.jobType.label}", size = 10, color = navy(0.5f), lineHeight = 1.3f); PText(a.damage, size = 12, weight = FontWeight.SemiBold, color = WarnText) }
                    if (oldPhoto != null) PText("Ketuk foto untuk memperbesar", size = 10, color = Sky)
                }
            }
        }

        PCard {
            PText("1. Data Water Meter Baru", Modifier.padding(bottom = 4.dp), size = 14, weight = FontWeight.Bold)
            PText("Isi sesuai label pada unit water meter yang dipasang.", Modifier.padding(bottom = 12.dp), size = 11, color = navy(0.5f))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectField("Merk", f.brand, { f = f.copy(brand = it) }, Options.brands, "Pilih merk", Modifier.weight(1f))
                    SelectField("Tipe", f.meterType, { f = f.copy(meterType = it) }, Options.meterTypes, "Pilih tipe", Modifier.weight(1f))
                }
                PInput(f.newSerial, { f = f.copy(newSerial = it.uppercase()) }, label = "Nomor Seri Water Meter Baru", placeholder = "Contoh: ITR-26-004471", icon = R.drawable.ic_hash, capitalization = KeyboardCapitalization.Characters)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PInput(f.standLast, { f = f.copy(standLast = it.filter(Char::isDigit)) }, Modifier.weight(1f), label = "Stand Bulan Lalu", placeholder = "0000", keyboardType = KeyboardType.Number)
                    PInput(f.standNow, { f = f.copy(standNow = it.filter(Char::isDigit)) }, Modifier.weight(1f), label = "Stand Sekarang", placeholder = "0000", keyboardType = KeyboardType.Number)
                }
                if (standBad) PText("Stand sekarang tidak boleh lebih kecil dari bulan lalu.", size = 12, weight = FontWeight.Medium, color = Danger)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PInput(f.digits, { f = f.copy(digits = it.filter(Char::isDigit).take(2)) }, Modifier.weight(1f), label = "Jumlah Digit", placeholder = "5", keyboardType = KeyboardType.Number)
                    PInput(f.seal, { f = f.copy(seal = it.uppercase()) }, Modifier.weight(1f), label = "Nomor Segel", placeholder = "SG-208900", capitalization = KeyboardCapitalization.Characters)
                }
            }
        }

        PCard {
            PText("2. Pemeriksaan Kondisi Water Meter", Modifier.padding(bottom = 12.dp), size = 14, weight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                fun setCond(k: String, v: String) { f = f.copy(conditions = f.conditions + (k to v)) }
                Options.conditionKeys.forEach { k ->
                    Column { PText(k, Modifier.padding(bottom = 6.dp), size = 12, weight = FontWeight.SemiBold); RadioButtons(Options.conditionOpts, f.conditions[k]) { v -> setCond(k, v) } }
                }
                Column {
                    PText(Options.POSITION_KEY, Modifier.padding(bottom = 6.dp), size = 12, weight = FontWeight.SemiBold)
                    RadioButtons(Options.positionOpts, f.conditions[Options.POSITION_KEY]) { v -> setCond(Options.POSITION_KEY, v) }
                }
                PTextArea(f.notes, { f = f.copy(notes = it) }, "Catatan Teknisi (opsional)", "Tuliskan jika ditemukan kondisi khusus")
            }
        }

        PCard {
            PText("3. Foto Water Meter Baru", Modifier.padding(bottom = 4.dp), size = 14, weight = FontWeight.Bold)
            PText("Bukti hasil pengerjaan setelah water meter baru terpasang.", Modifier.padding(bottom = 12.dp), size = 11, color = navy(0.5f))
            PhotoCapture("Foto Hasil Pemasangan", "Pastikan nomor seri dan segel terlihat jelas", f.newPhoto, { f = f.copy(newPhoto = it) }, { preview = it })
        }

        PCard {
            PText("4. Tanda Tangan Teknisi", Modifier.padding(bottom = 4.dp), size = 14, weight = FontWeight.Bold)
            PText("Sebagai bukti bahwa pekerjaan telah dilaksanakan dan data sudah sesuai.", Modifier.padding(bottom = 12.dp), size = 11, color = navy(0.5f))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SignaturePad(f.signature) { f = f.copy(signature = it) }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ic(R.drawable.ic_pen_line, 14.dp, tint = Sky)
                    PText("${user.name} · Teknisi", size = 12, weight = FontWeight.SemiBold)
                }
            }
        }

        PCard {
            PText("Kelengkapan Data", Modifier.padding(bottom = 10.dp), size = 12, weight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                checks.forEach { c ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Ic(if (c.ok) R.drawable.ic_check_circle else R.drawable.ic_circle, 16.dp, tint = if (c.ok) Success else navy(0.5f))
                        PText(c.label, size = 12, weight = FontWeight.Medium, color = if (c.ok) Success else navy(0.5f))
                    }
                }
            }
        }
    }

    preview?.let { PhotoViewer(it) { preview = null } }
    invalid?.let { msgs ->
        Modal({ invalid = null }) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(R.drawable.ic_triangle_alert, 22.dp, tint = Warn)
                PText("Data Belum Lengkap", size = 16, weight = FontWeight.Bold)
            }
            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                msgs.forEach { m ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.padding(top = 8.dp).size(6.dp).background(Warn, CircleShape))
                        PText(m, size = 14, color = navy(0.7f))
                    }
                }
            }
            PButton("Lengkapi Sekarang", { invalid = null }, Modifier.padding(top = 16.dp).fillMaxWidth())
        }
    }
}