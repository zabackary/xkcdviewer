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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.utils.SettingsItem
import com.zabackaryc.xkcdviewer.utils.htmltext.HtmlText
import kotlinx.coroutines.launch

@Composable
fun ComicDetails(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    viewModel: ComicViewModel,
    snackbarHostState: SnackbarHostState,
    onHideRequested: () -> Unit
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
                onHideRequested = onHideRequested
            )
        }
    }
}

@Composable
fun ComicActions(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    viewModel: ComicViewModel,
    snackbarHostState: SnackbarHostState,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onHideRequested: () -> Unit
) {
    val scrollState = rememberScrollState()
    if (cachedComic != null && listedComic != null) {
        val content = @Composable { renderAsExpanded: Boolean ->
            ComicActionType.allActionTypes.forEach {
                ComicAction(
                    actionType = it,
                    listedComic = listedComic,
                    cachedComic = cachedComic,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    renderAsExpanded = renderAsExpanded,
                    onHideRequested = onHideRequested
                )
            }
        }
        Column {
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
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text("Expand actions") } },
                        state = rememberTooltipState()
                    ) {
                        FilledIconButton(
                            onClick = {
                                onExpandChange(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Show full action list"
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    content(true)
                }
            }
        }
    }
}

@Composable
fun ComicAction(
    actionType: ComicActionType,
    listedComic: ListedComic,
    cachedComic: CachedComic,
    viewModel: ComicViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    renderAsExpanded: Boolean,
    onHideRequested: () -> Unit
) {
    val comicScope = ComicActionType.ComicActionScope(
        listedComic,
        cachedComic,
        viewModel,
        LocalContext.current,
        snackbarHostState
    )

    if (!actionType.show(comicScope)) return

    val icon = @Composable { contentDescription: String? ->
        when (val iconSrc = actionType.icon(comicScope)) {
            is ImageVector -> {
                Icon(
                    imageVector = iconSrc,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp)
                )
            }

            is Int -> {
                Icon(
                    painter = painterResource(id = iconSrc),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp)
                )
            }

            else -> {
                throw UnsupportedOperationException("unsupported icon type")
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val runAction = {
        if (actionType.closeBeforeRun) onHideRequested()
        coroutineScope.launch {
            actionType.action(comicScope)
        }
    }

    if (renderAsExpanded) {
        ListItem(
            leadingContent = {
                if (actionType.showBadge(comicScope)) {
                    BadgedBox(badge = {
                        Badge()
                    }) {
                        icon(null)
                    }
                } else {
                    icon(null)
                }
            },
            headlineContent = { Text(actionType.shortName) },
            supportingContent = actionType.contextualName(comicScope)?.let { { Text(text = it) } },
            modifier = modifier.clickable { runAction() },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    } else {
        TooltipBox(
            tooltip = {
                PlainTooltip {
                    Text(
                        when (val contextualName = actionType.contextualName(comicScope)) {
                            is String -> contextualName
                            else -> actionType.shortName
                        }
                    )
                }
            },
            modifier = modifier,
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            state = rememberTooltipState()
        ) {
            val iconButton = @Composable {
                FilledTonalIconButton(onClick = { runAction() }) {
                    icon(actionType.actionableName)
                }
            }
            if (actionType.showBadge(comicScope)) {
                BadgedBox(badge = {
                    Badge(
                        modifier = Modifier.offset((-12).dp, 12.dp)
                    )
                }) {
                    iconButton()
                }
            } else {
                iconButton()
            }
        }
    }
}

@Composable
fun NewsBubble(title: String, content: String, modifier: Modifier = Modifier) {
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
                    text = title,
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
    NewsBubble(title = "Fake news!", content = "This is fake news!! It's not real!")
}
