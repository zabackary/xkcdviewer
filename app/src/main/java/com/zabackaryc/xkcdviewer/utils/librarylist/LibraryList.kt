package com.zabackaryc.xkcdviewer.utils.librarylist

import android.content.Context
import com.mikepenz.aboutlibraries.Libs
import com.zabackaryc.xkcdviewer.R

class LibraryList {
    companion object {
        private var libraryList: Libs? = null

        fun getLibraryList(context: Context): Libs = libraryList
            ?: Libs.Builder()
                .withJson(
                    context
                        .resources
                        .openRawResource(R.raw.aboutlibraries)
                        .bufferedReader()
                        .use { it.readText() }
                )
                .build()
                .also {
                    libraryList = it
                }
    }
}
