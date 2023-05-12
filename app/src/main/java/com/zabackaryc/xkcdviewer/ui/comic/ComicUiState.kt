package com.zabackaryc.xkcdviewer.ui.comic

import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.HistoryEntry
import com.zabackaryc.xkcdviewer.data.ListedComic

data class ComicUiState(
    val offline: Boolean = false,
    val data: Map<Int, Pair<ListedComic?, CachedComic?>> = mapOf(),
    val totalComics: Int? = null,
    val currentComicId: Int = 1,
    val historyEntries: Map<HistoryEntry, ListedComic> = mapOf()
)
