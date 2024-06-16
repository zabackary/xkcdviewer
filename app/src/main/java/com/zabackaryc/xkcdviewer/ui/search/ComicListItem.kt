package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.zabackaryc.xkcdviewer.data.ComicDao
import com.zabackaryc.xkcdviewer.data.ListedComic
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ComicListItem(
    listedComic: ListedComic,
    onFavoriteChange: (Boolean) -> Unit,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    AbstractComicListItem(
        title = listedComic.title,
        description = null,
        overline = listedComic.date,
        id = listedComic.id,
        favorite = listedComic.favorite,
        onFavoriteChange = onFavoriteChange,
        onSelected = onSelected,
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun HighlightedComicListItem(
    listedComic: ListedComic,
    onFavoriteChange: (Boolean) -> Unit,
    onSelected: () -> Unit,
    highlightedReason: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        AbstractComicListItem(
            title = listedComic.title,
            description = "Posted on ${listedComic.date}",
            overline = highlightedReason,
            id = listedComic.id,
            favorite = listedComic.favorite,
            onFavoriteChange = onFavoriteChange,
            onSelected = onSelected,
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                supportingColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                overlineColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Composable
fun HistoryEntryListItem(
    historyEntryWithListedComic: ComicDao.HistoryEntryWithListedComic,
    onFavoriteChange: (Boolean) -> Unit,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedDateTime = remember(historyEntryWithListedComic.dateTime) {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(ConfigurationCompat.getLocales(context.resources.configuration)[0])
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(historyEntryWithListedComic.dateTime))
    }
    AbstractComicListItem(
        title = historyEntryWithListedComic.title,
        description = "Viewed at $formattedDateTime",
        overline = historyEntryWithListedComic.date,
        id = historyEntryWithListedComic.comicId,
        favorite = historyEntryWithListedComic.favorite,
        onFavoriteChange = onFavoriteChange,
        onSelected = onSelected,
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun AbstractComicListItem(
    title: String,
    overline: String,
    description: String?,
    id: Int,
    favorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
) {
    ListItem(
        overlineContent = { Text(overline) },
        headlineContent = { Text(title) },
        supportingContent = description?.let {
            {
                Text(text = it)
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    "$id",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        trailingContent = {
            IconToggleButton(
                checked = favorite,
                onCheckedChange = {
                    onFavoriteChange(
                        it
                    )
                }) {
                if (favorite) {
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
        },
        modifier = modifier
            .clickable {
                onSelected()
            },
        colors = colors
    )
}
