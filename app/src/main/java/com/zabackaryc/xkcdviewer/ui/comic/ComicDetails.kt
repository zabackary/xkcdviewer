package com.zabackaryc.xkcdviewer.ui.comic

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zabackaryc.xkcdviewer.R
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.utils.SettingsItem
import com.zabackaryc.xkcdviewer.utils.htmltext.HtmlText
import kotlinx.coroutines.launch

@Composable
fun ComicDetails(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    onShareRequest: suspend (Pair<ListedComic, CachedComic>) -> Unit,
    onTranscriptOpen: suspend (String) -> Unit,
    onLinkOpen: suspend (String) -> Unit,
    onExplainOpen: suspend (ListedComic) -> Unit
) {
    val expandActionsByDefault = SettingsItem.ComicActionsExpand.currentValue
    var actionsExpanded by remember { mutableStateOf(expandActionsByDefault) }

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
        Column {
            Text(
                text = cachedComic.mouseover, modifier = Modifier.padding(12.dp)
            )
            if (cachedComic.newsContent != null) {
                NewsBubble(cachedComic.newsContent)
            }
            ComicActions(
                listedComic = listedComic,
                cachedComic = cachedComic,
                onShareRequest = onShareRequest,
                onTranscriptOpen = onTranscriptOpen,
                onLinkOpen = onLinkOpen,
                onExplainOpen = onExplainOpen,
                expanded = actionsExpanded,
                onExpandChange = {
                    actionsExpanded = it
                }
            )
        }
    }
}

@Composable
fun ComicActions(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    onShareRequest: suspend (Pair<ListedComic, CachedComic>) -> Unit,
    onTranscriptOpen: suspend (String) -> Unit,
    onLinkOpen: suspend (String) -> Unit,
    onExplainOpen: suspend (ListedComic) -> Unit,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    if (cachedComic != null && listedComic != null) {
        val content = @Composable { renderAsExpanded: Boolean ->
            ComicAction(
                shortName = "Share",
                actionableName = "Share comic",
                onClick = {
                    coroutineScope.launch {
                        onShareRequest(listedComic to cachedComic)
                    }
                },
                imageVector = Icons.Default.Share,
                renderAsExpanded = renderAsExpanded
            )
            ComicAction(
                shortName = "Explain",
                actionableName = "Open Explain XKCD explanation",
                onClick = {
                    coroutineScope.launch {
                        onExplainOpen(listedComic)
                    }
                },
                painter = painterResource(R.drawable.explainxkcd_icon),
                renderAsExpanded = renderAsExpanded
            )
            if (cachedComic.link != null) {
                ComicAction(
                    shortName = "Linked URL",
                    actionableName = "Open linked URL in browser",
                    onClick = {
                        coroutineScope.launch {
                            onLinkOpen(cachedComic.link)
                        }
                    },
                    imageVector = Icons.Default.OpenInNew,
                    renderAsExpanded = renderAsExpanded
                )
            }
            if (cachedComic.transcript != null) {
                ComicAction(
                    shortName = "Transcript",
                    actionableName = "View official transcript",
                    onClick = {
                        coroutineScope.launch {
                            onTranscriptOpen(cachedComic.transcript)
                        }
                    },
                    imageVector = Icons.Default.Description,
                    renderAsExpanded = renderAsExpanded
                )
            }
        }
        Column {
            AnimatedVisibility(visible = expanded) {
                Column {
                    content(true)
                }
            }
            AnimatedVisibility(visible = !expanded) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        content(false)
                    }
                    PlainTooltipBox(tooltip = { Text("Expand actions") }) {
                        FilledIconButton(
                            onClick = {
                                onExpandChange(true)
                            },
                            modifier = Modifier.tooltipAnchor()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Show full action list"
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ComicAction(
    shortName: String,
    actionableName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    renderAsExpanded: Boolean = true,
    imageVector: ImageVector? = null,
    painter: Painter? = null,
) {
    if (!((imageVector == null) xor (painter == null))) throw IllegalArgumentException("must pass imageVector or painter, but not both")
    val icon = @Composable { contentDescription: String? ->
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )
        } else if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (renderAsExpanded) {
        ListItem(
            leadingContent = {
                icon(null)
            },
            headlineContent = { Text(shortName) },
            modifier = modifier.clickable {
                onClick()
            }
        )
    } else {
        PlainTooltipBox(tooltip = { Text(shortName) }, modifier = modifier) {
            FilledTonalIconButton(
                onClick = {
                    onClick()
                },
                modifier = Modifier.tooltipAnchor()
            ) {
                icon(actionableName)
            }
        }
    }
}

@Composable
fun NewsBubble(content: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ProvideTextStyle(value = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                Text(
                    text = "Header text news",
                    style = MaterialTheme.typography.labelSmall
                )
                val context = LocalContext.current
                HtmlText(
                    text = content,
                    linkClicked = {
                        ContextCompat.startActivity(
                            context,
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(it)
                            ),
                            null
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun NewsBubblePreview() {
    NewsBubble(content = "This is fake news!! It's not real!")
}

@Preview
@Composable
fun ComicDetailsPreview() {
    ComicDetails(listedComic = ListedComic(
        id = 0, title = "Title", date = "10-01-2008", favorite = true, note = "This is a note"
    ), cachedComic = CachedComic(
        id = 0,
        imgUrl = "https://imgs.xkcd.com/comics/siphon.png",
        mouseover = "Mouseover text lives here",
        transcript = "Example transcript.",
        dynamicHtml = null,
        link = null,
        newsContent = null
    ), onShareRequest = {}, onExplainOpen = {}, onTranscriptOpen = {}, onLinkOpen = {})
}

@Preview
@Composable
fun ComicDetailsLoadingPreview() {
    ComicDetails(listedComic = null,
        cachedComic = null,
        onShareRequest = {},
        onExplainOpen = {},
        onTranscriptOpen = {}, onLinkOpen = {})
}
