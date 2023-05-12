package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zabackaryc.xkcdviewer.repository.ComicRepository
import com.zabackaryc.xkcdviewer.ui.Argument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ComicViewModel @Inject constructor(
    private val comicsRepository: ComicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialComicId = savedStateHandle.get<Int>(Argument.COMIC_ID)

    var uiState by mutableStateOf(ComicUiState())
        private set

    init {
        uiState = uiState.copy(
            currentComicId = initialComicId ?: 1
        )
        initialComicId?.let { comicId ->
            viewModelScope.launch {
                val comics = comicsRepository.comics.first()
                uiState = uiState.copy(
                    data = uiState.data + comics.associateBy({ it.id }, { it to null }),
                    totalComics = comics.size + 1
                )
                launch {
                    comicsRepository.historyEntries.collect {
                        uiState = uiState.copy(
                            historyEntries = it
                        )
                    }
                }
                loadComic(comicId)
            }
        }
    }

    suspend fun loadComic(comicId: Int) {
        withContext(Dispatchers.IO) {
            comicsRepository.getComicFromId(comicId).collect { comics ->
                withContext(Dispatchers.Main) {
                    uiState = if (comics == null) {
                        uiState.copy(offline = true)
                    } else {
                        uiState.copy(
                            offline = false,
                            data = uiState.data + (comics.second.id to (comics.second to comics.first))
                        )
                    }
                }
            }
        }
    }

    fun setCurrentComicId(currentComicId: Int) {
        uiState = uiState.copy(currentComicId = currentComicId)
        viewModelScope.launch {
            comicsRepository.addHistoryEntry(currentComicId)
            loadComic(currentComicId)
        }
    }

    fun setFavoriteComic(comicId: Int, favorite: Boolean) {
        viewModelScope.launch {
            comicsRepository.setFavoriteComic(comicId, favorite)
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            comicsRepository.deleteHistory()
        }
    }
}
