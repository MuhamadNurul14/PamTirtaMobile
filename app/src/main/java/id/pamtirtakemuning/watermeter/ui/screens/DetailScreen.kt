package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.KondisiFisikWmLama
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.ui.components.ActionBar
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.IconChip
import id.pamtirtakemuning.watermeter.ui.components.InfoRow
import id.pamtirtakemuning.watermeter.ui.components.KvRow
import id.pamtirtakemuning.watermeter.ui.components.Modal
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PTextArea
import id.pamtirtakemuning.watermeter.ui.components.PageScaffold
import id.pamtirtakemuning.watermeter.ui.components.PhotoCapture
import id.pamtirtakemuning.watermeter.ui.components.PhotoThumb
import id.pamtirtakemuning.watermeter.ui.components.PhotoViewer
import id.pamtirtakemuning.watermeter.ui.components.Section
import id.pamtirtakemuning.watermeter.ui.components.SelectField
import id.pamtirtakemuning.watermeter.ui.components.StatusBadge
import id.pamtirtakemuning.watermeter.ui.components.annotated
import id.pamtirtakemuning.watermeter.ui.components.bold
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.DangerBg
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.Photos
import id.pamtirtakemuning.watermeter.util.fmtDate
import id.pamtirtakemuning.watermeter.util.fmtDateTime
import id.pamtirtakemuning.watermeter.util.phoneFmt
import java.io.File

private val photoTips = listOf("Nomor seri water meter terbaca jelas", "Angka stand terlihat dan tidak buram", "Foto diambil langsung di lokasi pemasangan")

