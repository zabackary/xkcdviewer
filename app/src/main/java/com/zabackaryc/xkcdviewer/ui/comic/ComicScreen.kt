package com.zabackaryc.xkcdviewer.ui.comic

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zabackaryc.xkcdviewer.ui.comic.actions.ComicDetails
import com.zabackaryc.xkcdviewer.utils.SettingsItem
import com.zabackaryc.xkcdviewer.utils.getActivity
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ComicScreen(onNavigationUp: () -> Unit, viewModel: ComicViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val collapsedToolbarCoroutineScope = rememberCoroutineScope()

    val comicDetailsSheetState = rememberModalBottomSheetState()
    var comicDetailsOpen by remember { mutableStateOf(false) }
    var comicDetailsCurrentComicId by remember { mutableStateOf<Int?>(null) }
    val comicDetailsCurrentComic =
        remember(comicDetailsCurrentComicId, viewModel.uiState.data) {
            viewModel.uiState.data[comicDetailsCurrentComicId]
        }

    var comicTranscript by remember { mutableStateOf<String?>(null) }

    var interactiveMode by rememberSaveable { mutableStateOf(false) }
    var toolbarIsCollapsed by rememberSaveable { mutableStateOf(false) }
    BackHandler(interactiveMode || toolbarIsCollapsed) {
        if (toolbarIsCollapsed) {
            toolbarIsCollapsed = false
            collapsedToolbarCoroutineScope.coroutineContext.cancelChildren()
        } else if (interactiveMode) interactiveMode = false
    }

    val pagerState =
        rememberPagerState(initialPage = viewModel.uiState.currentComicId - 1, pageCount = {
            viewModel.uiState.totalComics ?: Int.MAX_VALUE
        })
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.setCurrentComicId(page + 1)
        }
    }

    val context = LocalContext.current

    val (listedComic, cachedComic) = viewModel.uiState.data[pagerState.currentPage + 1]
        ?: (null to null)

    val randomize = suspend {
        viewModel.uiState.totalComics?.let {
            pagerState.scrollToPage(
                page = (1..it).random(),
            )
        }
    }

    val userWantsMetadataPopup = SettingsItem.ComicMetadataPopup.currentValue
    val hasMetadata = cachedComic?.link != null || cachedComic?.newsContent != null
    val isInteractive = cachedComic?.dynamicHtml != null
    LaunchedEffect(isInteractive, hasMetadata, listedComic?.id) {
        if (isInteractive) {
            if (snackbarHostState.showSnackbar(
                    message = "This comic is interactive",
                    actionLabel = "Enter interactive mode",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                ) == SnackbarResult.ActionPerformed
            ) {
                interactiveMode = true
            }
        } else if (hasMetadata && userWantsMetadataPopup && cachedComic != null) {
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
            AnimatedVisibility(
                visible = !toolbarIsCollapsed,
                enter = expandIn { IntSize(it.width, 0) },
                exit = shrinkOut { IntSize(it.width, 0) }
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = listedComic?.title ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            AnimatedVisibility(visible = !interactiveMode) {
                                Text(
                                    text = if (listedComic != null) "#${listedComic.id} · ${listedComic.date}" else "",
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        Crossfade(
                            targetState = interactiveMode,
                            label = "interactive mode crossfade"
                        ) {
                            if (it) {
                                TooltipBox(
                                    tooltip = {
                                        PlainTooltip { Text("Exit interactive mode") }
                                    },
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    state = rememberTooltipState()
                                ) {
                                    IconButton(
                                        onClick = { interactiveMode = false }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Exit interactive mode"
                                        )
                                    }
                                }
                            } else {
                                TooltipBox(
                                    tooltip = {
                                        PlainTooltip { Text("Back") }
                                    },
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    state = rememberTooltipState()
                                ) {
                                    IconButton(
                                        onClick = onNavigationUp
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = interactiveMode,
                            enter = scaleIn(),
                            exit = scaleOut()
                        ) {
                            TooltipBox(
                                tooltip = {
                                    PlainTooltip {
                                        Text("Hide toolbar")
                                    }
                                },
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                state = rememberTooltipState()
                            ) {
                                IconButton(
                                    onClick = {
                                        collapsedToolbarCoroutineScope.ensureActive()
                                        collapsedToolbarCoroutineScope.launch {
                                            toolbarIsCollapsed = true
                                            snackbarHostState.showSnackbar(
                                                message = "Touch back to show to toolbar and exit fullscreen.",
                                                withDismissAction = true,
                                                duration = SnackbarDuration.Indefinite
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Hide toolbar"
                                    )
                                }
                            }
                        }
                        TooltipBox(
                            tooltip = {
                                PlainTooltip {
                                    Text(if (hasMetadata) "More options. This comic has extra metadata" else "More options")
                                }
                            },
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            state = rememberTooltipState()
                        ) {
                            BadgedBox(badge = {
                                if (hasMetadata) Badge(
                                    modifier = Modifier.offset((-12).dp, 12.dp)
                                )
                            }) {
                                IconButton(
                                    onClick = {
                                        comicDetailsOpen = true
                                        comicDetailsCurrentComicId = listedComic?.id
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = if (hasMetadata) "More options. This comic has extra metadata" else "More options"
                                    )
                                }
                            }
                        }
                    },
                    colors = if (interactiveMode) TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) else TopAppBarDefaults.topAppBarColors()
                )
            }
        },
        floatingActionButton = {
            val shouldShowLabel = (context.getActivity()
                ?.let { calculateWindowSizeClass(activity = it).widthSizeClass }) != WindowWidthSizeClass.Compact
            AnimatedVisibility(
                visible = !interactiveMode && !toolbarIsCollapsed,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            randomize()
                        }
                    },
                    text = { Text(text = "Randomize") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Shuffle, contentDescription = "Random comic"
                        )
                    },
                    expanded = shouldShowLabel
                )
            }
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
                    modifier = Modifier.padding(paddingValues),
                    beyondViewportPageCount = 1,
                    userScrollEnabled = !interactiveMode,
                    pageSpacing = 8.dp,
                    state = pagerState,
                ) { page ->
                    val comicId = page + 1
                    LaunchedEffect(Unit) {
                        viewModel.loadComic(comicId)
                    }
                    ComicItem(
                        id = comicId,
                        cachedComic = viewModel.uiState.data[comicId]?.second,
                        showComicDetails = {
                            comicDetailsOpen = true
                            comicDetailsCurrentComicId = page + 1
                        },
                        interactiveMode = interactiveMode,
                        enableInteractiveMode = {
                            interactiveMode = true
                        }
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
                ComicDetails(
                    listedComic = comicDetailsCurrentComic?.first,
                    cachedComic = comicDetailsCurrentComic?.second,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    onHideRequested = {
                        coroutineScope.launch {
                            comicDetailsSheetState.hide()
                            comicDetailsOpen = false
                        }
                    },
                    onFullscreenRequested = {
                        toolbarIsCollapsed = true
                    },
                    onRandomizeRequested = {
                        coroutineScope.launch {
                            randomize()
                        }
                    }
                )
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

