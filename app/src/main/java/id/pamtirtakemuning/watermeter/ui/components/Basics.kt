package id.pamtirtakemuning.watermeter.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.ui.theme.GrayText
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Muted
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.Success
import id.pamtirtakemuning.watermeter.ui.theme.SuccessText
import id.pamtirtakemuning.watermeter.ui.theme.Warn
import id.pamtirtakemuning.watermeter.ui.theme.WarnText
import id.pamtirtakemuning.watermeter.ui.theme.navy

/* ---------- Teks & ikon ---------- */

@Composable
fun PText(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 14,
    weight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    mono: Boolean = false,
    lineHeight: Float = 1.4f,
    letterSpacing: Float = 0f,
) = PText(AnnotatedString(text), modifier, size, weight, color, align, maxLines, mono, lineHeight, letterSpacing)

@Composable
fun PText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    size: Int = 14,
    weight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    mono: Boolean = false,
    lineHeight: Float = 1.4f,
    letterSpacing: Float = 0f,
) = Text(
    text = text,
    modifier = modifier,
    color = color,
    fontSize = size.sp,
    fontWeight = weight,
    fontFamily = if (mono) FontFamily.Monospace else null,
    textAlign = align,
    maxLines = maxLines,
    overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
    lineHeight = (size * lineHeight).sp,
    letterSpacing = letterSpacing.sp,
)

/** Potongan teks tebal untuk dipakai di dalam [buildAnnotatedString]. */
fun AnnotatedString.Builder.bold(text: String, color: Color = Navy) =
    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = color)) { append(text) }

fun annotated(block: AnnotatedString.Builder.() -> Unit) = buildAnnotatedString(block)

@Composable
fun Ic(@DrawableRes id: Int, size: Dp = 20.dp, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current) {
    Icon(painterResource(id), contentDescription = null, modifier = modifier.size(size), tint = tint)
}

/* ---------- Kartu, tombol, badge ---------- */

private val CardShadow = navy(0.10f)

@Composable
fun PCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    color: Color = Color.White,
    contentColor: Color = Navy,
    borderColor: Color? = navy(0.05f),
    shadow: Boolean = true,
    radius: Dp = 24.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Column(
        modifier
            .fillMaxWidth()
            .then(if (shadow) Modifier.shadow(6.dp, shape, ambientColor = CardShadow, spotColor = CardShadow) else Modifier)
            .background(color, shape)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding),
    ) { CompositionLocalProvider(LocalContentColor provides contentColor) { content() } }
}

enum class BtnVariant { Primary, Secondary, Outline, Success, Ghost, Danger, DangerOutline }

