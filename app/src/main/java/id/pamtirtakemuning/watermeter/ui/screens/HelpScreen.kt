package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.components.BtnVariant
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.IconChip
import id.pamtirtakemuning.watermeter.ui.components.Logo
import id.pamtirtakemuning.watermeter.ui.components.Modal
import id.pamtirtakemuning.watermeter.ui.components.PButton
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.PTextArea
import id.pamtirtakemuning.watermeter.ui.components.PageScaffold
import id.pamtirtakemuning.watermeter.ui.components.RadioButtons
import id.pamtirtakemuning.watermeter.ui.components.Segmented
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Poppins
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.dial
import id.pamtirtakemuning.watermeter.util.openWhatsApp
import id.pamtirtakemuning.watermeter.util.sendMail

private val CATS = listOf("Semua", "Penugasan", "Eksekusi", "Akun")
private data class Faq(val c: String, val q: String, val a: String)

private val FAQ = listOf(
    Faq("Penugasan", "Bagaimana cara melihat penugasan baru?", "Penugasan baru muncul di halaman Tugas dengan status \"Belum\". Ikon lonceng di dashboard menampilkan jumlah penugasan yang masuk hari ini."),
    Faq("Penugasan", "Bagaimana menuju lokasi pelanggan?", "Buka detail penugasan, lalu ketuk tombol \"Buka Google Maps\" untuk memulai navigasi ke koordinat pelanggan."),
    Faq("Penugasan", "Pelanggan tidak berada di tempat, apa yang harus dilakukan?", "Hubungi pelanggan melalui tombol WhatsApp pada detail penugasan. Jika tetap tidak dapat ditemui, jangan tekan Konfirmasi Pengerjaan agar status tetap Belum, lalu laporkan ke admin cabang."),
    Faq("Eksekusi", "Apa beda Konfirmasi Pengerjaan dan Eksekusi Pengerjaan?", "Konfirmasi Pengerjaan berada di bagian bawah halaman Detail Penugasan (status Belum): unggah foto water meter lama sebagai bukti kondisi awal, lalu tekan Konfirmasi Pengerjaan sehingga status menjadi Proses. Eksekusi Pengerjaan (status Proses) adalah halaman terpisah untuk mengisi data water meter baru, foto hasil pemasangan, dan tanda tangan teknisi."),
    Faq("Eksekusi", "Apakah isian form hilang jika aplikasi tertutup?", "Tidak. Setiap perubahan pada form eksekusi tersimpan otomatis sebagai draft di perangkat, sehingga Anda dapat melanjutkannya kapan saja."),
    Faq("Eksekusi", "Foto apa saja yang wajib diunggah?", "Foto water meter lama diunggah di bagian Konfirmasi Pengerjaan pada halaman Detail, sedangkan foto water meter baru diunggah saat Eksekusi Pengerjaan. Pastikan nomor seri dan angka stand terlihat jelas."),
    Faq("Eksekusi", "Bagaimana jika tanda tangan teknisi salah?", "Ketuk \"Ulangi\" untuk menghapus goresan terakhir atau \"Hapus\" untuk mengosongkan area tanda tangan, lalu tanda tangani kembali."),
    Faq("Akun", "Saya lupa password, bagaimana cara masuk?", "Pada halaman login, ketuk \"Lupa Password?\" lalu masukkan username atau email. Admin cabang akan mengirimkan tautan pengaturan ulang."),
    Faq("Akun", "Bagaimana cara mengubah NIP atau cabang?", "Data kepegawaian dikelola oleh admin. Hubungi admin cabang melalui WhatsApp atau email yang tersedia di halaman ini."),
)

private val STEPS = listOf(
    "Buka penugasan lalu ketuk Lihat Detail",
    "Di bawah Detail: unggah foto WM lama & Konfirmasi Pengerjaan (status Proses)",
    "Eksekusi Pengerjaan: data & foto WM baru, tanda tangan",
    "Periksa ringkasan lalu Konfirmasi Penyelesaian (status Selesai)",
)

