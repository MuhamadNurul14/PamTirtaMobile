package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp

/** Pilih satu dari beberapa tombol (kondisi Baik/Rusak/Tidak Ada, posisi meter, jenis masalah). */
class OptionGroup @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private var options: List<String> = emptyList()
    private var cols = 3
    var value: String? = null
        private set
    private var onChange: (String) -> Unit = {}

    init { orientation = VERTICAL }

    fun bind(options: List<String>, value: String?, cols: Int = options.size, onChange: (String) -> Unit) {
        this.options = options; this.value = value; this.cols = cols; this.onChange = onChange
        render()
    }

    fun select(v: String?) { value = v; render() }

    private fun render() {
        removeAllViews()
        options.chunked(cols).forEachIndexed { r, row ->
            val line = LinearLayout(context).apply { orientation = HORIZONTAL }
            row.forEachIndexed { i, o ->
                val on = value == o
                val (bg, fg) = when {
                    !on -> R.drawable.opt_off to R.color.navy_60
                    o == "Rusak" -> R.drawable.opt_rusak to R.color.danger
                    o == "Tidak Ada" -> R.drawable.opt_none to R.color.gray_text
                    else -> R.drawable.opt_on to R.color.white
                }
                line.addView(TextView(context).apply {
                    text = o
                    gravity = Gravity.CENTER
                    minHeight = 40.dp
                    setPadding(8.dp, 6.dp, 8.dp, 6.dp)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
                    setTextColor(context.color(fg))
                    setBackgroundResource(bg)
                    setOnClickListener { value = o; render(); onChange(o) }
                }, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { if (i > 0) marginStart = 6.dp })
            }
            repeat(cols - row.size) { line.addView(View(context), LayoutParams(0, 1, 1f).apply { marginStart = 6.dp }) }
            addView(line, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { if (r > 0) topMargin = 6.dp })
        }
    }
}
