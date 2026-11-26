package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.data.Status
import id.pamtirtakemuning.watermeter.databinding.ItemStageBinding
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp

/** Indikator tahapan kerja: Belum -> konfirmasi, Proses -> eksekusi, Selesai -> semua tuntas. */
class JobStepper @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 2.dp.toFloat() }
    private var done = 0

    private val stages = listOf(
        Triple("Konfirmasi", "Foto WM lama", R.drawable.ic_camera),
        Triple("Eksekusi", "Pasang WM baru", R.drawable.ic_wrench),
        Triple("Selesai", "Hasil kerja", R.drawable.ic_badge_check),
    )

    init {
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.bg_card)
        elevation = 3.dp.toFloat()
        setPadding(12.dp, 14.dp, 12.dp, 14.dp)
        setWillNotDraw(false)
        setStatus(Status.Belum)
    }

    fun setStatus(s: Status) {
        done = when (s) { Status.Belum -> 0; Status.Proses -> 1; Status.Ditolak -> 1; Status.Validasi -> 2; Status.Selesai -> 4 }
        removeAllViews()
        val inflater = LayoutInflater.from(context)
        stages.forEachIndexed { i, (label, sub, icon) ->
            val b = ItemStageBinding.inflate(inflater, this, false)
            val isDone = i < done
            val isNow = i == done
            b.label.text = label
            b.sub.text = sub
            b.circle.backgroundTintList = ColorStateList.valueOf(context.color(if (isDone) R.color.success else if (isNow) R.color.sky else R.color.mist))
            b.halo.visibility = if (isNow) VISIBLE else GONE
            b.icon.setImageResource(if (isDone) R.drawable.ic_check else icon)
            b.icon.imageTintList = ColorStateList.valueOf(context.color(if (isDone || isNow) R.color.white else R.color.navy_35))
            b.label.setTextColor(context.color(if (isDone) R.color.success else if (isNow) R.color.navy else R.color.navy_40))
            addView(b.root, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
        }
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val w = width - paddingLeft - paddingRight
        val cell = w / 3f
        val y = paddingTop + 22.dp.toFloat()
        for (i in 1..2) {
            line.color = context.color(if (i <= done) R.color.success else R.color.navy_10)
            canvas.drawLine(paddingLeft + cell * (i - 0.5f) + 24.dp, y, paddingLeft + cell * (i + 0.5f) - 24.dp, y, line)
        }
        super.dispatchDraw(canvas)
    }
}