@Composable
fun HelpScreen(onBack: () -> Unit, toast: (String) -> Unit) {
    val ctx = LocalContext.current
    var q by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Semua") }
    var open by remember { mutableStateOf<String?>(FAQ[0].q) }
    var report by remember { mutableStateOf(false) }
    var kind by remember { mutableStateOf<String?>(null) }
    var desc by remember { mutableStateOf("") }
    val s = q.trim().lowercase()
    val list = FAQ.filter { (cat == "Semua" || it.c == cat) && (s.isEmpty() || (it.q + it.a).lowercase().contains(s)) }
    val counts = CATS.associateWith { c -> FAQ.count { c == "Semua" || it.c == c } }

    PageScaffold("Bantuan", onBack) {
        // Banner pencarian
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Navy)) {
            Box(Modifier.align(Alignment.TopEnd).offset(40.dp, (-40).dp).size(144.dp).background(Sky.copy(alpha = 0.25f), CircleShape))
            Box(Modifier.align(Alignment.BottomEnd).offset((-40).dp, 48.dp).size(96.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))
            Column(Modifier.padding(20.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconChip(R.drawable.ic_headset, Color.White.copy(alpha = 0.12f), Color.White, size = 44.dp, iconSize = 22.dp, radius = 16.dp)
                    Column(Modifier.weight(1f)) {
                        PText("Ada yang bisa kami bantu?", size = 16, weight = FontWeight.Bold, color = Color.White)
                        PText("Temukan jawaban cepat atau hubungi tim admin PAM Tirta Kemuning.", Modifier.padding(top = 2.dp), size = 12, color = Color.White.copy(alpha = 0.7f), lineHeight = 1.5f)
                    }
                }
                Row(
                    Modifier.padding(top = 16.dp).fillMaxWidth().height(44.dp).background(Color.White, RoundedCornerShape(16.dp)).padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically,
                ) {
                    Ic(R.drawable.ic_search, 17.dp, tint = navy(0.45f))
                    BasicTextField(
                        q, { q = it }, singleLine = true, cursorBrush = SolidColor(Sky), modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = Poppins, fontSize = 14.sp, color = Navy),
                        decorationBox = { inner -> Box(contentAlignment = Alignment.CenterStart) {
                            if (q.isEmpty()) PText("Cari pertanyaan...", size = 14, color = navy(0.35f))
                            inner()
                        } },
                    )
                    if (q.isNotEmpty()) Ic(R.drawable.ic_close, 16.dp, Modifier.clip(CircleShape).clickable { q = "" }, tint = navy(0.5f))
                }
            }
        }

        // Hubungi kami
        Column {
            PText("Hubungi Kami", Modifier.padding(bottom = 8.dp), size = 14, weight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contact(Modifier.weight(1f), R.drawable.ic_message_circle, "WhatsApp", "Admin Cabang", Success.copy(alpha = 0.12f), Success) {
                    ctx.openWhatsApp("6281200001234", "Halo Admin PAM Tirta Kemuning, saya membutuhkan bantuan terkait aplikasi pergantian water meter.")
                }
                Contact(Modifier.weight(1f), R.drawable.ic_phone, "Call Center", "(0232) 871234", Sky.copy(alpha = 0.15f), Sky) { ctx.dial("0232871234") }
                Contact(Modifier.weight(1f), R.drawable.ic_mail, "Email", "Helpdesk", navy(0.1f), Navy) { ctx.sendMail("helpdesk@pamtirtakemuning.co.id") }
            }
            Row(Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                Ic(R.drawable.ic_clock, 12.dp, tint = navy(0.5f))
                PText("Layanan: Senin–Jumat, 08.00–16.00 WIB", size = 11, color = navy(0.5f))
            }
        }

        // FAQ
        Column {
            PText("Pertanyaan Umum", Modifier.padding(bottom = 8.dp), size = 14, weight = FontWeight.Bold)
            Segmented(CATS, cat, { cat = it }, counts)
            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (list.isEmpty()) PCard {
                    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Ic(R.drawable.ic_search, 26.dp, tint = navy(0.25f))
                        PText("Pertanyaan tidak ditemukan", Modifier.padding(top = 8.dp), size = 14, weight = FontWeight.SemiBold)
                        PText("Coba kata kunci lain atau hubungi admin.", size = 12, color = navy(0.5f))
                    }
                }
                list.forEach { f ->
                    val on = open == f.q
                    val shape = RoundedCornerShape(16.dp)
                    Column(Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, if (on) Sky.copy(alpha = 0.4f) else navy(0.05f), shape)) {
                        Row(Modifier.fillMaxWidth().clickable { open = if (on) null else f.q }.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            PText(f.q, Modifier.weight(1f), size = 13, weight = FontWeight.SemiBold, lineHeight = 1.35f)
                            Ic(R.drawable.ic_chevron_down, 18.dp, Modifier.rotate(if (on) 180f else 0f), tint = if (on) Sky else navy(0.4f))
                        }
                        AnimatedVisibility(on) {
                            Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                                PText(f.c, Modifier.background(Ice, CircleShape).padding(horizontal = 8.dp, vertical = 2.dp), size = 10, weight = FontWeight.SemiBold, color = Sky, lineHeight = 1.3f)
                                PText(f.a, Modifier.padding(top = 6.dp), size = 12, color = navy(0.65f), lineHeight = 1.6f)
                            }
                        }
                    }
                }
            }
        }

        // Panduan singkat
        PCard {
            Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                IconChip(R.drawable.ic_book_open)
                Column {
                    PText("Panduan Singkat", size = 14, weight = FontWeight.Bold)
                    PText("Alur kerja pergantian water meter", size = 11, color = navy(0.5f), lineHeight = 1.3f)
                }
            }
            STEPS.forEachIndexed { i, step ->
                Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(28.dp).background(Navy, CircleShape), contentAlignment = Alignment.Center) {
                            PText("${i + 1}", size = 11, weight = FontWeight.Bold, color = Color.White, lineHeight = 1f)
                        }
                        if (i < STEPS.lastIndex) Box(Modifier.padding(vertical = 4.dp).width(1.dp).weight(1f).background(navy(0.15f)))
                    }
                    PText(step, Modifier.padding(top = 4.dp, bottom = if (i < STEPS.lastIndex) 16.dp else 0.dp), size = 13, weight = FontWeight.Medium, color = navy(0.8f), lineHeight = 1.4f)
                }
            }
        }

        PButton("Laporkan Masalah Aplikasi", { report = true }, Modifier.fillMaxWidth(), BtnVariant.Outline, icon = R.drawable.ic_message_warning)

        Column(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Logo(36.dp)
            PText("PAM TIRTA KEMUNING", Modifier.padding(top = 8.dp), size = 12, weight = FontWeight.Bold, letterSpacing = 0.5f)
            PText("Sistem Informasi Pergantian Water Meter · Versi 1.0.0", size = 10, color = navy(0.45f), align = TextAlign.Center)
        }
    }

    if (report) Modal({ report = false }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            PText("Laporkan Masalah", size = 16, weight = FontWeight.Bold)
            Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Mist).clickable { report = false }, contentAlignment = Alignment.Center) { Ic(R.drawable.ic_close, 16.dp) }
        }
        PText("Laporan akan diteruskan ke tim admin cabang.", Modifier.padding(top = 4.dp), size = 12, color = navy(0.55f))
        PText("Jenis Masalah", Modifier.padding(top = 16.dp, bottom = 6.dp), size = 12, weight = FontWeight.SemiBold)
        RadioButtons(listOf("Aplikasi Error", "Data Salah", "Lainnya"), kind, onChange = { kind = it })
        PTextArea(desc, { desc = it }, "Deskripsi", "Jelaskan masalah yang Anda alami...", Modifier.padding(top = 12.dp))
        PButton("Kirim Laporan", {
            report = false; kind = null; desc = ""
            toast("Laporan berhasil dikirim ke admin")
        }, Modifier.padding(top = 16.dp).fillMaxWidth(), icon = R.drawable.ic_send, enabled = kind != null && desc.trim().length >= 5)
    }
}

@Composable
private fun Contact(modifier: Modifier, icon: Int, label: String, sub: String, bg: Color, fg: Color, onClick: () -> Unit) {
    PCard(modifier, padding = PaddingValues(horizontal = 8.dp, vertical = 14.dp), onClick = onClick) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(44.dp).background(bg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) { Ic(icon, 20.dp, tint = fg) }
            PText(label, Modifier.padding(top = 8.dp), size = 12, weight = FontWeight.Bold, align = TextAlign.Center)
            PText(sub, size = 10, color = navy(0.5f), align = TextAlign.Center, lineHeight = 1.3f)
        }
    }
}