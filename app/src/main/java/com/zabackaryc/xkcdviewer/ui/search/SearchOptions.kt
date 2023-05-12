package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SearchOptions(
    searchTerm: String,
    onSearchTermChange: (String) -> Unit,
    filteringFavorites: Boolean,
    onFilteringFavoritesChange: (Boolean) -> Unit,
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = searchTerm,
            onValueChange = { onSearchTermChange(it) },
            modifier = Modifier.weight(1f),
            label = { Text("Search") }
        )
        IconToggleButton(
            checked = filteringFavorites,
            onCheckedChange = { onFilteringFavoritesChange(it) }) {
            if (filteringFavorites) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Showing favorites"
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Showing all"
                )
            }
        }
        Box {
            IconButton(
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
                        onSortOrderChange(SortOrder.NewestToOldest)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.alpha(if (sortOrder == SortOrder.NewestToOldest) 1f else 0f)
                        )
                    })
                DropdownMenuItem(
                    text = { Text("Oldest to newest") },
                    onClick = {
                        sortMenuExpanded = false
                        onSortOrderChange(SortOrder.OldestToNewest)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.alpha(if (sortOrder == SortOrder.OldestToNewest) 1f else 0f)
                        )
                    })
                DropdownMenuItem(
                    text = { Text("Title") },
                    onClick = {
                        sortMenuExpanded = false
                        onSortOrderChange(SortOrder.Title)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.alpha(if (sortOrder == SortOrder.Title) 1f else 0f)
                        )
                    })
            }
        }
    }
}

@Preview
@Composable
fun SearchOptionsPreview() {
    SearchOptions(
        searchTerm = "",
        onSearchTermChange = {},
        filteringFavorites = false,
        onFilteringFavoritesChange = {},
        sortOrder = SortOrder.NewestToOldest,
        onSortOrderChange = {}
    )
}
