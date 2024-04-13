package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.zabackaryc.xkcdviewer.utils.SettingsItem
import com.zabackaryc.xkcdviewer.utils.getActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ComicScreen(onNavigationUp: () -> Unit, viewModel: ComicViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val comicDetailsSheetState = rememberModalBottomSheetState()
    var comicDetailsOpen by remember { mutableStateOf(false) }
    var comicDetailsCurrentComicId by remember { mutableStateOf<Int?>(null) }
    val comicDetailsCurrentComic =
        remember(comicDetailsCurrentComicId, viewModel.uiState.data) {
            viewModel.uiState.data[comicDetailsCurrentComicId]
        }

    var comicTranscript by remember { mutableStateOf<String?>(null) }

    val pagerState = rememberPagerState(initialPage = viewModel.uiState.currentComicId - 1)
    var scrollEnabled by remember { mutableStateOf(true) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.setCurrentComicId(page + 1)
        }
    }

    val context = LocalContext.current

    val (listedComic, cachedComic) = viewModel.uiState.data[pagerState.currentPage + 1]
        ?: (null to null)

    val userWantsMetadataPopup = SettingsItem.ComicMetadataPopup.currentValue
    val hasMetadata = cachedComic?.link != null || cachedComic?.newsContent != null
    LaunchedEffect(hasMetadata, listedComic?.id) {
        if (hasMetadata && userWantsMetadataPopup && cachedComic != null) {
            if (snackbarHostState.showSnackbar(
                    message = when ((cachedComic.link != null) to (cachedComic.newsContent != null)) {
                        true to false -> "This comic is a clickable link."
                        false to true -> "This comic has a special header text."
                        true to true -> "This comic is clickable and has news attached."
                        else -> ""
                    },
                    actionLabel = "Show",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                ) == SnackbarResult.ActionPerformed
            ) {
                comicDetailsCurrentComicId = cachedComic.id
                comicDetailsOpen = true
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = listedComic?.title ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = if (listedComic != null) "#${listedComic.id} · ${listedComic.date}" else "",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    PlainTooltipBox(
                        tooltip = {
                            Text("Back")
                        }
                    ) {
                        IconButton(
                            onClick = onNavigationUp,
                            modifier = Modifier.tooltipAnchor()
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    PlainTooltipBox(tooltip = {
                        Text(if (listedComic?.favorite == true) "Remove favorite" else "Favorite comic")
                    }) {
                        IconToggleButton(
                            checked = listedComic?.favorite ?: false,
                            onCheckedChange = {
                                viewModel.setFavoriteComic(
                                    pagerState.currentPage + 1, it
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (it && listedComic != null) "Added '${listedComic.title}' to favorites"
                                        else if (listedComic != null) "Removed '${listedComic.title}' from favorites"
                                        else ""
                                    )
                                }
                            },
                            modifier = Modifier.tooltipAnchor()
                        ) {
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
                    }
                    if (hasMetadata) {
                        PlainTooltipBox(
                            tooltip = {
                                Text("More options. This comic has extra metadata")
                            }
                        ) {
                            BadgedBox(badge = {
                                Badge(
                                    modifier = Modifier.offset((-12).dp, 12.dp)
                                )
                            }) {
                                IconButton(
                                    onClick = {
                                        comicDetailsOpen = true
                                        comicDetailsCurrentComicId = listedComic?.id
                                    },
                                    modifier = Modifier.tooltipAnchor()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More options (comic has extra metadata)"
                                    )

                                }
                            }
                        }
                    } else {
                        PlainTooltipBox(
                            tooltip = {
                                Text("More options")
                            }
                        ) {
                            IconButton(
                                onClick = {
                                    comicDetailsOpen = true
                                    comicDetailsCurrentComicId = listedComic?.id
                                },
                                modifier = Modifier.tooltipAnchor()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                        }
                    }

                }
            )
        },
        floatingActionButton = {
            val shouldShowLabel = (context.getActivity()
                ?.let { calculateWindowSizeClass(activity = it).widthSizeClass }) != WindowWidthSizeClass.Compact
            ExtendedFloatingActionButton(onClick = {
                coroutineScope.launch {
                    viewModel.uiState.totalComics?.let {
                        pagerState.scrollToPage(
                            page = (1..it).random(),
                        )
                    }
                }
            }, text = { Text(text = "Randomize") }, icon = {
                Icon(
                    imageVector = Icons.Default.Shuffle, contentDescription = "Random comic"
                )
            }, expanded = shouldShowLabel)
        }
    ) { paddingValues ->
        viewModel.uiState.totalComics.let { totalComics ->
            if (totalComics == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
                        },
                    )
                }
            }
        }
    }

    if (comicDetailsOpen) {
        ModalBottomSheet(
            onDismissRequest = { comicDetailsOpen = false }, sheetState = comicDetailsSheetState
        ) {
            val modalScrollState = rememberScrollState()
            Box(Modifier.verticalScroll(modalScrollState)) {
                ComicDetails(listedComic = comicDetailsCurrentComic?.first,
                    cachedComic = comicDetailsCurrentComic?.second,
                    onShareRequest = {
                        viewModel.shareComic(context, it.first, it.second)
                        comicDetailsSheetState.hide()
                        comicDetailsOpen = false
                    },
                    onTranscriptOpen = {
                        comicTranscript = it
                        comicDetailsSheetState.hide()
                        comicDetailsOpen = false
                    },
                    onExplainOpen = {
                        viewModel.explainComic(context, it)
                        comicDetailsSheetState.hide()
                        comicDetailsOpen = false
                    },
                    onLinkOpen = {
                        viewModel.comicLink(context, it)
                        comicDetailsSheetState.hide()
                        comicDetailsOpen = false
                    })
            }
        }
    }

    comicTranscript?.let { transcript ->
        Dialog(
            onDismissRequest = { comicTranscript = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text("Transcript") },
                    navigationIcon = {
                        IconButton(onClick = { comicTranscript = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                )
            }) {
                val transcriptScrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(transcriptScrollState)
                ) {
                    Text(
                        text = transcript,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

        }
    }
}

