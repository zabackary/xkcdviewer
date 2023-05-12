package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun ComicScreen(onNavigationUp: () -> Unit, viewModel: ComicViewModel) {
    viewModel.uiState.totalComics?.let { totalComics ->
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val comicDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var comicDetailsOpen by remember { mutableStateOf(false) }
        var comicDetailsInitialPage by remember { mutableStateOf(0) }
        var comicDetailsCurrentComicId by remember { mutableStateOf<Int?>(null) }
        val comicDetailsCurrentComic =
            remember(comicDetailsCurrentComicId, viewModel.uiState.data) {
                viewModel.uiState.data[comicDetailsCurrentComicId]
            }

        val historyTransitionState = remember { MutableTransitionState(false) }

        val pagerState = rememberPagerState(initialPage = viewModel.uiState.currentComicId - 1)
        var scrollEnabled by remember { mutableStateOf(true) }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                viewModel.setCurrentComicId(page + 1)
            }
        }

        val (listedComic) = viewModel.uiState.data[pagerState.currentPage + 1]
            ?: (null to null)

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = listedComic?.title ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (listedComic != null) "#${listedComic.id} · ${listedComic.date}" else "",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigationUp) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconToggleButton(
                            checked = listedComic?.favorite ?: false,
                            onCheckedChange = {
                                viewModel.setFavoriteComic(
                                    pagerState.currentPage + 1,
                                    it
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (it && listedComic != null) "Added '${listedComic.title}' to favorites"
                                        else if (listedComic != null) "Removed '${listedComic.title}' from favorites"
                                        else ""
                                    )
                                }
                            }) {
                            if (listedComic?.favorite == true) {
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
                        ComicOverflowMenu(
                            onDetailsClick = {
                                comicDetailsOpen = true
                                comicDetailsCurrentComicId = pagerState.currentPage + 1
                                comicDetailsInitialPage = 0
                            },
                            onHistoryClick = { historyTransitionState.targetState = true }
                        )
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(
                            page = (1..totalComics).random(),
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Random comic"
                    )
                }
            }
        ) { paddingValues ->
            HorizontalPager(
                count = totalComics,
                modifier = Modifier.padding(paddingValues),
                state = pagerState,
                userScrollEnabled = scrollEnabled,
            ) { page ->
                LaunchedEffect(Unit) {
                    viewModel.loadComic(page + 1)
                }
                ComicItem(
                    cachedComic = viewModel.uiState.data[page + 1]?.second,
                    setScrollEnabled = { scrollEnabled = it },
                    showComicDetails = {
                        comicDetailsOpen = true
                        comicDetailsCurrentComicId = page + 1
                        comicDetailsInitialPage = 1
                    },
                )
            }
        }

        History(
            transitionState = historyTransitionState,
            onDismissRequest = {
                historyTransitionState.targetState = false
            },
            onComicClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(it - 1)
                }
            },
            historyEntries = viewModel.uiState.historyEntries,
            onDeleteHistory = {
                viewModel.deleteHistory()
            }
        )

        if (comicDetailsOpen) {
            ModalBottomSheet(
                onDismissRequest = { comicDetailsOpen = false },
                sheetState = comicDetailsSheetState
            ) {
                ComicDetails(
                    listedComic = comicDetailsCurrentComic?.first,
                    cachedComic = comicDetailsCurrentComic?.second,
                    initialPage = comicDetailsInitialPage
                )
            }
        }
    }
}
