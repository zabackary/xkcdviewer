package com.zabackaryc.xkcdviewer.repository

import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ComicDao
import com.zabackaryc.xkcdviewer.data.HistoryEntry
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.network.ComicsApi
import com.zabackaryc.xkcdviewer.network.ExplainApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    val comics = comicDao.getComics()

    val favoriteComics = comicDao.getFavoriteComics(true)

    val historyEntries = comicDao.getHistory()

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
        val response = comicApi.getListing()
        val comics = response.body()
        return if (response.isSuccessful && comics != null) {
            comicDao.insertListedComics(comics.map { comic -> comic.toListedComic() })
            true
        } else {
            false
        }
    }

    suspend fun deleteHistory() {
        comicDao.deleteComicHistory()
    }

    suspend fun getComicExplanation(id: Long): String? {
        val (_, listed) = getComicFromId(id.toInt()).first { it != null }!!
        val pageTitle = "${listed.id}:_${listed.title.replace(" ", "_")}"
        val response = explainApi.getExplanation(pageTitle)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body.parse.text.content
        } else {
            null
        }
    }

    fun getComicFromId(id: Int) = channelFlow<Pair<CachedComic, ListedComic>?> {
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
                                comicDao.insertCachedComic(cached)
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
