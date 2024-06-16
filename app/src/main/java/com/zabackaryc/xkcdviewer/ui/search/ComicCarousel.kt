package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.CarouselItemInfo
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.zabackaryc.xkcdviewer.data.ListedComic
import kotlin.math.min

fun defaultImageURL(listedComic: ListedComic) =
    "https://imgs.xkcd.com/comics/${
        listedComic.title.lowercase()
            .replace(Regex("[- .()/](?!$)"), "_")
            .replace(Regex("[^0-9a-z_]"), "")
    }.png"

@Composable
fun ComicCarouselItem(
    listedComic: ListedComic?,
    onSelected: () -> Unit,
    itemInfo: CarouselItemInfo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onSelected() }
    ) {
        if (listedComic != null) {
            SubcomposeAsyncImage(
                model = defaultImageURL(listedComic),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            ) {
                when (this@SubcomposeAsyncImage.painter.state) {
                    AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { CircularProgressIndicator() }

                    is AsyncImagePainter.State.Error -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Failed to load preview",
                            modifier = Modifier.size(96.dp),
                            tint = Color.Black
                        )
                    }

                    is AsyncImagePainter.State.Success -> this@SubcomposeAsyncImage.SubcomposeAsyncImageContent()
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0.0F to Color.Transparent,
                            0.6F to Color.Transparent,
                            1.0F to Color(
                                0.0F,
                                0.0F,
                                0.0F,
                                0.7F
                            )
                        )
                    )
            ) {
                Text(
                    text = listedComic.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .graphicsLayer {
                            translationX = itemInfo.maskRect.left
                            alpha = itemInfo.size / itemInfo.maxSize
                        }
                        .padding(16.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (listedComic.favorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "This comic is favorited",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .graphicsLayer {
                            translationX = itemInfo.maskRect.left
                            alpha = itemInfo.size / itemInfo.maxSize
                        }
                        .padding(16.dp),
                    tint = Color(0xffff6b6b)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        }
    }
}

@Composable
fun CarouselBrowseMoreItem(
    onClick: () -> Unit,
    itemInfo: CarouselItemInfo,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(itemInfo.size / min(260F, itemInfo.maxSize)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledIconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Browse more button",
                    modifier = Modifier.size(96.dp)
                )
            }
            Text(
                text = "Browse more",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun ComicCarousel(
    comics: () -> (List<ListedComic>)?,
    onComicSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onBrowseMoreClicked: (() -> Unit)? = null
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState(
            itemCount = {
                comics()?.size?.plus(if (onBrowseMoreClicked != null) 1 else 0) ?: 4
            }
        ),
        preferredItemWidth = 240.dp,
        itemSpacing = 8.dp,
        modifier = modifier
    ) { i ->
        comics().let { comics ->
            if (comics?.size.let {
                    it != null && i >= it
                }) {
                CarouselBrowseMoreItem(
                    onClick = { onBrowseMoreClicked?.invoke() },
                    itemInfo = carouselItemInfo,
                    modifier = Modifier.maskClip(shape = MaterialTheme.shapes.extraLarge)
                )
            } else {
                comics?.get(i).let { listedComic ->
                    ComicCarouselItem(
                        listedComic = listedComic,
                        onSelected = {
                            if (listedComic != null) onComicSelected(listedComic.id)
                        },
                        itemInfo = carouselItemInfo,
                        modifier = Modifier
                            .maskClip(shape = MaterialTheme.shapes.extraLarge)
                    )
                }
            }
        }
    }
}

@Composable
fun ComicCarouselHeader(
    headerText: String,
    onBrowseMoreClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .weight(1f)
        )
        TextButton(onClick = { onBrowseMoreClicked() }) {
            Text(text = "Show all")
        }
    }
}
