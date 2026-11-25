package id.pamtirtakemuning.watermeter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.databinding.ViewPhotoCaptureBinding
import id.pamtirtakemuning.watermeter.databinding.ViewPhotoThumbBinding
import id.pamtirtakemuning.watermeter.ui.loadFile

/** Kotak pengambilan foto (kamera / galeri) dengan pratinjau. Peluncur kamera dimiliki Fragment. */
class PhotoCapture @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val b = ViewPhotoCaptureBinding.inflate(LayoutInflater.from(ctx), this, true)
    var onTake: () -> Unit = {}
    var onGallery: () -> Unit = {}
    var onRemove: () -> Unit = {}
    var onPreview: (String) -> Unit = {}
    private var path: String? = null

    init {
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg_dashed_photo)
        b.take.setOnClickListener { onTake() }
        b.gallery.setOnClickListener { onGallery() }
        b.remove.setOnClickListener { onRemove() }
        b.previewBox.setOnClickListener { path?.let(onPreview) }
    }

    fun set(title: String, desc: String, value: String?) {
        path = value
        b.title.text = title
        b.desc.text = desc
        b.previewBox.visibility = if (value != null) View.VISIBLE else View.GONE
        b.remove.visibility = if (value != null) View.VISIBLE else View.GONE
        b.take.text = if (value != null) "Foto Ulang" else "Ambil Foto"
        b.preview.loadFile(value)
    }
}

/** Thumbnail foto arsip dengan label di bawahnya. */
class PhotoThumb @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {
    private val b = ViewPhotoThumbBinding.inflate(LayoutInflater.from(ctx), this, true)

    init { orientation = VERTICAL }

    fun set(label: String, src: String?, onOpen: (String) -> Unit) {
        b.label.text = label
        b.image.loadFile(src)
        b.empty.visibility = if (src == null) View.VISIBLE else View.GONE
        b.box.isClickable = src != null
        b.box.setOnClickListener { src?.let(onOpen) }
    }
}
