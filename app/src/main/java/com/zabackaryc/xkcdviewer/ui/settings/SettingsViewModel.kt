package com.zabackaryc.xkcdviewer.ui.settings

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
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val comicsRepository: ComicRepository
) : ViewModel() {
    var totalHistoryEntries by mutableStateOf<Int?>(null)
        private set

    var isDeletingHistory by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            comicsRepository.countHistoryEntries().collect {
                withContext(Dispatchers.Main) {
                    totalHistoryEntries = it
                }
            }
        }
    }

    suspend fun deleteHistory() {
        withContext(Dispatchers.Main) {
            isDeletingHistory = true
        }
        withContext(Dispatchers.IO) {
            comicsRepository.deleteHistory()
        }
        withContext(Dispatchers.Main) {
            isDeletingHistory = false
        }
    }
}
