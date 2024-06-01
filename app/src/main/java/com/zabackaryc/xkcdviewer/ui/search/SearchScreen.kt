package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zabackaryc.xkcdviewer.data.ComicSort
import com.zabackaryc.xkcdviewer.ui.components.TopLevelWrapper

@Composable
fun SearchScreen(
    onComicSelected: (comicId: Int) -> Unit,
    onTopLevelDestinationSelected: (route: String) -> Unit,
    viewModel: SearchViewModel
) {
    TopLevelWrapper(
        searchPlaceholder = "Search comics",
        searchQuery = viewModel.uiState.activeSearch?.filter ?: "",
        onQueryChange = {
            viewModel.updateActiveSearch(filter = it)
        },
        onExpandedChange = { active ->
            if (active) {
                viewModel.beginSearch()
            } else {
                viewModel.endSearch()
            }
        },
        searchResults = {
            SearchOptions(
                onlyFavorites = viewModel.uiState.activeSearch?.onlyFavorites ?: false,
                onOnlyFavoritesChange = { viewModel.updateActiveSearch(onlyFavorites = it) },
                comicSort = viewModel.uiState.activeSearch?.comicSort ?: ComicSort.Default,
                onComicSortChange = { viewModel.updateActiveSearch(comicSort = it) }
            )
            HorizontalDivider()
            viewModel.uiState.activeSearch.let { activeSearch ->
                LazyColumn {
                    activeSearch?.highlightedResult?.let { item ->
                        item {
                            HighlightedComicListItem(
                                listedComic = item,
                                onFavoriteChange = {
                                    viewModel.setFavoriteComic(item.id, it)
                                },
                                onSelected = {
                                    onComicSelected(item.id)
                                },
                                highlightedReason = "Because the comic ID matches",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    if (activeSearch?.results == null) {
                        item {
                            Text(
                                text = "Recently viewed",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        viewModel.uiState.historySample.let { historySample ->
                            if (historySample == null) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else {
                                items(historySample) { item ->
                                    HistoryEntryListItem(
                                        historyEntryWithListedComic = item,
                                        onFavoriteChange = {
                                            viewModel.setFavoriteComic(item.comicId, it)
                                        },
                                        onSelected = {
                                            onComicSelected(item.comicId)
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        items(activeSearch.results) { item ->
                            ComicListItem(
                                listedComic = item,
                                onFavoriteChange = {
                                    viewModel.setFavoriteComic(item.id, it)
                                },
                                onSelected = {
                                    onComicSelected(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    ) {
        if (!viewModel.uiState.offline) {
            LazyColumn(contentPadding = PaddingValues(top = 86.dp, bottom = 16.dp)) {
                item {
                    Spacer(
                        Modifier.windowInsetsBottomHeight(
                            WindowInsets.systemBars
                        )
                    )
                }
                item {
                    Text(
                        text = "Latest comics",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    ComicCarousel(
                        comics = { viewModel.uiState.latestComicsSample },
                        onComicSelected = onComicSelected,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onBrowseMoreClicked = { /* TODO */ }
                    )
                }
                item {
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    ComicCarousel(
                        comics = { viewModel.uiState.favoriteComicsSample },
                        onComicSelected = onComicSelected,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onBrowseMoreClicked = { /* TODO */ }
                    )
                }
                item {
                    Spacer(
                        Modifier.windowInsetsBottomHeight(
                            WindowInsets.systemBars
                        )
                    )
                }
            }
        } else {
            Box {
                // TODO: Write better error text
                Text("Offline... Sad for you.")
            }
        }
    }
}

