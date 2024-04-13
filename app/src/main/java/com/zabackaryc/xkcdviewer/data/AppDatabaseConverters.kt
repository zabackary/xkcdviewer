package com.zabackaryc.xkcdviewer.data

import androidx.room.TypeConverter

class AppDatabaseConverters {
    @TypeConverter
    fun comicSortToInt(comicSort: ComicSort?): Int? {
        return comicSort?.ordinal
    }
}
