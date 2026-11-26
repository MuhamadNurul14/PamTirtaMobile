package id.pamtirtakemuning.watermeter.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.navy
import id.pamtirtakemuning.watermeter.util.Photos
import java.io.File

/** Pengambilan foto: kamera (disimpan lewat FileProvider) atau galeri. [value] adalah path file foto. */
@Composable
fun PhotoCapture(title: String, desc: String, value: String?, onChange: (String?) -> Unit, onPreview: (String) -> Unit) {
    val ctx = LocalContext.current
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val p = pending
        pending = null
        if (p != null) {
            val f = File(p)
            if (ok) Photos.processCamera(ctx, f)?.let { onChange(it) } else f.delete()
        }
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { Photos.importUri(ctx, it)?.let(onChange) }
    }
    val dash = Sky.copy(alpha = 0.6f)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Ice)
            .drawBehind {
                drawRoundRect(dash, cornerRadius = CornerRadius(16.dp.toPx()), style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))))
            }.padding(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconChip(R.drawable.ic_camera, Color.White, Sky, size = 40.dp, iconSize = 20.dp)
            Column(Modifier.weight(1f)) {
                PText(title, size = 14, weight = FontWeight.Bold)
                PText(desc, size = 12, color = navy(0.6f), lineHeight = 1.3f)
            }
        }
        if (value != null) {
            Box(Modifier.padding(top = 12.dp).fillMaxWidth().height(176.dp).clip(RoundedCornerShape(12.dp)).clickable { onPreview(value) }) {
                AsyncImage(Photos.resolveUrl(value), title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp).background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Ic(R.drawable.ic_maximize, 14.dp)
                }
            }
        }
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PButton(if (value != null) "Foto Ulang" else "Ambil Foto", {
                val f = Photos.newFile(ctx)
                pending = f.path
                camera.launch(Photos.uriFor(ctx, f))
            }, Modifier.weight(1f), BtnVariant.Secondary, small = true, icon = R.drawable.ic_camera)
            PButton("Dari Galeri", {
                gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }, Modifier.weight(1f), BtnVariant.Outline, small = true, icon = R.drawable.ic_image)
        }
        if (value != null) {
            PText("Hapus foto", Modifier.padding(top = 8.dp).fillMaxWidth().clickable { onChange(null) }, size = 12, weight = FontWeight.SemiBold, color = Danger, align = TextAlign.Center)
        }
    }
}

@Composable
fun PhotoThumb(label: String, src: String?, onOpen: (String) -> Unit) {
    Column {
        Box(
            Modifier.fillMaxWidth().height(128.dp).clip(RoundedCornerShape(16.dp)).background(Ice).clickable(enabled = src != null) { src?.let(onOpen) },
            contentAlignment = Alignment.Center,
        ) {
            if (src != null) AsyncImage(Photos.resolveUrl(src), label, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Ic(R.drawable.ic_image_plus, 22.dp, tint = navy(0.4f))
                PText("Foto arsip", size = 10, color = navy(0.4f))
            }
        }
        PText(label, Modifier.fillMaxWidth().padding(top = 4.dp), size = 11, weight = FontWeight.Medium, color = navy(0.6f), align = TextAlign.Center)
    }
}

@Composable
fun PhotoViewer(src: String, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.92f)).clickable(onClick = onClose).padding(16.dp), contentAlignment = Alignment.Center) {
            AsyncImage(Photos.resolveUrl(src), "Pratinjau foto", Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            Box(Modifier.align(Alignment.TopEnd).size(40.dp).background(Color.White.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Ic(R.drawable.ic_close, 20.dp, tint = Color.White)
            }
        }
    }
}

/** Area tanda tangan digital. Hasilnya disimpan sebagai PNG transparan dan [onChange] menerima path file-nya. */
@Composable
fun SignaturePad(value: String?, onChange: (String?) -> Unit) {
    val ctx = LocalContext.current
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var base by remember { mutableStateOf<ImageBitmap?>(value?.let { runCatching { BitmapFactory.decodeFile(it)?.asImageBitmap() }.getOrNull() }) }
    var padSize by remember { mutableStateOf(IntSize.Zero) }
    val sw = with(androidx.compose.ui.platform.LocalDensity.current) { 2.5.dp.toPx() }
    val empty = strokes.isEmpty() && base == null
    val stroke = remember(sw) { Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round) }

    fun commit() {
        if (strokes.isEmpty() && base == null) { onChange(null); return }
        val bmp = Bitmap.createBitmap(maxOf(padSize.width, 1), maxOf(padSize.height, 1), Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        base?.let { canvas.drawBitmap(it.asAndroidBitmap(), null, android.graphics.Rect(0, 0, bmp.width, bmp.height), null) }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#123B68"); style = Paint.Style.STROKE; strokeWidth = sw
            strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
        }
        strokes.forEach { s ->
            val p = android.graphics.Path()
            s.forEachIndexed { i, o -> if (i == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y) }
            canvas.drawPath(p, paint)
        }
        onChange(Photos.savePng(ctx, bmp))
    }

    Column {
        Box(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, navy(0.15f), RoundedCornerShape(16.dp))) {
            Canvas(
                Modifier.fillMaxSize().onSizeChanged { padSize = it }.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { strokes.add(listOf(it)) },
                        onDrag = { change, _ -> change.consume(); strokes[strokes.lastIndex] = strokes.last() + change.position },
                        onDragEnd = { commit() },
                        onDragCancel = { commit() },
                    )
                },
            ) {
                base?.let { drawImage(it, dstSize = IntSize(padSize.width, padSize.height)) }
                strokes.forEach { s ->
                    if (s.size == 1) drawCircle(Navy, sw / 2f, s[0])
                    else drawPath(Path().apply { moveTo(s[0].x, s[0].y); s.drop(1).forEach { lineTo(it.x, it.y) } }, Navy, style = stroke)
                }
            }
            if (empty) PText("Tanda tangan di area ini", Modifier.align(Alignment.Center), size = 12, weight = FontWeight.Medium, color = navy(0.3f))
            Box(Modifier.align(Alignment.BottomCenter).padding(horizontal = 24.dp, vertical = 24.dp).fillMaxWidth().height(1.dp).background(navy(0.15f)))
        }
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PButton("Hapus", { strokes.clear(); base = null; commit() }, Modifier.weight(1f), BtnVariant.Outline, small = true)
            PButton("Ulangi", { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) else base = null; commit() }, Modifier.weight(1f), BtnVariant.Outline, small = true)
        }
    }
}
