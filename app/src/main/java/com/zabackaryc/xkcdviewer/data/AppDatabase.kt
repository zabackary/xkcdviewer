package com.zabackaryc.xkcdviewer.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedComic::class, ListedComic::class, HistoryEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAppDao(): ComicDao
}
