package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    onComicSelected: (comicId: Int) -> Unit,
    viewModel: SearchViewModel
) {
    val appBarState = rememberTopAppBarState()
    var searchQueryState by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                query = searchQueryState,
                onQueryChange = {
                    searchQueryState = it
                    viewModel.setTerm(it)
                },
                onSearch = { },
                active = searchActive,
                onActiveChange = { searchActive = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                placeholder = { Text(text = "Search comics") },
                trailingIcon = {
                    if (searchActive) IconButton(
                        onClick = { sortMenuExpanded = true },
                    ) {
                        Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Newest to oldest") },
                            onClick = {
                                sortMenuExpanded = false
                                viewModel.setSort(SortOrder.NewestToOldest)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier.alpha(if (viewModel.uiState.sortOrder == SortOrder.NewestToOldest) 1f else 0f)
                                )
                            })
                        DropdownMenuItem(
                            text = { Text("Oldest to newest") },
                            onClick = {
                                sortMenuExpanded = false
                                viewModel.setSort(SortOrder.OldestToNewest)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier.alpha(if (viewModel.uiState.sortOrder == SortOrder.OldestToNewest) 1f else 0f)
                                )
                            })
                        DropdownMenuItem(
                            text = { Text("Title") },
                            onClick = {
                                sortMenuExpanded = false
                                viewModel.setSort(SortOrder.Title)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier.alpha(if (viewModel.uiState.sortOrder == SortOrder.Title) 1f else 0f)
                                )
                            })
                    }
                }
            ) {
                viewModel.uiState.filteredList.forEach { item ->
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
                    )
                }

            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (!viewModel.uiState.offline) {
            Column(modifier = Modifier.padding(paddingValues)) {
                Text(
                    text = "Latest comics",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyRow {
                    items(viewModel.uiState.list.reversed()) { item ->
                        ComicCard(
                            listedComic = item, onSelected = {
                                onComicSelected(it)
                            }, modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyRow {
                    items(viewModel.uiState.favoriteComics) { item ->
                        ComicCard(
                            listedComic = item, onSelected = {
                                onComicSelected(it)
                            }, modifier = Modifier
                                .padding(8.dp)
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
