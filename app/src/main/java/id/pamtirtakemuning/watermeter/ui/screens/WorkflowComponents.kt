package id.pamtirtakemuning.watermeter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Assignment
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.ui.components.Ic
import id.pamtirtakemuning.watermeter.ui.components.PCard
import id.pamtirtakemuning.watermeter.ui.components.PText
import id.pamtirtakemuning.watermeter.ui.components.StatusBadge
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.Warn
import id.pamtirtakemuning.watermeter.ui.theme.WarnBg
import id.pamtirtakemuning.watermeter.ui.theme.WarnText
import id.pamtirtakemuning.watermeter.ui.theme.navy

private data class Stage(val label: String, val sub: String, val icon: Int)

private val stages = listOf(
    Stage("Konfirmasi", "WM lama", R.drawable.ic_camera),
    Stage("Eksekusi", "WM baru", R.drawable.ic_wrench),
    Stage("Validasi", "Pusat", R.drawable.ic_shield_check),
    Stage("Selesai", "Hasil kerja", R.drawable.ic_badge_check),
)

/** Indikator tahapan kerja: Belum -> konfirmasi, Proses/Ditolak -> eksekusi/perbaikan, Validasi -> validasi Pusat, Selesai -> tuntas. */
@Composable
fun JobStepper(status: Status) {
    val done = when (status) {
        Status.Belum -> 0
        Status.Proses -> 1
        Status.Ditolak -> 1
        Status.Validasi -> 2
        Status.Selesai -> 4
    }
    val navyLine = navy(0.1f)
    PCard(padding = PaddingValues(horizontal = 10.dp, vertical = 14.dp)) {
        Row(
            Modifier.fillMaxWidth().drawBehind {
                val cell = size.width / 4f
                for (i in 1..3) {
                    drawLine(
                        if (i <= done) Success else navyLine,
                        Offset(cell * (i - 0.5f) + 18.dp.toPx(), 18.dp.toPx()), Offset(cell * (i + 0.5f) - 18.dp.toPx(), 18.dp.toPx()), 2.dp.toPx(),
                    )
                }
            },
        ) {
            stages.forEachIndexed { i, s ->
                val isDone = i < done
                val isNow = i == done
                val isRejectedStage = isNow && status == Status.Ditolak
                val stageColor = if (isDone) Success else if (isRejectedStage) id.pamtirtakemuning.watermeter.ui.theme.Danger else if (isNow) Sky else Mist
                val stageLabel = if (i == 1 && status == Status.Ditolak) "Perbaikan" else s.label
                val stageSub = if (i == 1 && status == Status.Ditolak) "Perbarui data" else if (i == 2 && status == Status.Validasi) "Validasi Pusat" else s.sub
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(36.dp)
                            .drawBehind { if (isNow) drawCircle((if (isRejectedStage) id.pamtirtakemuning.watermeter.ui.theme.Danger else Sky).copy(alpha = 0.2f), radius = 22.dp.toPx()) }
                            .background(stageColor, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Ic(if (isDone) R.drawable.ic_check else s.icon, 17.dp, tint = if (isDone || isNow) Color.White else navy(0.35f)) }
                    PText(stageLabel, Modifier.padding(top = 6.dp), size = 11, weight = FontWeight.Bold, color = if (isDone) Success else if (isRejectedStage) id.pamtirtakemuning.watermeter.ui.theme.Danger else if (isNow) Navy else navy(0.4f), lineHeight = 1.3f)
                    PText(stageSub, size = 10, color = navy(0.45f), lineHeight = 1.3f, align = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun ReasonCard(a: Assignment) {
    val shape = RoundedCornerShape(24.dp)
    Column(Modifier.fillMaxWidth().background(WarnBg, shape).border(1.dp, Warn.copy(alpha = 0.35f), shape).padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(40.dp).shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = Warn, spotColor = Warn).background(Warn, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Ic(R.drawable.ic_triangle_alert, 19.dp, tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                PText("ALASAN ${a.jobType.label.uppercase()}", size = 10, weight = FontWeight.Bold, color = WarnText, letterSpacing = 1f, lineHeight = 1.3f)
                PText(a.damage, size = 16, weight = FontWeight.Bold, lineHeight = 1.3f)
            }
        }
        PText(a.reason, Modifier.padding(top = 12.dp), size = 13, color = navy(0.75f), lineHeight = 1.6f)
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PText("${a.jobType.label} Water Meter", Modifier.background(Color.White, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), size = 11, weight = FontWeight.SemiBold, color = WarnText, lineHeight = 1.3f)
            PText(a.oldSerial, Modifier.background(Color.White, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), size = 11, weight = FontWeight.SemiBold, color = navy(0.7f), mono = true, lineHeight = 1.3f)
        }
    }
}

@Composable
fun JobHeader(a: Assignment) {
    PCard(color = Navy, contentColor = Color.White, borderColor = null) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                PText("${a.assignNo} · ${a.jobType.label}", size = 11, color = Color.White.copy(alpha = 0.6f))
                PText(a.name, size = 18, weight = FontWeight.Bold, maxLines = 1)
                PText("No. ${a.custNo}", size = 12, color = Color.White.copy(alpha = 0.75f))
            }
            StatusBadge(a.status)
        }
    }
}

@Composable
fun StepCard(n: Int, title: String, hint: String? = null, content: @Composable () -> Unit) {
    PCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(24.dp).background(Navy, CircleShape), contentAlignment = Alignment.Center) {
                    PText("$n", size = 11, weight = FontWeight.Bold, color = Color.White, lineHeight = 1f)
                }
                Column(Modifier.weight(1f)) {
                    PText(title, size = 14, weight = FontWeight.Bold)
                    if (hint != null) PText(hint, Modifier.padding(top = 2.dp), size = 11, color = navy(0.55f), lineHeight = 1.3f)
                }
            }
            content()
        }
    }
}
