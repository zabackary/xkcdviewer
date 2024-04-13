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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onSelected: () -> Unit
) {
    AbstractComicListItem(
        title = listedComic.title,
        description = null,
        date = listedComic.date,
        id = listedComic.id,
        favorite = listedComic.favorite,
        onFavoriteChange = onFavoriteChange,
        onSelected = onSelected,
    )
}

@Composable
fun HistoryEntryListItem(
    historyEntryWithListedComic: ComicDao.HistoryEntryWithListedComic,
    onFavoriteChange: (Boolean) -> Unit,
    onSelected: () -> Unit
) {
    val context = LocalContext.current
    val formattedDateTime = remember {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(ConfigurationCompat.getLocales(context.resources.configuration)[0])
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(historyEntryWithListedComic.dateTime))
    }
    AbstractComicListItem(
        title = historyEntryWithListedComic.title,
        description = "Viewed at $formattedDateTime",
        date = historyEntryWithListedComic.date,
        id = historyEntryWithListedComic.comicId,
        favorite = historyEntryWithListedComic.favorite,
        onFavoriteChange = onFavoriteChange,
        onSelected = onSelected,
    )
}

@Composable
fun AbstractComicListItem(
    title: String,
    date: String,
    description: String?,
    id: Int,
    favorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onSelected: () -> Unit
) {
    ListItem(
        overlineContent = { Text(date) },
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
                    modifier = Modifier.align(Alignment.Center)
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
        modifier = Modifier
            .clickable {
                onSelected()
            }
    )
}
