package com.zabackaryc.xkcdviewer.network.model

import com.zabackaryc.xkcdviewer.data.ListedComic

data class ComicListingApiModel(
    val id: Int,
    val date: String,
    val title: String
) {
    fun toListedComic() = ListedComic(
        id = id,
        date = date,
        favorite = false,
        note = "",
        title = title
    )
}
