package com.zabackaryc.xkcdviewer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ListedComic")
data class ListedComic(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    /**
     * This is `safe_title`, not `title` from the API!
     */
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date")
    val date: String,

    // User stats

    @ColumnInfo(name = "favorite")
    val favorite: Boolean,

    @ColumnInfo(name = "note")
    val note: String,
)
