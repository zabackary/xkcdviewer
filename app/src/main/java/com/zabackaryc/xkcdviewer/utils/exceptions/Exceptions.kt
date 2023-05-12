package com.zabackaryc.xkcdviewer.utils.exceptions

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zabackaryc.xkcdviewer.R

class XkcdExceptions {
    companion object {
        private var xkcdExceptions: List<XkcdException>? = null

        fun getExceptions(context: Context): List<XkcdException> = xkcdExceptions
            ?: Gson()
                .fromJson<List<XkcdException>>(
                    context
                        .resources
                        .openRawResource(R.raw.xkcd_exceptions)
                        .bufferedReader()
                        .use { it.readText() },
                    object : TypeToken<List<XkcdException>>() {}.type
                )
                .also {
                    xkcdExceptions = it
                }
    }
}
