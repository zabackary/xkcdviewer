package com.zabackaryc.xkcdviewer.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.abs

class DarkThemeTransformation(private val darkTheme: Boolean) : Transformation {
    override val cacheKey = "${DarkThemeTransformation::class.qualifiedName}-$darkTheme"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return if (darkTheme) {
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
            if (hasColor) {
                input
            } else {
                input.copy(Bitmap.Config.ARGB_8888, true).apply {
                    val pixels = IntArray(width * height)
                    getPixels(pixels, 0, width, 0, 0, width, height)
                    for (i in 0 until width * height) {
                        pixels[i] = if (pixels[i] == Color.WHITE) {
                            Color.TRANSPARENT
                        } else {
                            pixels[i] xor 0x00FFFFFF
                        }
                    }
                    setPixels(pixels, 0, width, 0, 0, width, height)
                }
            }
        } else {
            input
        }
    }
}
