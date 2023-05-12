package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zabackaryc.xkcdviewer.data.HistoryEntry
import com.zabackaryc.xkcdviewer.data.ListedComic
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.material3.ListItem

@Composable
fun History(
    transitionState: MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
    historyEntries: Map<HistoryEntry, ListedComic>,
    onComicClick: (Int) -> Unit,
    onDeleteHistory: () -> Unit
) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    val history = remember(historyEntries) {
        historyEntries.toList().sortedBy { it.first.dateTime }
    }
    var overflowMenuOpen by remember { mutableStateOf(false) }
    var confirmOpen by remember { mutableStateOf(false) }

    if (transitionState.currentState || transitionState.targetState) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismissRequest
        ) {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = slideInVertically(
                    initialOffsetY = { it },
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = {
                            LargeTopAppBar(
                                title = { Text("History") },
                                scrollBehavior = scrollBehavior,
                                navigationIcon = {
                                    IconButton(onClick = onDismissRequest) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Navigate up"
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { overflowMenuOpen = true }) {
                                        Icon(
                                            imageVector = Icons.Default.More,
                                            contentDescription = "Show overflow menu"
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = overflowMenuOpen,
                                        onDismissRequest = { overflowMenuOpen = false }) {
                                        DropdownMenuItem(
                                            text = { Text("Clear history") },
                                            onClick = { confirmOpen = true })
                                    }
                                }
                            )
                        },
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) { paddingValues ->
                        LazyColumn(modifier = Modifier.padding(paddingValues)) {
                            items(history) { item ->
                                HistoryItem(item, onComicClick = {
                                    transitionState.targetState = false
                                    onComicClick(it)
                                })
                            }
                        }
                    }
                }
            }
        }
    }


    if (confirmOpen) {
        AlertDialog(
            onDismissRequest = { confirmOpen = false },
            title = { Text("Clear history?") },
            text = { Text("You cannot undo deleting your comic viewing history.") },
            confirmButton = {
                TextButton(onClick = { onDeleteHistory() }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmOpen = false }) {
                    Text("Dismiss")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
fun HistoryItem(item: Pair<HistoryEntry, ListedComic>, onComicClick: (Int) -> Unit) {
    val dateString =
        remember(item.first.dateTime) {
            DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(ZoneId.from(ZoneOffset.UTC))
                .format(Instant.ofEpochMilli(item.first.dateTime))
        }

    ListItem(
        headlineContent = { Text(item.second.title) },
        supportingContent = { Text(dateString) },
        modifier = Modifier.clickable { onComicClick(item.second.id) }
    )
}
