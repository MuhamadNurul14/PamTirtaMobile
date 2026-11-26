package id.pamtirtakemuning.watermeter.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.pamtirtakemuning.watermeter.R

// Design system: Navy Blue, Sky Blue, Light Blue + Poppins
val Navy = Color(0xFF123B68)
val Sky = Color(0xFF39A9E8)
val Ice = Color(0xFFEAF5FC)
val Mist = Color(0xFFF4F6F9)
val Success = Color(0xFF22A06B)
val Warn = Color(0xFFF59E0B)
val Muted = Color(0xFF9CA3AF)
val Danger = Color(0xFFDC2626)
val GrayText = Color(0xFF6B7280)
val WarnText = Color(0xFFB45309)
val SuccessText = Color(0xFF177A50)
val WarnBg = Color(0xFFFFF7E8)
val DangerBg = Color(0xFFFEF2F2)

/** Navy dengan transparansi, setara `text-navy/60`, `border-navy/5`, dst. */
fun navy(alpha: Float) = Navy.copy(alpha = alpha)

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

private val base = TextStyle(fontFamily = Poppins, fontSize = 14.sp, platformStyle = PlatformTextStyle(includeFontPadding = false))

private val typography = Typography(
    displayLarge = base, displayMedium = base, displaySmall = base,
    headlineLarge = base, headlineMedium = base, headlineSmall = base,
    titleLarge = base, titleMedium = base, titleSmall = base,
    bodyLarge = base, bodyMedium = base, bodySmall = base,
    labelLarge = base, labelMedium = base, labelSmall = base,
)

@Composable
fun PamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Navy, secondary = Sky, background = Mist, surface = Color.White, onSurface = Navy, onBackground = Navy),
        typography = typography,
        content = { CompositionLocalProvider(LocalContentColor provides Navy, content = content) },
    )
}