@Composable
fun PButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: BtnVariant = BtnVariant.Primary,
    small: Boolean = false,
    large: Boolean = false,
    @DrawableRes icon: Int? = null,
    enabled: Boolean = true,
) {
    val (bg, fg) = when (variant) {
        BtnVariant.Primary -> Navy to Color.White
        BtnVariant.Secondary -> Sky to Color.White
        BtnVariant.Outline -> Color.White to Navy
        BtnVariant.Success -> Success to Color.White
        BtnVariant.Ghost -> Color.Transparent to Navy
        BtnVariant.Danger -> Color(0xFFDC2626) to Color.White
        BtnVariant.DangerOutline -> Color.White to Color(0xFFDC2626)
    }
    val glow = variant in listOf(BtnVariant.Primary, BtnVariant.Secondary, BtnVariant.Success) && enabled
    val shape = RoundedCornerShape(16.dp)
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = bg,
        contentColor = fg,
        border = when (variant) {
            BtnVariant.Outline -> BorderStroke(1.dp, navy(0.15f))
            BtnVariant.DangerOutline -> BorderStroke(1.dp, Color(0xFFFCA5A5))
            else -> null
        },
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .then(if (glow) Modifier.shadow(4.dp, shape, ambientColor = bg.copy(alpha = 0.25f), spotColor = bg.copy(alpha = 0.35f)) else Modifier),
    ) {
        Row(
            Modifier
                .height(if (large) 56.dp else if (small) 40.dp else 48.dp)
                .padding(horizontal = if (small) 12.dp else 20.dp),
            horizontalArrangement = Arrangement.spacedBy(if (small) 6.dp else 8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) Ic(icon, if (small) 14.dp else if (large) 18.dp else 16.dp)
            PText(text, size = if (large) 15 else if (small) 12 else 14, weight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
fun StatusBadge(status: Status, modifier: Modifier = Modifier) {
    val (bg, fg, dot) = when (status) {
        Status.Belum -> Triple(Muted.copy(alpha = 0.15f), GrayText, Muted)
        Status.Proses -> Triple(Warn.copy(alpha = 0.15f), WarnText, Warn)
        Status.Selesai -> Triple(Success.copy(alpha = 0.12f), SuccessText, Success)
    }
    Row(
        modifier.background(bg, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).background(dot, CircleShape))
        PText(status.label, size = 11, weight = FontWeight.SemiBold, color = fg, lineHeight = 1.2f)
    }
}

/** Kotak ikon bulat-persegi kecil (mis. ikon judul section). */
@Composable
fun IconChip(@DrawableRes icon: Int, bg: Color = Ice, tint: Color = Navy, size: Dp = 32.dp, iconSize: Dp = 16.dp, radius: Dp = 12.dp) {
    Box(Modifier.size(size).background(bg, RoundedCornerShape(radius)), contentAlignment = Alignment.Center) { Ic(icon, iconSize, tint = tint) }
}

/* ---------- Layout ---------- */

/** Kolom dengan garis pemisah tipis antar-anak (setara `divide-y divide-navy/5`). */
@Composable
fun DividedColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    SubcomposeLayout(modifier) { c ->
        val w = c.maxWidth
        val loose = Constraints(minWidth = w, maxWidth = w, minHeight = 0, maxHeight = Constraints.Infinity)
        val items = subcompose("items", content).map { it.measure(loose) }
        val dividers = (1 until items.size).map { i ->
            subcompose("divider$i") { Box(Modifier.fillMaxWidth().height(1.dp).background(navy(0.05f))) }.first().measure(loose)
        }
        val h = items.sumOf { it.height } + dividers.sumOf { it.height }
        layout(w, h) {
            var y = 0
            items.forEachIndexed { i, p ->
                if (i > 0) { dividers[i - 1].place(0, y); y += dividers[i - 1].height }
                p.place(0, y); y += p.height
            }
        }
    }
}

@Composable
fun Section(title: String, @DrawableRes icon: Int, content: @Composable () -> Unit) {
    PCard {
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            IconChip(icon)
            PText(title, size = 14, weight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(navy(0.05f)))
        DividedColumn(content = content)
    }
}

@Composable
fun InfoRowC(label: String, @DrawableRes icon: Int? = null, content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (icon != null) Ic(icon, 16.dp, Modifier.padding(top = 2.dp), tint = Sky)
        Column(Modifier.weight(1f)) {
            PText(label, size = 11, weight = FontWeight.Medium, color = navy(0.5f))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, @DrawableRes icon: Int? = null, mono: Boolean = false) =
    InfoRowC(label, icon) { PText(value, size = 14, weight = FontWeight.SemiBold, mono = mono, lineHeight = 1.3f) }

/** Baris label (kiri) dan nilai rata kanan. */
@Composable
fun KvRowC(label: String, content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        PText(label, size = 14, color = navy(0.55f))
        Box(Modifier.weight(1f), contentAlignment = Alignment.TopEnd) { content() }
    }
}

@Composable
fun KvRow(label: String, value: String, mono: Boolean = false, color: Color = Color.Unspecified) =
    KvRowC(label) { PText(value, size = 14, weight = FontWeight.SemiBold, mono = mono, color = color, align = TextAlign.End) }

/** Header halaman + area isi yang dapat di-scroll + (opsional) bilah aksi di bawah. */
@Composable
fun PageScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    right: (@Composable () -> Unit)? = null,
    bar: (@Composable () -> Unit)? = null,
    background: Color = Mist,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(background)) {
        PageHeader(title, onBack, right, background)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                content()
                if (bar == null) Spacer(Modifier.navigationBarsPadding())
            },
        )
        bar?.invoke()
    }
}

@Composable
fun PageHeader(title: String, onBack: (() -> Unit)? = null, right: (@Composable () -> Unit)? = null, background: Color = Mist) {
    Row(
        Modifier.fillMaxWidth().background(background).statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            val shape = RoundedCornerShape(16.dp)
            Box(
                Modifier.size(40.dp).shadow(2.dp, shape, ambientColor = CardShadow, spotColor = CardShadow).background(Color.White, shape)
                    .border(1.dp, navy(0.05f), shape).clip(shape).clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) { Ic(R.drawable.ic_arrow_left, 20.dp) }
        }
        PText(title, Modifier.weight(1f), size = 18, weight = FontWeight.Bold)
        right?.invoke()
    }
}

