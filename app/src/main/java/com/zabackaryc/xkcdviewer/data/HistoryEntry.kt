package com.zabackaryc.xkcdviewer.data

import androidx.room.*

@Entity(
    tableName = "HistoryEntry",
    foreignKeys = [
        ForeignKey(
            entity = ListedComic::class,
            childColumns = ["comic_id"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = arrayOf("comic_id"), unique = false)
    ]
)
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "comic_id")
    val comicId: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: Long // Ready for 2038 :)
)
