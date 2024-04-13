package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zabackaryc.xkcdviewer.data.ComicSort

@Composable
fun SearchOptions(
    onlyFavorites: Boolean,
    onOnlyFavoritesChange: (Boolean) -> Unit,
    comicSort: ComicSort,
    onComicSortChange: (ComicSort) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val sortTextMap = remember {
        sortedMapOf(
            ComicSort.DateNewest to "Newest to oldest",
            ComicSort.DateOldest to "Oldest to newest",
            ComicSort.TitleAZ to "Title, A-Z",
            ComicSort.TitleZA to "Title, Z-A"
        )
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        item {
            FilterChip(
                leadingIcon = {
                    Crossfade(onlyFavorites, label = "Favorite selector icon") {
                        when (it) {
                            true -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )

                            false -> Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                selected = onlyFavorites,
                onClick = {
                    onOnlyFavoritesChange(!onlyFavorites)
                },
                label = {
                    Text(text = "Only favorites")
                }
            )
        }
        item {
            Box {
                InputChip(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
                    },
                    selected = comicSort != ComicSort.Default,
                    onClick = { sortMenuExpanded = true },
                    label = {
                        Text(
                            text = sortTextMap[comicSort] ?: "Sort"
                        )
                    }
                )
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    sortTextMap.entries.forEach { sortText ->
                        if (sortText.key != ComicSort.Default)
                            DropdownMenuItem(
                                text = { Text(text = sortText.value) },
                                onClick = {
                                    sortMenuExpanded = false
                                    onComicSortChange(
                                        if (comicSort == sortText.key) {
                                            ComicSort.Default
                                        } else {
                                            sortText.key
                                        }
                                    )
                                },
                                leadingIcon = {
                                    if (comicSort == sortText.key) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected"
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.width(24.dp))
                                    }
                                },
                                modifier = Modifier.background(
                                    if (comicSort == sortText.key) MaterialTheme.colorScheme.secondaryContainer
                                    else Color.Transparent
                                )
                            )
                    }
                }
            }
        }
    }
}