/** Bilah aksi yang menempel di bawah layar (tombol utama + petunjuk). */
@Composable
fun ActionBar(hint: String? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Column(
        Modifier.fillMaxWidth()
            .shadow(12.dp, shape, ambientColor = CardShadow, spotColor = CardShadow)
            .background(Color.White, shape)
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
    ) {
        if (hint != null) {
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                Ic(R.drawable.ic_info, 12.dp, tint = navy(0.55f))
                PText(hint, size = 11, color = navy(0.55f), align = TextAlign.Center, lineHeight = 1.3f, modifier = Modifier.weight(1f, fill = false))
            }
        }
        content()
    }
}

/* ---------- Foto kantor, logo, peta ---------- */

/** Header berlatar foto kantor PAM Tirta Kemuning dengan lapisan navy agar teks putih tetap terbaca. */
@Composable
fun PhotoHero(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(0.dp),
    focusY: Float = -0.1f,
    overlay: List<Color> = listOf(Navy.copy(alpha = 0.95f), Navy.copy(alpha = 0.70f), Navy.copy(alpha = 0.55f)),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxWidth().clip(shape)) {
        Image(
            painterResource(R.drawable.kantor_pam), contentDescription = null,
            modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop, alignment = BiasAlignment(0f, focusY),
        )
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(overlay)))
        CompositionLocalProvider(LocalContentColor provides Color.White) { content() }
    }
}

@Composable
fun Logo(size: Dp = 64.dp, light: Boolean = false) {
    val shape = RoundedCornerShape(28)
    Box(
        Modifier.size(size).shadow(8.dp, shape).background(if (light) Color.White else Navy, shape),
        contentAlignment = Alignment.Center,
    ) { Ic(R.drawable.ic_droplets, size * 0.5f, tint = if (light) Navy else Color.White) }
}

/** Pratinjau peta sederhana (digambar manual) dengan pin lokasi pelanggan. */
@Composable
fun MapPreview(name: String) {
    Box(Modifier.fillMaxWidth().height(144.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFE8F1F8))) {
        Canvas(Modifier.fillMaxSize()) {
            val s = maxOf(size.width / 320f, size.height / 144f)
            translate((size.width - 320f * s) / 2f, (size.height - 144f * s) / 2f) {
                scale(s, s, Offset.Zero) {
                    drawRoundRect(Color(0xFFCFE8D6), Offset(190f, 70f), Size(110f, 60f), CornerRadius(10f))
                    val river = Path().apply { moveTo(0f, 100f); cubicTo(80f, 80f, 140f, 130f, 320f, 40f) }
                    drawPath(river, Color(0xFF9FD0F2), style = Stroke(14f))
                    val road = Stroke(9f, cap = StrokeCap.Round)
                    listOf(Offset(-10f, 40f) to Offset(330f, 40f), Offset(90f, -10f) to Offset(90f, 160f),
                        Offset(230f, -10f) to Offset(230f, 160f), Offset(0f, 130f) to Offset(120f, 0f))
                        .forEach { (a, b) -> drawLine(Color.White, a, b, road.width, StrokeCap.Round) }
                    drawLine(Color(0xFFD3E1EE), Offset(0f, 70f), Offset(320f, 70f), 1.5f)
                    drawLine(Color(0xFFD3E1EE), Offset(160f, 0f), Offset(160f, 144f), 1.5f)
                }
            }
        }
        Box(Modifier.align(Alignment.Center).offset(y = (-18).dp).size(36.dp).shadow(6.dp, RoundedCornerShape(18.dp, 18.dp, 18.dp, 0.dp))
            .rotate(-45f).background(Navy, RoundedCornerShape(18.dp, 18.dp, 18.dp, 0.dp)), contentAlignment = Alignment.Center) {
            Ic(R.drawable.ic_droplets, 16.dp, Modifier.rotate(45f), tint = Color.White)
        }
        PText(name, Modifier.align(Alignment.BottomStart).padding(8.dp).background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
            size = 10, weight = FontWeight.SemiBold, lineHeight = 1.2f)
    }
}

/* ---------- Dialog ---------- */

@Composable
fun Modal(onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(
            Modifier.fillMaxSize().background(navy(0.5f)).clickable(onClick = onClose, indication = null, interactionSource = remember { MutableInteractionSource() }),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                Modifier.navigationBarsPadding().imePadding().padding(16.dp).fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)).background(Color.White, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}.padding(20.dp),
            ) { CompositionLocalProvider(LocalContentColor provides Navy) { content() } }
        }
    }
}
