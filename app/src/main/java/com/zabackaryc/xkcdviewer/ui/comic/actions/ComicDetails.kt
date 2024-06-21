package com.zabackaryc.xkcdviewer.ui.comic.actions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.ui.comic.ComicViewModel
import com.zabackaryc.xkcdviewer.utils.SettingsItem

enum class ComicDetailsSheetState {
    NORMAL,
    EXPLAIN,
    TRANSCRIPT
}

@Composable
fun ComicDetails(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    viewModel: ComicViewModel,
    snackbarHostState: SnackbarHostState,
    onHideRequested: () -> Unit,
    onFullscreenRequested: () -> Unit,
    onRandomizeRequested: () -> Unit
) {
    val expandActionsByDefault = SettingsItem.ComicActionsExpand.currentValue
    var actionsExpanded by rememberSaveable { mutableStateOf(expandActionsByDefault) }
    var sheetState by rememberSaveable {
        mutableStateOf(ComicDetailsSheetState.NORMAL)
    }
    val context = LocalContext.current
    val explainPreference =
        SettingsItem.ComicExplainXkcdIntegration.Values.entries[SettingsItem.ComicExplainXkcdIntegration.currentValue]

    if (cachedComic == null || listedComic == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        AnimatedContent(
            targetState = sheetState,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),

                        ) { 30 } + fadeIn(
                        animationSpec = tween(
                            delayMillis = 90,
                            durationMillis = 210,
                            easing = LinearOutSlowInEasing
                        )
                    ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearOutSlowInEasing
                                )
                            ) { -30 } + fadeOut(
                        animationSpec = tween(
                            durationMillis = 90,
                            easing = FastOutLinearInEasing
                        )
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ) { -30 } + fadeIn(
                        animationSpec = tween(
                            delayMillis = 90,
                            durationMillis = 210,
                            easing = LinearOutSlowInEasing
                        )
                    ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearOutSlowInEasing
                                )
                            ) { 30 } + fadeOut(
                        animationSpec = tween(
                            durationMillis = 90,
                            easing = FastOutLinearInEasing
                        )
                    )
                }
            },
            label = "Sheet state slide animation",
            // modifier = Modifier.animateContentSize()
        ) { animatedSheetState ->
            when (animatedSheetState) {
                ComicDetailsSheetState.NORMAL -> {
                    Column {
                        Text(
                            text = cachedComic.mouseover, modifier = Modifier.padding(12.dp)
                        )
                        if (cachedComic.newsContent != null) {
                            NewsBubble("Header text news", cachedComic.newsContent)
                        }
                        ComicActions(
                            listedComic = listedComic,
                            cachedComic = cachedComic,
                            viewModel = viewModel,
                            snackbarHostState = snackbarHostState,
                            expanded = actionsExpanded,
                            onExpandChange = {
                                actionsExpanded = it
                            },
                            onHideRequested = onHideRequested,
                            comicActionUiInterface = object : ComicActionUiInterface() {
                                override fun enterFullscreen() {
                                    onFullscreenRequested()
                                }

                                override fun showTranscript() {
                                    sheetState = ComicDetailsSheetState.TRANSCRIPT
                                }

                                override fun showExplain() {
                                    when (explainPreference) {
                                        SettingsItem.ComicExplainXkcdIntegration.Values.FULL -> {
                                            sheetState = ComicDetailsSheetState.EXPLAIN
                                        }

                                        SettingsItem.ComicExplainXkcdIntegration.Values.BROWSER,
                                        SettingsItem.ComicExplainXkcdIntegration.Values.CUSTOM_TABS -> {
                                            viewModel.explainComicInBrowser(
                                                context,
                                                listedComic,
                                                explainPreference == SettingsItem.ComicExplainXkcdIntegration.Values.CUSTOM_TABS
                                            )
                                        }

                                        SettingsItem.ComicExplainXkcdIntegration.Values.DISABLED -> {}
                                    }
                                }

                                override fun randomize() {
                                    onRandomizeRequested()
                                }
                            }
                        )
                    }
                }

                ComicDetailsSheetState.EXPLAIN -> {
                    Column {
                        SheetHeader(
                            name = "explain xkcd",
                            onBackPressed = { sheetState = ComicDetailsSheetState.NORMAL }
                        )
                        Text(
                            text = "not implemented"
                        )
                    }
                }

                ComicDetailsSheetState.TRANSCRIPT -> {
                    Column {
                        SheetHeader(
                            name = "Comic transcript",
                            onBackPressed = { sheetState = ComicDetailsSheetState.NORMAL }
                        )
                        Text(
                            text = cachedComic.transcript
                                ?: "This comic does not have a transcript.",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
