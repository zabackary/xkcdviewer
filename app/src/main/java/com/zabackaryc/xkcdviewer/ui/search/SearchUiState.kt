package com.zabackaryc.xkcdviewer.ui.search

import com.zabackaryc.xkcdviewer.data.ComicDao
import com.zabackaryc.xkcdviewer.data.ComicSort
import com.zabackaryc.xkcdviewer.data.ListedComic

data class ActiveSearch(
    val filter: String,
    val onlyFavorites: Boolean,
    val comicSort: ComicSort,
    val results: List<ListedComic>?,
    val resultsNextOffset: Int?, // null if no more results

    val highlightedResult: ListedComic? // e.g. a search by comic ID, etc.
)

data class SearchUiState(
    val offline: Boolean = false,
    val activeSearch: ActiveSearch? = null,

    val historySample: List<ComicDao.HistoryEntryWithListedComic>? = null,
    val latestComicsSample: List<ListedComic>? = null,
    val favoriteComicsSample: List<ListedComic>? = null,
)

