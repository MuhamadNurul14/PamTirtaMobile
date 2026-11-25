package id.pamtirtakemuning.watermeter.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.theme.GrayText
import id.pamtirtakemuning.watermeter.ui.theme.Danger
import id.pamtirtakemuning.watermeter.ui.theme.Mist
import id.pamtirtakemuning.watermeter.ui.theme.Muted
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.Poppins
import id.pamtirtakemuning.watermeter.ui.theme.Sky
import id.pamtirtakemuning.watermeter.ui.theme.navy

private val fieldText = TextStyle(fontFamily = Poppins, fontSize = 14.sp, color = Navy)

@Composable
fun FieldLabel(text: String) = PText(text, Modifier.padding(bottom = 6.dp), size = 12, weight = FontWeight.SemiBold)

/** Input satu baris dengan label, ikon di kiri, dan slot kanan opsional. */
@Composable
fun PInput(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    @DrawableRes icon: Int? = null,
    right: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    password: Boolean = false,
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    Column(modifier) {
        if (label != null) FieldLabel(label)
        Row(
            Modifier.fillMaxWidth().height(48.dp)
                .background(if (focused) Color.White else Mist, RoundedCornerShape(16.dp))
                .border(if (focused) 1.5.dp else 1.dp, if (focused) Sky else navy(0.1f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) Ic(icon, 18.dp, tint = navy(0.5f))
            BasicTextField(
                value = value, onValueChange = onChange, singleLine = true, textStyle = fieldText, interactionSource = source,
                cursorBrush = SolidColor(Sky),
                visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType, capitalization = capitalization),
                modifier = Modifier.weight(1f),
                decorationBox = { inner -> Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) PText(placeholder, size = 14, color = navy(0.35f), maxLines = 1)
                    inner()
                } },
            )
            right?.invoke()
        }
    }
}

@Composable
fun PTextArea(value: String, onChange: (String) -> Unit, label: String, placeholder: String, modifier: Modifier = Modifier) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    Column(modifier) {
        FieldLabel(label)
        BasicTextField(
            value = value, onValueChange = onChange, textStyle = fieldText, interactionSource = source, minLines = 3, maxLines = 6,
            cursorBrush = SolidColor(Sky),
            modifier = Modifier.fillMaxWidth()
                .background(if (focused) Color.White else Mist, RoundedCornerShape(16.dp))
                .border(if (focused) 1.5.dp else 1.dp, if (focused) Sky else navy(0.1f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            decorationBox = { inner -> Box {
                if (value.isEmpty()) PText(placeholder, size = 14, color = navy(0.35f))
                inner()
            } },
        )
    }
}

@Composable
fun SearchBar(value: String, onChange: (String) -> Unit, placeholder: String = "Cari nama atau nomor pelanggan") {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().height(48.dp)
            .shadow(6.dp, shape, ambientColor = navy(0.1f), spotColor = navy(0.1f)).background(Color.White, shape).border(1.dp, navy(0.05f), shape)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Ic(R.drawable.ic_search, 18.dp, tint = navy(0.5f))
        BasicTextField(
            value = value, onValueChange = onChange, singleLine = true, textStyle = fieldText, cursorBrush = SolidColor(Sky), modifier = Modifier.weight(1f),
            decorationBox = { inner -> Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) PText(placeholder, size = 14, color = navy(0.35f), maxLines = 1)
                inner()
            } },
        )
        if (value.isNotEmpty()) Ic(R.drawable.ic_close, 16.dp, Modifier.clip(CircleShape).clickable { onChange("") }, tint = navy(0.5f))
    }
}

/** Pilihan segmen (tab filter) dengan jumlah opsional. */
@Composable
fun Segmented(options: List<String>, value: String, onChange: (String) -> Unit, counts: Map<String, Int>? = null) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().shadow(6.dp, shape, ambientColor = navy(0.1f), spotColor = navy(0.1f)).background(Color.White, shape).border(1.dp, navy(0.05f), shape).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { o ->
            val on = value == o
            Row(
                Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (on) Navy else Color.Transparent).clickable { onChange(o) }.padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically,
            ) {
                PText(o, size = 11, weight = FontWeight.SemiBold, color = if (on) Color.White else navy(0.55f), maxLines = 1, lineHeight = 1.3f)
                if (counts != null) {
                    PText("${counts[o] ?: 0}", Modifier.background(if (on) Color.White.copy(alpha = 0.2f) else Mist, CircleShape).padding(horizontal = 6.dp, vertical = 1.dp),
                        size = 10, color = if (on) Color.White else navy(0.55f), lineHeight = 1.2f)
                }
            }
        }
    }
}

/** Pilihan satu dari beberapa tombol (kondisi Baik/Rusak/Tidak Ada, posisi meter, jenis masalah). */
@Composable
fun RadioButtons(options: List<String>, value: String?, cols: Int = options.size, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.chunked(cols).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { o ->
                    val on = value == o
                    val (bg, border, fg) = when {
                        !on -> Triple(Color.White, navy(0.1f), navy(0.6f))
                        o == "Rusak" -> Triple(Color(0xFFFEF2F2), Color(0xFFEF4444), Danger)
                        o == "Tidak Ada" -> Triple(Mist, Muted, GrayText)
                        else -> Triple(Navy, Navy, Color.White)
                    }
                    Box(
                        Modifier.weight(1f).heightIn(min = 40.dp).clip(RoundedCornerShape(12.dp)).background(bg).border(1.dp, border, RoundedCornerShape(12.dp))
                            .clickable { onChange(o) }.padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) { PText(o, size = 12, weight = FontWeight.SemiBold, color = fg, align = TextAlign.Center, lineHeight = 1.25f) }
                }
                repeat(cols - row.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

/** Dropdown pilihan (Merk, Tipe water meter). */
@Composable
fun SelectField(label: String, value: String, onChange: (String) -> Unit, options: List<String>, placeholder: String, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    Column(modifier) {
        FieldLabel(label)
        Box {
            Row(
                Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(16.dp)).background(Mist).border(1.dp, if (open) Sky else navy(0.1f), RoundedCornerShape(16.dp))
                    .clickable { open = true }.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PText(value.ifEmpty { placeholder }, Modifier.weight(1f), size = 14, color = if (value.isEmpty()) navy(0.35f) else Navy, maxLines = 1)
                Ic(R.drawable.ic_chevron_down, 16.dp, tint = navy(0.4f))
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }, modifier = Modifier.background(Color.White)) {
                options.forEach { o ->
                    DropdownMenuItem(text = { PText(o, size = 14) }, onClick = { onChange(o); open = false })
                }
            }
        }
    }
}
