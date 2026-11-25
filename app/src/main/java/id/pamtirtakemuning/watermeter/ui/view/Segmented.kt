package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp
import id.pamtirtakemuning.watermeter.ui.pill

/** Pilihan segmen (tab filter) dengan jumlah opsional. */
class Segmented @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private var options: List<String> = emptyList()
    private var counts: Map<String, Int>? = null
    var value: String = ""
        private set
    private var onChange: (String) -> Unit = {}

    init {
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.bg_card_16)
        setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        elevation = 3.dp.toFloat()
    }

    fun bind(options: List<String>, value: String, counts: Map<String, Int>? = null, onChange: (String) -> Unit) {
        this.options = options; this.value = value; this.counts = counts; this.onChange = onChange
        render()
    }

    fun setCounts(c: Map<String, Int>) { counts = c; render() }

    private fun render() {
        removeAllViews()
        options.forEachIndexed { i, o ->
            val on = o == value
            val cell = LinearLayout(context).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(6.dp, 8.dp, 6.dp, 8.dp)
                background = if (on) androidx.core.content.ContextCompat.getDrawable(context, R.drawable.seg_on) else null
                setOnClickListener { value = o; render(); onChange(o) }
            }
            cell.addView(TextView(context).apply {
                text = o
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
                setTextColor(context.color(if (on) R.color.white else R.color.navy_55))
                maxLines = 1
            })
            counts?.let { c ->
                cell.addView(TextView(context).apply {
                    text = "${c[o] ?: 0}"
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    setTextColor(context.color(if (on) R.color.white else R.color.navy_55))
                    background = pill(context.color(if (on) R.color.white_15 else R.color.mist))
                    setPadding(6.dp, 1.dp, 6.dp, 1.dp)
                }, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply { marginStart = 4.dp })
            }
            addView(cell, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { if (i > 0) marginStart = 4.dp })
        }
    }
}
