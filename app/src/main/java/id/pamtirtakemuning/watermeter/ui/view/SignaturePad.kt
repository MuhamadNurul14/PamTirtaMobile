package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.databinding.ViewSignaturePadBinding
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp
import id.pamtirtakemuning.watermeter.util.Photos

/** Kanvas goresan tangan. Hasil dikirim sebagai PNG transparan lewat [onChange]. */
class SignatureCanvas @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : View(ctx, attrs) {
    private val strokes = mutableListOf<MutableList<Pair<Float, Float>>>()
    private var base: Bitmap? = null
    var onChange: (String?) -> Unit = {}
    var onEmptyChanged: (Boolean) -> Unit = {}
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.color(R.color.navy); style = Paint.Style.STROKE; strokeWidth = 2.5f.dp
        strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
    }

    val isEmpty get() = strokes.isEmpty() && base == null

    fun load(path: String?) {
        base = path?.let { runCatching { BitmapFactory.decodeFile(it) }.getOrNull() }
        strokes.clear(); invalidate(); onEmptyChanged(isEmpty)
    }

    private fun pathOf(s: List<Pair<Float, Float>>) = Path().apply {
        s.forEachIndexed { i, o -> if (i == 0) moveTo(o.first, o.second) else lineTo(o.first, o.second) }
    }

    override fun onDraw(c: Canvas) {
        base?.let { c.drawBitmap(it, null, android.graphics.Rect(0, 0, width, height), null) }
        strokes.forEach { s ->
            if (s.size == 1) { paint.style = Paint.Style.FILL; c.drawCircle(s[0].first, s[0].second, paint.strokeWidth / 2, paint); paint.style = Paint.Style.STROKE }
            else c.drawPath(pathOf(s), paint)
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> { parent.requestDisallowInterceptTouchEvent(true); strokes.add(mutableListOf(e.x to e.y)); onEmptyChanged(false) }
            MotionEvent.ACTION_MOVE -> strokes.lastOrNull()?.add(e.x to e.y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { parent.requestDisallowInterceptTouchEvent(false); commit() }
        }
        invalidate()
        return true
    }

    fun clear() { strokes.clear(); base = null; commit(); invalidate() }
    fun undo() { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) else base = null; commit(); invalidate() }

    private fun commit() {
        onEmptyChanged(isEmpty)
        if (isEmpty) { onChange(null); return }
        val bmp = Bitmap.createBitmap(maxOf(width, 1), maxOf(height, 1), Bitmap.Config.ARGB_8888)
        draw(Canvas(bmp))
        onChange(Photos.savePng(context, bmp))
    }
}

/** Area tanda tangan + tombol Hapus / Ulangi. */
class SignaturePad @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val b = ViewSignaturePadBinding.inflate(LayoutInflater.from(ctx), this, true)

    init {
        orientation = VERTICAL
        b.canvas.onEmptyChanged = { b.hint.visibility = if (it) VISIBLE else GONE }
        b.clear.setOnClickListener { b.canvas.clear() }
        b.undo.setOnClickListener { b.canvas.undo() }
    }

    fun bind(value: String?, onChange: (String?) -> Unit) {
        b.canvas.load(value)
        b.canvas.onChange = onChange
    }
}
