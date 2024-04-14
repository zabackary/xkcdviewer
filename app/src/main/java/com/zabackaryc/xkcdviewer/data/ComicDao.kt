package com.zabackaryc.xkcdviewer.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

enum class ComicSort {
    Default,
    DateNewest,
    DateOldest,
    TitleAZ,
    TitleZA
}

@Dao
interface ComicDao {
    @Query(
        "SELECT * FROM CachedComic WHERE id = :id"
    )
    fun getCachedComic(id: Int): Flow<CachedComic?>

    @Query(
        "SELECT * FROM ListedComic WHERE id = :id"
    )
    fun getListedComic(id: Int): Flow<ListedComic?>

    @Query(
        "SELECT * " +
                "FROM ListedComic " +
                "WHERE (:favorited IS NULL OR favorite = :favorited) AND (:filter IS NULL OR (title LIKE '%' || :filter || '%') OR (note LIKE '%' || :filter || '%')) " +
                "ORDER BY " +
                "CASE WHEN :sort = 0 THEN date END DESC," + // default sort order
                "CASE WHEN :sort = 1 THEN date END DESC," +
                "CASE WHEN :sort = 2 THEN date END ASC, " +
                "CASE WHEN :sort = 3 THEN title END ASC," +
                "CASE WHEN :sort = 4 THEN title END DESC " +
                "LIMIT :limit OFFSET :offset "
    )
    fun getComics(
        favorited: Boolean? = null,
        filter: String? = null,
        limit: Int,
        offset: Int = 0,
        sort: ComicSort
    ): Flow<List<ListedComic>>

    @Query(
        "SELECT COUNT(*) " +
                "FROM ListedComic " +
                "WHERE (:favorited IS NULL OR favorite = :favorited) AND (:filter IS NULL OR (title LIKE '%' || :filter || '%') OR (note LIKE '%' || :filter || '%'))"
    )
    fun countComics(
        favorited: Boolean = false,
        filter: String = ""
    ): Flow<Int>


    data class HistoryEntryWithListedComic(
        @ColumnInfo(name = "history_id")
        val historyId: Int,
        @ColumnInfo(name = "comic_id")
        val comicId: Int,
        val title: String,
        val date: String,
        @ColumnInfo(name = "date_time")
        val dateTime: Long,
        val favorite: Boolean,
        val note: String
    )

    @Query(
        "SELECT HistoryEntry.id as history_id, comic_id, date_time, title, date, favorite, note " +
                "FROM HistoryEntry " +
                "JOIN ListedComic ON HistoryEntry.comic_id = ListedComic.id " +
                "ORDER BY HistoryEntry.date_time DESC " +
                "LIMIT :limit OFFSET :offset"
    )
    fun getHistoryEntriesWithListedComic(
        limit: Int,
        offset: Int = 0
    ): Flow<List<HistoryEntryWithListedComic>>

    @Query(
        "SELECT Count(*) FROM HistoryEntry"
    )
    fun countHistoryEntries(): Flow<Int>

    @Query("SELECT * FROM HistoryEntry WHERE comic_id = :id")
    fun getComicHistoryEntries(id: Int): Flow<List<HistoryEntry>>

    @Query("DELETE FROM HistoryEntry")
    suspend fun deleteAllHistoryEntries()

    @Insert
    suspend fun insertHistoryEntry(entry: HistoryEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListedComics(comics: List<ListedComic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListedComic(comic: ListedComic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedComics(comics: List<CachedComic>)
}
