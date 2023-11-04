package com.zabackaryc.xkcdviewer.network.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic

data class ComicApiModel(
    @SerializedName("alt")
    val alt: String,
    @SerializedName("day")
    val day: String,
    @SerializedName("img")
    val img: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("month")
    val month: String,
    @SerializedName("news")
    val news: String,
    @SerializedName("num")
    val num: Int,
    @SerializedName("safe_title")
    val safeTitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("transcript")
    val transcript: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("extra_parts")
    val extraParts: ComicExtraPartsApiModel?
) {
    fun toCachedComic() = CachedComic(
        id = num,
        mouseover = alt,
        imgUrl = img,
        transcript = transcript.ifEmpty { null },
        dynamicHtml = if (extraParts != null) Gson().toJson(extraParts) else null,
        newsContent = news.ifEmpty { null },
        link = link.ifEmpty { null }
    )

    fun toListedComic() = ListedComic(
        id = num,
        date = "$year-$month-$day",
        favorite = false,
        note = "",
        title = safeTitle
    )
}
