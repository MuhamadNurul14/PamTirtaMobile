package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp

/** Tombol dengan ikon opsional. Varian & ukuran diatur lewat atribut XML `btnVariant` / `btnSize`. */
class PButton @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val iconView = ImageView(ctx)
    private val label = TextView(ctx)
    private var variant = 0
    private var size = 1

    var text: CharSequence
        get() = label.text
        set(v) { label.text = v }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        isClickable = true
        isFocusable = true
        var icon = 0
        ctx.obtainStyledAttributes(attrs, R.styleable.PButton).apply {
            label.text = getString(R.styleable.PButton_btnText) ?: ""
            icon = getResourceId(R.styleable.PButton_btnIcon, 0)
            variant = getInt(R.styleable.PButton_btnVariant, 0)
            size = getInt(R.styleable.PButton_btnSize, 1)
            recycle()
        }
        val (bg, fg) = when (variant) {
            1 -> R.drawable.btn_secondary to R.color.white
            2 -> R.drawable.btn_outline to R.color.navy
            3 -> R.drawable.btn_success to R.color.white
            4 -> R.drawable.btn_danger to R.color.white
            5 -> R.drawable.btn_danger_outline to R.color.danger
            else -> R.drawable.btn_primary to R.color.white
        }
        setBackgroundResource(bg)
        if (variant in 0..1 || variant == 3) elevation = 3.dp.toFloat()
        val small = size == 0
        val large = size == 2
        val h = if (large) 56 else if (small) 40 else 48
        minimumHeight = h.dp
        setPadding((if (small) 12 else 20).dp, 0, (if (small) 12 else 20).dp, 0)

        label.apply {
            setTextColor(ctx.color(fg))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, if (large) 15f else if (small) 12f else 14f)
            typeface = ResourcesCompat.getFont(ctx, R.font.poppins_semibold) ?: Typeface.DEFAULT_BOLD
            includeFontPadding = false
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        if (icon != 0) {
            val s = (if (large) 18 else if (small) 14 else 16).dp
            iconView.setImageResource(icon)
            iconView.imageTintList = ColorStateList.valueOf(ctx.color(fg))
            addView(iconView, LayoutParams(s, s).apply { marginEnd = (if (small) 6 else 8).dp })
        }
        addView(label, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.4f
    }

    fun setIcon(res: Int) { iconView.setImageResource(res) }
}
