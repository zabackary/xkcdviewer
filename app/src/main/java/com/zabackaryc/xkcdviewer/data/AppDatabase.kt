package com.zabackaryc.xkcdviewer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CachedComic::class, ListedComic::class, HistoryEntry::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAppDao(): ComicDao
}
