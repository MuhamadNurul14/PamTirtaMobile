package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.ui.color
import id.pamtirtakemuning.watermeter.ui.dp
import id.pamtirtakemuning.watermeter.ui.roundRect

/** Kotak kecil berisi ikon (ikon judul section, avatar ikon, dll). */
class IconChip @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : FrameLayout(ctx, attrs) {
    private val img = ImageView(ctx)
    private var radiusPx = 12.dp

    init {
        var size = 32.dp; var iconSize = 16.dp; var radius = 12.dp
        var bg = ctx.color(R.color.ice); var tint = ctx.color(R.color.navy); var icon = 0
        ctx.obtainStyledAttributes(attrs, R.styleable.IconChip).apply {
            icon = getResourceId(R.styleable.IconChip_chipIcon, 0)
            bg = getColor(R.styleable.IconChip_chipColor, bg)
            tint = getColor(R.styleable.IconChip_chipTint, tint)
            size = getDimensionPixelSize(R.styleable.IconChip_chipSize, size)
            iconSize = getDimensionPixelSize(R.styleable.IconChip_iconSize, iconSize)
            radius = getDimensionPixelSize(R.styleable.IconChip_chipRadius, radius)
            recycle()
        }
        minimumWidth = size; minimumHeight = size
        radiusPx = radius
        background = roundRect(bg, 0).apply { cornerRadius = radius.toFloat() }
        if (icon != 0) img.setImageResource(icon)
        img.imageTintList = ColorStateList.valueOf(tint)
        addView(img, LayoutParams(iconSize, iconSize, Gravity.CENTER))
    }

    override fun onMeasure(w: Int, h: Int) = super.onMeasure(
        MeasureSpec.makeMeasureSpec(minimumWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY),
    )

    fun set(icon: Int, bg: Int? = null, tint: Int? = null) {
        img.setImageResource(icon)
        bg?.let { background = roundRect(it, 0).apply { cornerRadius = radiusPx.toFloat() } }
        tint?.let { img.imageTintList = ColorStateList.valueOf(it) }
    }
}
