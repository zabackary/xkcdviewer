package com.zabackaryc.xkcdviewer.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {
    @Query("SELECT * FROM ListedComic")
    fun getComics(): Flow<List<ListedComic>>

    @Query(
        "SELECT * FROM CachedComic WHERE id = :id"
    )
    fun getCachedComic(id: Int): Flow<CachedComic?>

    @Query(
        "SELECT * FROM ListedComic WHERE id = :id"
    )
    fun getListedComic(id: Int): Flow<ListedComic?>

    @Query("SELECT * FROM ListedComic WHERE title LIKE :title")
    fun getAllMatchingComics(title: String): Flow<List<ListedComic>?>

    @Query("SELECT * FROM ListedComic WHERE favorite = :favorited")
    fun getFavoriteComics(
        favorited: Boolean
    ): Flow<List<ListedComic>>

    @Query("SELECT * FROM ListedComic WHERE title LIKE :title AND favorite = :favorited")
    fun getFavoritedMatchingComics(
        favorited: Boolean,
        title: String
    ): Flow<List<ListedComic>>

    @Query("SELECT * FROM HistoryEntry JOIN ListedComic ON HistoryEntry.comic_id = ListedComic.id")
    fun getHistory(): Flow<Map<HistoryEntry, ListedComic>>

    @Query("SELECT * FROM ListedComic JOIN HistoryEntry ON ListedComic.id = HistoryEntry.comic_id WHERE ListedComic.id = :listedComicId")
    suspend fun getComicHistory(listedComicId: Int): Map<ListedComic, List<HistoryEntry>>

    @Query("DELETE FROM HistoryEntry")
    suspend fun deleteComicHistory()

    @Insert
    suspend fun insertHistoryEntry(entry: HistoryEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListedComics(comics: List<ListedComic>)

    @Update
    suspend fun updateListedComic(comic: ListedComic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedComic(comic: CachedComic)
}
