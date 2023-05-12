package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zabackaryc.xkcdviewer.repository.ComicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val comicsRepository: ComicRepository
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch { comicsRepository.refreshComics() }
            comicsRepository.comics.collect { list ->
                withContext(Dispatchers.Main) {
                    if (list.isEmpty()) {
                        uiState = uiState.copy(offline = true)
                    } else {
                        uiState = uiState.copy(
                            list = list,
                            offline = false
                        )
                        recomputeSortFilter()
                    }
                }
            }
        }
    }

    private fun recomputeSortFilter() {
        val dateFormatter = DateTimeFormatter.ofPattern("u-M-d")
        uiState = uiState.copy(
            filteredList = uiState.list.filter { !uiState.filteringFavorites || it.favorite }
                .sortedWith(when (uiState.sortOrder) {
                    SortOrder.NewestToOldest -> Comparator { a, b ->
                        -LocalDate.parse(a.date, dateFormatter)
                            .compareTo(LocalDate.parse(b.date, dateFormatter))
                    }
                    SortOrder.OldestToNewest -> Comparator { a, b ->
                        LocalDate.parse(a.date, dateFormatter)
                            .compareTo(LocalDate.parse(b.date, dateFormatter))
                    }
                    SortOrder.Title -> Comparator.comparing { it.title }
                }).filter { it.title.lowercase().contains(uiState.term.lowercase()) }
        )
    }

    fun setFilteringFavorites(filteringFavorites: Boolean? = null) {
        uiState = uiState.copy(
            filteringFavorites = when (filteringFavorites) {
                true, false -> filteringFavorites
                else -> !uiState.filteringFavorites
            },
        )
        recomputeSortFilter()
    }

    fun setSort(sortOrder: SortOrder) {
        uiState = uiState.copy(
            sortOrder = sortOrder
        )
        recomputeSortFilter()
    }

    fun setTerm(term: String) {
        uiState = uiState.copy(
            term = term
        )
        recomputeSortFilter()
    }

    fun setFavoriteComic(comicId: Int, favorite: Boolean) {
        viewModelScope.launch {
            comicsRepository.setFavoriteComic(comicId, favorite)
        }
    }
}
