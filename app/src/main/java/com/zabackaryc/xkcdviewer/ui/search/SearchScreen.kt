package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(
    onComicSelected: (comicId: Int) -> Unit,
    viewModel: SearchViewModel
) {
    var searchQueryState by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val searchButtonAnimationState by animateFloatAsState(
        targetValue = if (searchActive) 1f else 0f,
        label = "Search button animation"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter),
            query = searchQueryState,
            onQueryChange = {
                searchQueryState = it
                viewModel.setTerm(it)
            },
            onSearch = { },
            active = searchActive,
            onActiveChange = { searchActive = it },
            leadingIcon = {
                Box {
                    if (searchButtonAnimationState != 1f) Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(1f - searchButtonAnimationState)
                            .rotate(searchButtonAnimationState * 180f)
                            .align(Alignment.Center)
                    )
                    if (searchButtonAnimationState != 0f) IconButton(
                        onClick = { searchActive = false },
                        modifier = Modifier
                            .alpha(searchButtonAnimationState)
                            .rotate((searchButtonAnimationState - 1f) * 180f)
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                }
            },
            placeholder = { Text(text = "Search comics") },
            trailingIcon = {
                if (searchButtonAnimationState != 0f) IconButton(
                    onClick = { sortMenuExpanded = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.scale(searchButtonAnimationState)
                    )
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
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text("${item.id}", modifier = Modifier.align(Alignment.Center))
                        }
                    },
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
                }
                item {
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
