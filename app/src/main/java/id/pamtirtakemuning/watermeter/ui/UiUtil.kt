package id.pamtirtakemuning.watermeter.ui

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.databinding.DialogPhotoBinding
import id.pamtirtakemuning.watermeter.databinding.ItemInfoRowBinding
import id.pamtirtakemuning.watermeter.databinding.ItemKvRowBinding

val Int.dp: Int get() = ((this * Resources.getSystem().displayMetrics.density) + 0.5f).toInt()
val Float.dp: Float get() = this * Resources.getSystem().displayMetrics.density

fun Context.color(res: Int) = ContextCompat.getColor(this, res)
fun View.tint(res: Int) { backgroundTintList = ColorStateList.valueOf(context.color(res)) }

/** Tambahkan inset status bar / navigation bar / keyboard ke padding view (edge-to-edge). */
fun View.padInsets(top: Boolean = false, bottom: Boolean = false) {
    val t = paddingTop
    val b = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        v.updatePadding(top = t + if (top) bars.top else 0, bottom = b + if (bottom) maxOf(bars.bottom, ime.bottom) else 0)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

fun roundRect(color: Int, radiusDp: Int = 12, strokeColor: Int? = null, strokeDp: Int = 1): GradientDrawable =
    GradientDrawable().apply {
        setColor(color); cornerRadius = radiusDp.dp.toFloat()
        strokeColor?.let { setStroke(strokeDp.dp, it) }
    }

fun pill(color: Int): GradientDrawable = roundRect(color, 100)

/** Muat foto dari file (diperkecil agar hemat memori). */
fun ImageView.loadFile(path: String?, maxPx: Int = 900) {
    if (path == null) { setImageDrawable(null); return }
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxPx) sample *= 2
    setImageBitmap(BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample }))
}

/* ---------- Baris informasi ---------- */

object Rows {
    fun info(parent: ViewGroup, label: String, value: String, icon: Int? = null, mono: Boolean = false): TextView {
        val b = ItemInfoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        b.label.text = label
        b.value.text = value
        if (mono) b.value.typeface = android.graphics.Typeface.MONOSPACE
        if (icon != null) b.icon.setImageResource(icon) else b.icon.visibility = View.GONE
        parent.addView(b.root)
        return b.value
    }

    /** Baris dengan konten bebas (mis. badge atau teks dua baris). */
    fun infoCustom(parent: ViewGroup, label: String, icon: Int? = null): LinearLayout {
        val b = ItemInfoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        b.label.text = label
        b.value.visibility = View.GONE
        if (icon != null) b.icon.setImageResource(icon) else b.icon.visibility = View.GONE
        parent.addView(b.root)
        return b.body
    }

    fun kv(parent: ViewGroup, label: String, value: String, mono: Boolean = false, danger: Boolean = false) {
        val b = ItemKvRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        b.label.text = label
        b.value.text = value
        if (mono) b.value.typeface = android.graphics.Typeface.MONOSPACE
        if (danger) b.value.setTextColor(parent.context.color(R.color.danger))
        parent.addView(b.root)
    }
}

/* ---------- Dialog ---------- */

object Modals {
    /** Dialog dari bawah layar dengan layout XML [layout]. */
    @Suppress("DEPRECATION")
    fun bottom(ctx: Context, layout: Int): Pair<Dialog, View> {
        val d = Dialog(ctx, R.style.Dialog_Bottom)
        val v = LayoutInflater.from(ctx).inflate(layout, android.widget.FrameLayout(ctx), false)
        d.setContentView(v)
        d.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        return d to v
    }

    fun photo(ctx: Context, path: String) {
        val d = Dialog(ctx, R.style.Dialog_Full)
        val b = DialogPhotoBinding.inflate(LayoutInflater.from(ctx))
        b.image.loadFile(path, 1600)
        b.root.setOnClickListener { d.dismiss() }
        b.close.setOnClickListener { d.dismiss() }
        d.setContentView(b.root)
        d.show()
    }
}

fun statusBg(ctx: Context, s: id.pamtirtakemuning.watermeter.data.Status): Triple<Int, Int, Int> = when (s) {
    id.pamtirtakemuning.watermeter.data.Status.Belum -> Triple(R.color.muted_15, R.color.gray_text, R.color.muted)
    id.pamtirtakemuning.watermeter.data.Status.Proses -> Triple(R.color.warn_15, R.color.warn_text, R.color.warn)
    id.pamtirtakemuning.watermeter.data.Status.Selesai -> Triple(R.color.success_12, R.color.success_text, R.color.success)
}.let { (first, second, third) -> Triple(ctx.color(first), ctx.color(second), ctx.color(third)) }


/* ---------- Teks kaya (sebagian tebal) ---------- */

private class FontSpan(private val face: android.graphics.Typeface?) : android.text.style.MetricAffectingSpan() {
    override fun updateDrawState(p: android.text.TextPaint) { p.typeface = face }
    override fun updateMeasureState(p: android.text.TextPaint) { p.typeface = face }
}

class RichBuilder(private val ctx: Context) {
    val sb = android.text.SpannableStringBuilder()
    fun t(s: String) { sb.append(s) }
    fun bold(s: String) {
        val start = sb.length
        sb.append(s)
        sb.setSpan(FontSpan(androidx.core.content.res.ResourcesCompat.getFont(ctx, R.font.poppins_semibold)), start, sb.length, 0)
    }
}

fun rich(ctx: Context, block: RichBuilder.() -> Unit): CharSequence = RichBuilder(ctx).apply(block).sb
