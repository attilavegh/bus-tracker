package hu.attilavegh.vbkoveto.utility

import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

class BitmapUtils {
    companion object {

        fun scaleBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

            val scaleX = newWidth / bitmap.width.toFloat()
            val scaleY = newHeight / bitmap.height.toFloat()
            val pivotX = 0f
            val pivotY = 0f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY)

            val canvas = Canvas(scaledBitmap)
            canvas.matrix = scaleMatrix
            canvas.drawBitmap(bitmap, 0f, 0f, Paint(FILTER_BITMAP_FLAG))

            return scaledBitmap
        }
    }
}