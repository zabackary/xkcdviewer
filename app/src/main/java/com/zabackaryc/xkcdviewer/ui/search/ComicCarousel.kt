package com.zabackaryc.xkcdviewer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselItemInfo
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

fun defaultImageURL(listedComic: ListedComic) =
    "https://imgs.xkcd.com/comics/${
        listedComic.title.lowercase().replace(Regex("[- (]"), "_").replace(Regex("[^a-z_]"), "")
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
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Failed to load preview",
                            modifier = Modifier.size(96.dp)
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
                                if (listedComic.favorite) 0.3F else 0.0F, // show a little red for favorite comics
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
fun CarouselBrowseMoreItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
