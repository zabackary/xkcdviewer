package com.zabackaryc.xkcdviewer.repository

import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ComicDao
import com.zabackaryc.xkcdviewer.data.ComicSort
import com.zabackaryc.xkcdviewer.data.HistoryEntry
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.network.ComicsApi
import com.zabackaryc.xkcdviewer.network.ExplainApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class ComicRepository @Inject constructor(
    private val comicApi: ComicsApi,
    private val comicDao: ComicDao,
    private val explainApi: ExplainApi
) {
    fun getLatestComicId(): Flow<Int> {
        return comicDao.getLatestComicId()
    }

    fun searchComics(
        favorited: Boolean? = null,
        filter: String = "",
        limit: Int,
        offset: Int = 0,
        sort: ComicSort
    ): Flow<List<ListedComic>> {
        return comicDao.getComics(favorited, filter, limit, offset, sort)
    }

    fun listHistory(
        limit: Int,
        offset: Int = 0
    ): Flow<List<ComicDao.HistoryEntryWithListedComic>> {
        return comicDao.getHistoryEntriesWithListedComic(limit, offset)
    }

    fun countComics(
        favorited: Boolean = false,
        filter: String = ""
    ): Flow<Int> {
        return comicDao.countComics(favorited, filter)
    }

    suspend fun addHistoryEntry(comicId: Int) {
        comicDao.insertHistoryEntry(
            HistoryEntry(
                comicId = comicId, dateTime = Instant.now().toEpochMilli()
            )
        )
    }

    suspend fun setFavoriteComic(comicId: Int, favorite: Boolean) {
        val current = comicDao.getListedComic(comicId).first()
            ?: throw Exception("Comic not in listed database: $comicId")
        comicDao.updateListedComic(current.copy(favorite = favorite))
    }

    suspend fun refreshComics(): Boolean {
        // TODO: decide based on how stale the last comic is whether to do a full fetch or just use
        //       the ATOM feed to get the latest comics.
        val response = comicApi.getListing()
        val comics = response.body()
        return if (response.isSuccessful && comics != null) {
            comicDao.insertListedComics(comics.map { it.toListedComic() })
            true
        } else {
            false
        }
    }

    fun countHistoryEntries(): Flow<Int> {
        return comicDao.countHistoryEntries()
    }

    suspend fun deleteHistory() {
        comicDao.deleteAllHistoryEntries()
    }

    suspend fun getComicExplanation(id: Int): String? {
        val (_, listed) = getComicById(id).first { it != null }!!
        val pageTitle = "${listed.id}:_${listed.title.replace(" ", "_")}"
        val response = explainApi.getExplanation(pageTitle)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body.parse.text.content
        } else {
            null
        }
    }

    fun getListedComicById(id: Int): Flow<ListedComic?> {
        return comicDao.getListedComic(id)
    }

    fun getComicById(id: Int) = channelFlow<Pair<CachedComic, ListedComic>?> {
        var listedComic: ListedComic? = null
        var cachedComic: CachedComic? = null
        suspend fun checkComplete() {
            listedComic?.let { listed ->
                cachedComic?.let { cached ->
                    send(Pair(cached, listed))
                }
            }
        }
        coroutineScope {
            withContext(Dispatchers.IO) {
                launch {
                    comicDao.getListedComic(id).collect {
                        listedComic = it
                        checkComplete()
                    }
                }
                launch {
                    comicDao.getCachedComic(id).collect {
                        it?.let { cachedComic = it }
                        if (it == null) {
                            val response = comicApi.getComic(id)
                            val comic = response.body()
                            if (response.isSuccessful && comic != null) {
                                val cached = comic.toCachedComic()
                                val listed = comic.toListedComic()
                                listedComic = listed
                                cachedComic = cached
                                comicDao.upsertCachedComics(listOf(cached))
                                comicDao.insertListedComics(listOf(listed))
                            }
                        }
                        checkComplete()
                    }
                }
            }
        }
    }
}
