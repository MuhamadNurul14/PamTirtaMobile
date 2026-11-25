package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.ui.dp
import id.pamtirtakemuning.watermeter.ui.pill
import id.pamtirtakemuning.watermeter.ui.statusBg

/** Lencana status: titik berwarna + teks (Belum / Proses / Selesai). */
class StatusBadge @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val dot = View(ctx)
    private val label = TextView(ctx)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(10.dp, 4.dp, 10.dp, 4.dp)
        addView(dot, LayoutParams(6.dp, 6.dp).apply { marginEnd = 6.dp })
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        label.includeFontPadding = false
        label.typeface = ResourcesCompat.getFont(ctx, R.font.poppins_semibold)
        addView(label)
        setStatus(Status.Belum)
    }

    fun setStatus(s: Status) {
        val (bg, fg, d) = statusBg(context, s)
        background = pill(bg)
        label.setTextColor(fg)
        label.text = s.label
        dot.background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(d) }
    }
}
