package com.zabackaryc.xkcdviewer.ui.comic.actions

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.ui.comic.ComicViewModel
import com.zabackaryc.xkcdviewer.utils.SettingsItem

val EXPLAIN_XKCD_PAGE_REGEX =
    Regex("https://www\\.explainxkcd\\.com/wiki/index\\.php/(\\d+):_.+")

fun Color.toCSS(): String =
    "rgb(${red * 255}, ${green * 255}, ${blue * 255})"

fun CornerBasedShape.toCSSBorderRadius(shapeSize: Size, density: Density): String =
    "${this.topStart.toPx(shapeSize, density)}px ${
        this.topEnd.toPx(
            shapeSize,
            density
        )
    }px ${this.bottomStart.toPx(shapeSize, density)}px ${this.bottomEnd.toPx(shapeSize, density)}px"

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
    onRandomizeRequested: () -> Unit,
    onNavigateToComic: (Int) -> Unit
) {
    val expandActionsByDefault = SettingsItem.ComicActionsExpand.currentValue
    var actionsExpanded by rememberSaveable { mutableStateOf(expandActionsByDefault) }
    var sheetState by rememberSaveable {
        mutableStateOf(ComicDetailsSheetState.NORMAL)
    }
    val context = LocalContext.current
    val explainPreference =
        SettingsItem.ComicExplainXkcdIntegration.Values.entries[SettingsItem.ComicExplainXkcdIntegration.currentValue]
    var explainContent by remember { mutableStateOf<String?>(null) }

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
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
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
                                            explainContent = null
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
                        val content = explainContent
                        if (content != null) {
                            val colorScheme = MaterialTheme.colorScheme
                            val shapes = MaterialTheme.shapes
                            val typography = MaterialTheme.typography
                            val density = LocalDensity.current
                            val listedComicId = listedComic.id
                            val webViewData =
                                remember(
                                    content,
                                    colorScheme,
                                    shapes,
                                    typography,
                                    density,
                                    listedComicId
                                ) {
                                    """
                                    <style>
                                    :root {
                                        background-color: ${colorScheme.surfaceContainerLow.toCSS()};
                                        color: ${colorScheme.onSurface.toCSS()};
                                        -webkit-tap-highlight-color: transparent;
                                    }
                                    .notice_tpl, #Explanation, .mw-editsection {
                                        display: none;
                                    }
                                    a {
                                        color: ${colorScheme.primary.toCSS()};
                                    }
                                    a:hover, a:focus, a:active {
                                        text-decoration: none;
                                    }
                                    table.wikitable {
                                        background-color: ${colorScheme.surfaceContainerHigh.toCSS()};
                                        color: ${colorScheme.onSurface.toCSS()};
                                        margin: 1em 0;
                                        border: 1px solid ${colorScheme.outline.toCSS()};
                                        border-collapse: collapse;
                                    }
                                    table.wikitable > tr > th,table.wikitable > tr > td,table.wikitable > * > tr > th,table.wikitable > * > tr > td {
                                        border: 1px solid ${colorScheme.outline.toCSS()};
                                        padding: 0.2em 0.4em
                                    }
                                    table.wikitable > tr > th,table.wikitable > * > tr > th {
                                        background-color: ${colorScheme.surfaceContainerHighest.toCSS()};
                                        text-align: center
                                    }
                                    table.wikitable > caption {
                                        font-weight: bold
                                    }
                                    </style>
                                    <base href="https://www.explainxkcd.com/wiki/index.php/$listedComicId" />
                                """.trimIndent() + content
                                }
                            val state = rememberWebViewStateWithHTMLData(data = webViewData)
                            Column(
                                modifier =
                                Modifier
                                    .verticalScroll(rememberScrollState())
                                    .fillMaxWidth()
                            ) {
                                WebView(
                                    state = state,
                                    client = object : AccompanistWebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            Log.d("xkcdviewer", "webview request: ${request?.url}")
                                            if (request != null) {
                                                val match =
                                                    EXPLAIN_XKCD_PAGE_REGEX.matchEntire(request.url.toString())
                                                if (match == null) {
                                                    viewModel.openLinkedURL(
                                                        context,
                                                        request.url.toString()
                                                    )
                                                } else {
                                                    // guaranteed not to throw since regex only
                                                    // matches numbers
                                                    val comicId =
                                                        match.groups[1]!!.value.toInt()
                                                    onHideRequested()
                                                    onNavigateToComic(comicId)
                                                }
                                            }
                                            return true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Text("Content written by explain xkcd contributors is licensed under CC BY-SA 3.0.")
                                FilledTonalButton(
                                    onClick = {
                                        viewModel.explainComicInBrowser(
                                            context,
                                            listedComic,
                                            true
                                        )
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Read more")
                                }
                            }
                        } else {
                            LaunchedEffect(Unit) {
                                explainContent = viewModel.loadExplain(listedComic.id)
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        }
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
                            modifier = Modifier
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}