/** Detail Penugasan dengan konfirmasi WM lama, perbaikan data (Ditolak), dan status Validasi Pusat. */
@Composable
fun DetailScreen(
    a: Assignment,
    onBack: () -> Unit,
    onConfirm: (photo: String, kondisi: KondisiFisikWmLama, notes: String) -> Unit,
    onExecute: () -> Unit,
    onResult: () -> Unit,
) {
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    var photo by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedKondisi by remember { mutableStateOf(KondisiFisikWmLama.Normal) }
    var oldNotes by remember { mutableStateOf("") }
    var ask by rememberSaveable { mutableStateOf(false) }

    val canConfirm = photo != null

    val bar: (@Composable () -> Unit)? = when (a.status) {
        Status.Belum -> null
        Status.Proses -> ({
            ActionBar("Input data water meter baru, foto, dan tanda tangan.") {
                PButton("Eksekusi Pengerjaan", onExecute, Modifier.fillMaxWidth(), large = true, icon = R.drawable.ic_wrench)
            }
        })
        Status.Ditolak -> ({
            ActionBar("Perbarui data penugasan yang dinyatakan tidak sesuai.") {
                PButton("Input Data Perbaikan", onExecute, Modifier.fillMaxWidth(), BtnVariant.Danger, large = true, icon = R.drawable.ic_wrench)
            }
        })
        Status.Validasi -> ({
            ActionBar("Laporan dikirim. Menunggu validasi dari tim Pusat.") {
                PButton("Lihat Data Terkirim", onResult, Modifier.fillMaxWidth(), BtnVariant.Outline, large = true, icon = R.drawable.ic_file_text)
            }
        })
        Status.Selesai -> ({
            ActionBar("Pengerjaan telah selesai dan terarsip.") {
                PButton("Lihat Hasil Pengerjaan", onResult, Modifier.fillMaxWidth(), BtnVariant.Success, large = true, icon = R.drawable.ic_file_text)
            }
        })
    }

    PageScaffold("DETAIL PENUGASAN", onBack, bar = bar) {
        JobHeader(a)
        JobStepper(a.status)

        if (a.status == Status.Ditolak) {
            PCard(color = DangerBg, borderColor = Danger.copy(alpha = 0.35f)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconChip(R.drawable.ic_triangle_alert, Danger.copy(alpha = 0.15f), Danger)
                        Column(Modifier.weight(1f)) {
                            PText("PENUGASAN DITOLAK (MEMERLUKAN PERBAIKAN)", size = 11, weight = FontWeight.Bold, color = Danger, letterSpacing = 0.5f)
                            PText("Perbaikan dilakukan dengan memperbarui data pada penugasan yang sama tanpa perlu bukti pergantian baru.", size = 12, color = navy(0.7f), lineHeight = 1.4f)
                        }
                    }
                    if (!a.rejectionReason.isNullOrEmpty()) {
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp)) {
                            PText("Catatan Verifikator:", size = 11, weight = FontWeight.Bold, color = Danger)
                            PText(a.rejectionReason, Modifier.padding(top = 2.dp), size = 12, color = navy(0.8f), lineHeight = 1.4f)
                        }
                    }
                    PButton("Input Data Perbaikan", onExecute, Modifier.fillMaxWidth(), BtnVariant.Danger, icon = R.drawable.ic_wrench)
                }
            }
        }

        if (a.status == Status.Validasi) {
            PCard(color = Ice, borderColor = Sky.copy(alpha = 0.35f)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconChip(R.drawable.ic_shield_check, Sky.copy(alpha = 0.15f), Sky)
                        Column(Modifier.weight(1f)) {
                            PText("MENUNGGU VALIDASI PUSAT", size = 11, weight = FontWeight.Bold, color = Sky, letterSpacing = 0.5f)
                            PText("Laporan pengerjaan water meter baru telah dikirim dan sedang dalam proses validasi oleh tim Pusat.", size = 12, color = navy(0.7f), lineHeight = 1.4f)
                        }
                    }
                    PButton("Lihat Data Terkirim", onResult, Modifier.fillMaxWidth(), BtnVariant.Outline, icon = R.drawable.ic_file_text)
                }
            }
        }

        ReasonCard(a)
        Section("Informasi Pelanggan", R.drawable.ic_user_round) {
            InfoRow("Nama Pelanggan", a.name, R.drawable.ic_user)
            InfoRow("Nomor Pelanggan", a.custNo, R.drawable.ic_hash)
            InfoRow("Tarif / Golongan", a.tariff, R.drawable.ic_tag)
            InfoRow("Alamat Lengkap", a.address, R.drawable.ic_building)
            InfoRow("Nomor Telepon", phoneFmt(a.phone), R.drawable.ic_phone)
        }
        Section("Water Meter & Penugasan", R.drawable.ic_gauge) {
            InfoRow("Nomor Water Meter", a.oldSerial, mono = true)
            InfoRow("Jenis Penugasan", "${a.jobType.label} Water Meter")
            InfoRow("Nomor Penugasan", a.assignNo)
            InfoRow("Tanggal Penugasan", fmtDate(a.assignedAt))
            InfoRow("Petugas / Cabang", "${a.officer} · ${a.branch}")
            InfoRow("Catatan", a.notes.ifEmpty { "-" })
        }

        if (a.status == Status.Belum) {
            PCard(borderColor = Sky.copy(alpha = 0.3f)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconChip(R.drawable.ic_camera, Sky, Color.White)
                        Column(Modifier.weight(1f)) {
                            PText("Konfirmasi Pengerjaan (WM Lama)", size = 14, weight = FontWeight.Bold)
                            PText("Unggah foto WM lama & pilih kondisi fisik sebelum pengerjaan dimulai.", Modifier.padding(top = 2.dp), size = 11, color = navy(0.55f), lineHeight = 1.3f)
                        }
                    }
                    PhotoCapture("Foto Water Meter Lama", if (photo != null) "Ketuk foto untuk melihat pratinjau penuh" else "No. seri ${a.oldSerial}", photo, { photo = it }, { preview = it })

                    SelectField(
                        label = "Kondisi Fisik Water Meter Lama",
                        value = selectedKondisi.label,
                        onChange = { lbl -> selectedKondisi = KondisiFisikWmLama.entries.find { it.label == lbl } ?: KondisiFisikWmLama.Normal },
                        options = KondisiFisikWmLama.entries.map { it.label },
                        placeholder = "Pilih kondisi fisik"
                    )
                    PTextArea(oldNotes, { oldNotes = it }, "Catatan Kondisi Awal (opsional)", "Tuliskan kondisi fisik WM lama sebelum dilepas")

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        photoTips.forEach { t ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(16.dp).background(Success.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) { Ic(R.drawable.ic_check, 10.dp, tint = Success) }
                                PText(t, size = 12, color = navy(0.65f))
                            }
                        }
                    }
                    Column {
                        PButton("Konfirmasi Pengerjaan", { ask = true }, Modifier.fillMaxWidth(), BtnVariant.Secondary, large = true, icon = R.drawable.ic_check_circle2, enabled = canConfirm)
                        Row(Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                            Ic(R.drawable.ic_info, 12.dp, tint = navy(0.55f))
                            PText(if (canConfirm) "Status berubah dari Belum menjadi Proses setelah konfirmasi." else "Unggah foto water meter lama untuk mengaktifkan tombol.",
                                Modifier.weight(1f, fill = false), size = 11, color = navy(0.55f), align = TextAlign.Center, lineHeight = 1.3f)
                        }
                    }
                }
            }
        } else a.confirmation?.let { c ->
            PCard {
                Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconChip(R.drawable.ic_check_circle, Success.copy(alpha = 0.12f), Success)
                    Column(Modifier.weight(1f)) {
                        PText("Dokumentasi Awal · Water Meter Lama", size = 14, weight = FontWeight.Bold)
                        PText("Dikonfirmasi ${fmtDateTime(c.at)}", size = 11, color = navy(0.5f), lineHeight = 1.3f)
                    }
                }
                PhotoThumb("Foto WM lama · ${a.oldSerial}", c.oldPhoto) { preview = it }
                Column(Modifier.padding(top = 12.dp).fillMaxWidth().background(Ice, RoundedCornerShape(12.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KvRow("Kondisi Fisik WM Lama", c.kondisiFisik.label)
                    if (c.notes.isNotEmpty()) KvRow("Catatan Awal", c.notes)
                }
            }
        }
    }

    preview?.let { PhotoViewer(it) { preview = null } }
    val p = photo
    if (ask && p != null) Modal({ ask = false }) {
        PText("Konfirmasi Pengerjaan?", size = 16, weight = FontWeight.Bold)
        PText(
            annotated {
                append("Foto & data kondisi WM lama akan disimpan dan status penugasan berubah menjadi ")
                bold("Proses")
                append(".")
            },
            Modifier.padding(top = 6.dp), size = 14, color = navy(0.6f), lineHeight = 1.5f,
        )
        Row(Modifier.padding(top = 12.dp).fillMaxWidth().background(Mist, RoundedCornerShape(16.dp)).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(Photos.resolveUrl(p), "Foto water meter lama", Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(Status.Belum); PText("→", color = navy(0.4f)); StatusBadge(Status.Proses)
            }
        }
        Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PButton("Batal", { ask = false }, Modifier.weight(1f), BtnVariant.Outline)
            PButton("Ya, Konfirmasi", { ask = false; onConfirm(p, selectedKondisi, oldNotes); photo = null }, Modifier.weight(1f), BtnVariant.Secondary)
        }
    }
}
