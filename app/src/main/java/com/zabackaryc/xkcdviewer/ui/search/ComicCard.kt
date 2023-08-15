package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.zabackaryc.xkcdviewer.data.ListedComic

fun defaultImageURL(listedComic: ListedComic) =
    "https://imgs.xkcd.com/comics/${
        listedComic.title.lowercase().replace(Regex("[- (]"), "_").replace(Regex("[^a-z_]"), "")
    }.png"

@Composable
fun ComicCard(listedComic: ListedComic, onSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = { onSelected(listedComic.id) },
        modifier = modifier
            .width(280.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            SubcomposeAsyncImage(
                model = defaultImageURL(listedComic),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (this@SubcomposeAsyncImage.painter.state) {
                        AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
                        is AsyncImagePainter.State.Error -> Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Failed to load preview",
                            modifier = Modifier.size(96.dp)
                        )

                        is AsyncImagePainter.State.Success -> this@SubcomposeAsyncImage.SubcomposeAsyncImageContent()
                    }
                }
            }
        }
        Row(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier
                    .padding(PaddingValues(end = 8.dp))
                    .weight(1f)
            ) {
                Text(
                    text = listedComic.title,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(text = listedComic.date)
            }
            if (listedComic.favorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite comic",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
