package com.zabackaryc.xkcdviewer.ui.comic

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.repository.ComicRepository
import com.zabackaryc.xkcdviewer.ui.Argument
import com.zabackaryc.xkcdviewer.utils.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ComicViewModel @Inject constructor(
    private val comicsRepository: ComicRepository,
    private val okHttpClient: OkHttpClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialComicId = savedStateHandle.get<Int>(Argument.ComicId.name)

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

    suspend fun shareComic(context: Context, listedComic: ListedComic, cachedComic: CachedComic) {
        val imagePath = File(context.filesDir, "shared_comic_images")
        if (!imagePath.exists()) {
            imagePath.mkdir()
        }
        val file = File(imagePath, cachedComic.imgUrl.substringAfterLast("/"))
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
                val request = Request.Builder().url(cachedComic.imgUrl).build()
                val response = okHttpClient.newCall(request).await()
                response.body?.let {
                    val sink = file.sink().buffer()
                    sink.writeAll(it.source())
                    sink.close()
                }
            }
        }
        val fileUri = FileProvider.getUriForFile(
            context, "com.zabackaryc.xkcdviewer.fileprovider", file
        )
        val clipDataThumbnail = ClipData.newUri(context.contentResolver, null, fileUri)
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT, "https://xkcd.com/${cachedComic.id}/"
            )
            type = "text/plain"
            clipData = clipDataThumbnail
            putExtra(Intent.EXTRA_TITLE, "xkcd: ${listedComic.title}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, null)
        startActivity(context, share, null)
    }

    fun explainComic(context: Context, listedComic: ListedComic) {
        startActivity(
            context,
            Intent(Intent.ACTION_VIEW, Uri.parse("https://explainxkcd.com/${listedComic.id}/")),
            null
        )
    }

    fun comicLink(context: Context, url: String) {
        startActivity(
            context,
            Intent(Intent.ACTION_VIEW, Uri.parse(url)),
            null
        )
    }
}
