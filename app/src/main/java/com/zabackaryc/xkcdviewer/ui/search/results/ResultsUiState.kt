package com.zabackaryc.xkcdviewer.ui.search.results

import com.zabackaryc.xkcdviewer.data.ComicSort
import com.zabackaryc.xkcdviewer.data.ListedComic

data class ResultsUiState(


    val highlightedResult: ListedComic? // e.g. a search by comic ID, etc.
) {
    data class LocalSearch(
        val filter: String,
        val onlyFavorites: Boolean,
        val comicSort: ComicSort,
        val results: List<ListedComic>?,
        val resultsNextOffset: Int?, // null if no more results
    )

    data class ExplainXkcdSearch(
        val filter: String
    )
}
