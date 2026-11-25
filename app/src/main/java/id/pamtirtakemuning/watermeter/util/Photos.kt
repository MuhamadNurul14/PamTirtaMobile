package id.pamtirtakemuning.watermeter.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/** Penyimpanan foto di memori internal aplikasi (files/photos). Database hanya menyimpan path file. */
object Photos {
    private fun dir(c: Context) = File(c.filesDir, "photos").apply { mkdirs() }

    fun newFile(c: Context, ext: String = "jpg"): File = File(dir(c), "${UUID.randomUUID()}.$ext")

    fun uriFor(c: Context, f: File): Uri = FileProvider.getUriForFile(c, "${c.packageName}.fileprovider", f)

    /** Foto kamera: perkecil, putar sesuai EXIF, lalu timpa file asal. */
    fun processCamera(c: Context, f: File, max: Int = 1280): String? = try {
        val bmp = decode(max, { BitmapFactory.decodeFile(f.path, it) }, { ExifInterface(f.path) })
        if (bmp == null) null else { save(bmp, f); f.path }
    } catch (e: Exception) { null }

    /** Foto galeri: salin ke penyimpanan aplikasi dalam ukuran yang diperkecil. */
    fun importUri(c: Context, uri: Uri, max: Int = 1280): String? = try {
        val bmp = decode(max,
            { o -> c.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, o) } },
            { ExifInterface(c.contentResolver.openInputStream(uri)!!) })
        if (bmp == null) null else newFile(c).also { save(bmp, it) }.path
    } catch (e: Exception) { null }

    private fun decode(max: Int, read: (BitmapFactory.Options) -> Bitmap?, exif: () -> ExifInterface): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        read(bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= max) sample *= 2
        val raw = read(BitmapFactory.Options().apply { inSampleSize = sample }) ?: return null
        val k = minOf(1f, max.toFloat() / maxOf(raw.width, raw.height))
        val rotation = when (runCatching { exif().getAttributeInt(ExifInterface.TAG_ORIENTATION, 1) }.getOrDefault(1)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val m = Matrix().apply { postScale(k, k); postRotate(rotation) }
        return Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, m, true)
    }

    private fun save(b: Bitmap, f: File) = FileOutputStream(f).use { b.compress(Bitmap.CompressFormat.JPEG, 80, it) }

    fun savePng(c: Context, b: Bitmap): String = newFile(c, "png").also { f -> FileOutputStream(f).use { b.compress(Bitmap.CompressFormat.PNG, 100, it) } }.path
}
