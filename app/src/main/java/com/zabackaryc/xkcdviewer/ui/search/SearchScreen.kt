package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    onComicSelected: (comicId: Int) -> Unit,
    viewModel: SearchViewModel
) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("All comics") },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (!viewModel.uiState.offline) {
            Column(modifier = Modifier.padding(paddingValues)) {
                SearchOptions(
                    modifier = Modifier.padding(8.dp),
                    searchTerm = viewModel.uiState.term,
                    onSearchTermChange = { viewModel.setTerm(it) },
                    filteringFavorites = viewModel.uiState.filteringFavorites,
                    onFilteringFavoritesChange = { viewModel.setFilteringFavorites(it) },
                    sortOrder = viewModel.uiState.sortOrder,
                    onSortOrderChange = { viewModel.setSort(it) }
                )
                LazyColumn {
                    items(
                        items = viewModel.uiState.filteredList,
                        key = { it.id }
                    ) { item ->
                        ListItem(
                            overlineContent = { Text(item.date) },
                            headlineContent = { Text(item.title) },
                            trailingContent = {
                                IconToggleButton(
                                    checked = item.favorite,
                                    onCheckedChange = {
                                        viewModel.setFavoriteComic(
                                            item.id,
                                            it
                                        )
                                    }) {
                                    if (item.favorite) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Favorited"
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = "Click to favorite"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .clickable {
                                    onComicSelected(item.id)
                                }
                                .animateItemPlacement()
                        )
                    }
                }
            }

        } else {
            Box(modifier = Modifier.padding(paddingValues)) {
                // TODO: Write better error text
                Text("Offline... Sad for you.")
            }
        }
    }
}
