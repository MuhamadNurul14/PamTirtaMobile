package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.databinding.ViewPageHeaderBinding
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.padInsets

/** Header halaman: tombol kembali + judul. Otomatis menambah padding status bar. */
class PageHeader @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val b = ViewPageHeaderBinding.inflate(LayoutInflater.from(ctx), this, true)

    init {
        orientation = HORIZONTAL
        ctx.obtainStyledAttributes(attrs, R.styleable.PageHeader).apply {
            b.title.text = getString(R.styleable.PageHeader_headerTitle) ?: ""
            if (!getBoolean(R.styleable.PageHeader_showBack, true)) b.back.visibility = GONE
            setBackgroundColor(getColor(R.styleable.PageHeader_headerColor, ctx.color(R.color.mist)))
            recycle()
        }
        padInsets(top = true)
    }

    fun setTitle(t: String) { b.title.text = t }
    fun onBack(block: () -> Unit) = b.back.setOnClickListener { block() }
}
