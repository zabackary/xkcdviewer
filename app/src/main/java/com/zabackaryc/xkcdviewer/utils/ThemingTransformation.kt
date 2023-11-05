package com.zabackaryc.xkcdviewer.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.abs

class ThemingTransformation(
    private val darkMode: Boolean,
    private val foregroundColor: Int,
    private val backgroundColor: Int
) :
    Transformation {
    override val cacheKey =
        "${ThemingTransformation::class.qualifiedName}-$darkMode-$foregroundColor-$backgroundColor"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val hasColor = input.let { _ ->
            for (x in 0 until input.width) {
                for (y in 0 until input.height) {
                    if (input.getPixel(x, y).let {
                            abs(it.red - it.green) > 1 || abs(it.blue - it.green) > 1 || abs(it.blue - it.red) > 1
                        }) return@let true
                }
            }
            false
        }
        return if (hasColor) {
            input
        } else {
            input.copy(Bitmap.Config.ARGB_8888, true).apply {
                val pixels = IntArray(width * height)
                getPixels(pixels, 0, width, 0, 0, width, height)
                for (i in 0 until width * height) {
                    pixels[i] = if (pixels[i] == Color.WHITE) {
                        backgroundColor
                    } else if (pixels[i] == Color.BLACK) {
                        foregroundColor
                    } else if (darkMode) {
                        pixels[i] xor 0x00ffffff
                    } else {
                        pixels[i]
                    }
                }
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        }
    }
}
