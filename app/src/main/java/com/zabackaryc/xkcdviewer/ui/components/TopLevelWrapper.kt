package com.zabackaryc.xkcdviewer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
    searchResults: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    extraSearchItems: @Composable (searchActive: Boolean, animationState: Float) -> Unit = { _, _ -> },
    onExpandedChange: (active: Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val searchButtonAnimationState by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        label = "Search button animation"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = {
                        onQueryChange(it)
                    },
                    onSearch = { },
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = it
                        onExpandedChange(it)
                    },
                    enabled = true,
                    placeholder = { Text(searchPlaceholder) },
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
                                    expanded = false
                                    onExpandedChange(false)
                                },
                                modifier = Modifier
                                    .alpha(searchButtonAnimationState)
                                    .rotate((searchButtonAnimationState - 1f) * 180f)
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }

                        }
                    },
                    trailingIcon = {
                        extraSearchItems(expanded, searchButtonAnimationState)
                    },
                    colors = SearchBarDefaults.inputFieldColors(),
                    interactionSource = null,
                )
            },
            expanded = expanded,
            onExpandedChange = {
                expanded = it
                onExpandedChange(it)
            },
            modifier = Modifier
                .align(Alignment.TopCenter),
            shape = SearchBarDefaults.inputFieldShape,
            colors = SearchBarDefaults.colors(),
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = searchResults,
        )
        content()
    }
}
