package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupMenu
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.databinding.ViewSelectFieldBinding
import id.pamtirtakemuning.watermeter.ui.color

/** Dropdown pilihan (Merk, Tipe water meter). */
class SelectField @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val b = ViewSelectFieldBinding.inflate(LayoutInflater.from(ctx), this, true)
    private var placeholder = ""
    private var options: List<String> = emptyList()
    var value: String = ""
        private set
    private var onChange: (String) -> Unit = {}

    init {
        orientation = VERTICAL
        ctx.obtainStyledAttributes(attrs, R.styleable.SelectField).apply {
            b.label.text = getString(R.styleable.SelectField_fieldLabel) ?: ""
            placeholder = getString(R.styleable.SelectField_fieldPlaceholder) ?: ""
            recycle()
        }
        b.box.setOnClickListener {
            PopupMenu(context, b.box).apply {
                options.forEach { menu.add(it) }
                setOnMenuItemClickListener { item -> set(item.title.toString()); onChange(value); true }
            }.show()
        }
        render()
    }

    fun bind(options: List<String>, value: String, onChange: (String) -> Unit) {
        this.options = options; this.onChange = onChange; set(value)
    }

    fun set(v: String) { value = v; render() }

    private fun render() {
        b.text.text = value.ifEmpty { placeholder }
        b.text.setTextColor(context.color(if (value.isEmpty()) R.color.navy_35 else R.color.navy))
    }
}
