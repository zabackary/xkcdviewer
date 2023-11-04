package com.zabackaryc.xkcdviewer.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CachedComic::class, ListedComic::class, HistoryEntry::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAppDao(): ComicDao
}
