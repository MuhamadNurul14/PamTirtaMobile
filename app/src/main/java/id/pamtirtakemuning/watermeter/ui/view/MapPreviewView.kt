package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp

/** Pratinjau peta sederhana (digambar manual) dengan pin lokasi pelanggan. */
class MapPreviewView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : View(ctx, attrs) {
    var name: String = ""
        set(v) { field = v; invalidate() }
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clip = Path()

    override fun onMeasure(w: Int, h: Int) = setMeasuredDimension(MeasureSpec.getSize(w), 144.dp)

    override fun onDraw(c: Canvas) {
        clip.reset(); clip.addRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), 16.dp.toFloat(), 16.dp.toFloat(), Path.Direction.CW)
        c.clipPath(clip)
        c.drawColor(Color.parseColor("#E8F1F8"))
        val s = maxOf(width / 320f, height / 144f)
        c.save()
        c.translate((width - 320f * s) / 2f, (height - 144f * s) / 2f)
        c.scale(s, s)
        p.style = Paint.Style.FILL; p.color = Color.parseColor("#CFE8D6")
        c.drawRoundRect(190f, 70f, 300f, 130f, 10f, 10f, p)
        p.style = Paint.Style.STROKE; p.strokeWidth = 14f; p.color = Color.parseColor("#9FD0F2")
        c.drawPath(Path().apply { moveTo(0f, 100f); cubicTo(80f, 80f, 140f, 130f, 320f, 40f) }, p)
        p.strokeWidth = 9f; p.color = Color.WHITE; p.strokeCap = Paint.Cap.ROUND
        c.drawLine(-10f, 40f, 330f, 40f, p); c.drawLine(90f, -10f, 90f, 160f, p)
        c.drawLine(230f, -10f, 230f, 160f, p); c.drawLine(0f, 130f, 120f, 0f, p)
        p.strokeWidth = 1.5f; p.color = Color.parseColor("#D3E1EE")
        c.drawLine(0f, 70f, 320f, 70f, p); c.drawLine(160f, 0f, 160f, 144f, p)
        c.restore()
        p.strokeCap = Paint.Cap.BUTT

        // pin navy dengan ikon tetes air
        val cx = width / 2f
        val cy = height / 2f - 18.dp
        c.save(); c.rotate(-45f, cx, cy)
        p.style = Paint.Style.FILL; p.color = context.color(R.color.navy)
        val r = 18.dp.toFloat()
        val pin = Path().apply {
            addRoundRect(RectF(cx - r, cy - r, cx + r, cy + r), floatArrayOf(r, r, r, r, r, r, 0f, 0f), Path.Direction.CW)
        }
        c.drawPath(pin, p)
        c.restore()
        ContextCompat.getDrawable(context, R.drawable.ic_droplets)?.mutate()?.let {
            it.setTint(Color.WHITE)
            val h = 8.dp
            it.setBounds((cx - h).toInt(), (cy - h).toInt(), (cx + h).toInt(), (cy + h).toInt())
            it.draw(c)
        }
        // label nama
        p.textSize = 10.dp.toFloat() * 0.95f
        p.typeface = androidx.core.content.res.ResourcesCompat.getFont(context, R.font.poppins_semibold)
        val tw = p.measureText(name)
        val pad = 8.dp.toFloat()
        val x = 8.dp.toFloat(); val bottom = height - 8.dp.toFloat(); val top = bottom - 22.dp
        p.color = Color.parseColor("#E6FFFFFF")
        c.drawRoundRect(x, top, x + tw + pad * 2, bottom, 8.dp.toFloat(), 8.dp.toFloat(), p)
        p.color = context.color(R.color.navy)
        c.drawText(name, x + pad, bottom - 7.dp, p)
    }
}
