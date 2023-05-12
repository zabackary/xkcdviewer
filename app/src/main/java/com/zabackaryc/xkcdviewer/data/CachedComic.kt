package com.zabackaryc.xkcdviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.zabackaryc.xkcdviewer.network.model.ComicExtraPartsApiModel

@Entity(tableName = "CachedComic")
data class CachedComic(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "mouseover")
    val mouseover: String,

    @ColumnInfo(name = "imgUrl")
    val imgUrl: String,

    @ColumnInfo(name = "transcript")
    val transcript: String?,

    /**
     * This is JSON.
     */
    @ColumnInfo(name = "dynamicHtml")
    val dynamicHtml: String?
) {
    fun getDynamicHtmlModel() =
        dynamicHtml?.let { Gson().fromJson(it, ComicExtraPartsApiModel::class.java) }
}
