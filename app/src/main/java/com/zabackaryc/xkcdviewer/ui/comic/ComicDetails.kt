package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zabackaryc.xkcdviewer.R
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import kotlinx.coroutines.launch

@Composable
fun ComicDetails(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    onShareRequest: suspend (Pair<ListedComic, CachedComic>) -> Unit,
    onTranscriptOpen: suspend (String) -> Unit,
    onExplainOpen: suspend (ListedComic) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    if (cachedComic == null || listedComic == null) {
        // TODO: Center align this
        CircularProgressIndicator()
    } else {
        Column {
            Text(
                text = cachedComic.mouseover, modifier = Modifier.padding(12.dp)
            )
            ListItem(leadingContent = {
                Icon(
                    imageVector = Icons.Default.Share, contentDescription = null
                )
            }, headlineContent = { Text(text = "Share") }, modifier = Modifier.clickable {
                coroutineScope.launch {
                    onShareRequest(listedComic to cachedComic)
                }
            })
            ListItem(leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.explainxkcd_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }, headlineContent = { Text(text = "Explain") }, modifier = Modifier.clickable {
                coroutineScope.launch {
                    onExplainOpen(listedComic)
                }
            })
            if (cachedComic.transcript != null && cachedComic.transcript != "") {
                ListItem(leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Description, contentDescription = null
                    )
                }, headlineContent = { Text(text = "Transcript") }, modifier = Modifier.clickable {
                    coroutineScope.launch {
                        onTranscriptOpen(cachedComic.transcript)
                    }
                })
            }
        }
    }
}

@Preview
@Composable
fun ComicDetailsPreview() {
    ComicDetails(listedComic = ListedComic(
        id = 0, title = "Title", date = "10-01-2008", favorite = true, note = "This is a note"
    ), cachedComic = CachedComic(
        id = 0,
        imgUrl = "https://imgs.xkcd.com/comics/siphon.png",
        mouseover = "Mouseover",
        transcript = "Someone walks into a bar. THe end.",
        dynamicHtml = null,
    ), onShareRequest = {}, onExplainOpen = {}, onTranscriptOpen = {})
}

@Preview
@Composable
fun ComicDetailsLoadingPreview() {
    ComicDetails(listedComic = null,
        cachedComic = null,
        onShareRequest = {},
        onExplainOpen = {},
        onTranscriptOpen = {})
}
