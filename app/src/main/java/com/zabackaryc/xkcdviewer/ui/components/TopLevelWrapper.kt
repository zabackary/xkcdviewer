package com.zabackaryc.xkcdviewer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate

@Composable
fun TopLevelWrapper(
    searchPlaceholder: String,
    searchQuery: String,
    onQueryChange: (term: String) -> Unit,
    searchResults: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    extraSearchItems: @Composable (searchActive: Boolean, animationState: Float) -> Unit = { _, _ -> },
    onActiveChange: (active: Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    var searchActive by remember { mutableStateOf(false) }
    val searchButtonAnimationState by animateFloatAsState(
        targetValue = if (searchActive) 1f else 0f,
        label = "Search button animation"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter),
            query = searchQuery,
            onQueryChange = {
                onQueryChange(it)
            },
            onSearch = { },
            active = searchActive,
            onActiveChange = {
                searchActive = it
                onActiveChange(it)
            },
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
                        onClick = {
                            searchActive = false
                            onActiveChange(false)
                        },
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
            placeholder = { Text(searchPlaceholder) },
            trailingIcon = {
                extraSearchItems(searchActive, searchButtonAnimationState)
            }
        ) {
            searchResults()
        }
        content()
    }
}
