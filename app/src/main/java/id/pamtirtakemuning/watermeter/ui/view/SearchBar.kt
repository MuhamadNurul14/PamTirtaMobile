package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.databinding.ViewSearchBarBinding
import id.pamtirtakemuning.watermeter.ui.dp

/** Kolom pencarian dengan tombol hapus. */
class SearchBar @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val b = ViewSearchBarBinding.inflate(LayoutInflater.from(ctx), this, true)
    private var onChange: (String) -> Unit = {}

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundResource(R.drawable.bg_card_16)
        elevation = 3.dp.toFloat()
        setPadding(14.dp, 0, 14.dp, 0)
        minimumHeight = 48.dp
        b.input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, c: Int, d: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, c: Int, d: Int) {}
            override fun afterTextChanged(s: Editable?) {
                b.clear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                onChange(s?.toString().orEmpty())
            }
        })
        b.clear.setOnClickListener { b.input.setText("") }
    }

    fun bind(hint: String, onChange: (String) -> Unit) { b.input.hint = hint; this.onChange = onChange }
}
