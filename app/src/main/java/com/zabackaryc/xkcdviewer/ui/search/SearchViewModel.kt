package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zabackaryc.xkcdviewer.data.ComicSort
import com.zabackaryc.xkcdviewer.repository.ComicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val COMIC_PREVIEW_COUNT = 10

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val comicsRepository: ComicRepository
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            comicsRepository.refreshComics()
            launch {
                comicsRepository.searchComics(
                    limit = COMIC_PREVIEW_COUNT,
                    offset = 0,
                    sort = ComicSort.DateNewest
                ).collect { list ->
                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(
                            latestComicsSample = list
                        )
                    }
                }
            }
            launch {
                comicsRepository.searchComics(
                    limit = COMIC_PREVIEW_COUNT,
                    offset = 0,
                    favorited = true,
                    sort = ComicSort.DateNewest
                ).collect { list ->
                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(
                            favoriteComicsSample = list
                        )
                    }
                }
            }
            launch {
                comicsRepository.listHistory(
                    limit = COMIC_PREVIEW_COUNT,
                    offset = 0
                ).collect { list ->
                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(
                            historySample = list
                        )
                    }
                }
            }
        }
    }

    fun loadNextSearchPage(
        pageSize: Int = 20
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newActiveSearch = uiState.activeSearch?.let { activeSearch ->
                if (activeSearch.resultsNextOffset == null) {
                    activeSearch
                } else {
                    val newResults = comicsRepository.searchComics(
                        favorited = activeSearch.onlyFavorites,
                        filter = activeSearch.filter,
                        sort = activeSearch.comicSort,
                        limit = pageSize,
                        offset = activeSearch.resultsNextOffset
                    ).first()
                    activeSearch.copy(
                        results = (if (activeSearch.resultsNextOffset == 0) listOf() else activeSearch.results
                            ?: listOf()) + newResults,
                        resultsNextOffset = if (newResults.size < pageSize) null
                        else activeSearch.resultsNextOffset + newResults.size
                    )
                }
            }
            withContext(Dispatchers.Main) {
                uiState = uiState.copy(
                    activeSearch = newActiveSearch
                )
            }
        }
    }

    fun updateActiveSearch(
        filter: String? = null,
        onlyFavorites: Boolean? = null,
        comicSort: ComicSort? = null
    ) {
        if (uiState.activeSearch == null) beginSearch()
        val activeSearch = uiState.activeSearch
            ?: // should never happen
            return
        uiState = uiState.copy(
            activeSearch = activeSearch.copy(
                filter = filter ?: activeSearch.filter,
                onlyFavorites = onlyFavorites ?: activeSearch.onlyFavorites,
                comicSort = comicSort ?: activeSearch.comicSort,

                resultsNextOffset = 0
            )
        )
        loadNextSearchPage()
    }

    fun beginSearch() {
        uiState = uiState.copy(
            activeSearch = ActiveSearch(
                filter = "",
                onlyFavorites = false,
                comicSort = ComicSort.Default,
                results = null,
                resultsNextOffset = null
            )
        )
    }

    fun endSearch() {
        uiState = uiState.copy(
            activeSearch = null
        )
    }

    fun setFavoriteComic(comicId: Int, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            comicsRepository.setFavoriteComic(comicId, favorite)
        }
    }
}
