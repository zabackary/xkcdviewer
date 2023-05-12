package com.zabackaryc.xkcdviewer.ui.search

import com.zabackaryc.xkcdviewer.data.ListedComic

data class SearchUiState(
    val term: String = "",
    val list: List<ListedComic> = listOf(),
    val filteredList: List<ListedComic> = listOf(),
    val offline: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NewestToOldest,
    val filteringFavorites: Boolean = false
)

