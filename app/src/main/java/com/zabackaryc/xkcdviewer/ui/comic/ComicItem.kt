package com.zabackaryc.xkcdviewer.ui.comic

import android.annotation.SuppressLint
import android.text.Html.escapeHtml
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.utils.Constant
import com.zabackaryc.xkcdviewer.utils.SettingsItem
import com.zabackaryc.xkcdviewer.utils.ThemingTransformation
import com.zabackaryc.xkcdviewer.utils.exceptions.XkcdExceptions
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComicItem(
    id: Int,
    cachedComic: CachedComic?,
    showComicDetails: () -> Unit,
    modifier: Modifier = Modifier,
    setElevatedAppBar: (Boolean) -> Unit = {},
) {
    val shouldPreprocessImage = SettingsItem.ComicDarkTheme.currentValue
    val context = LocalContext.current
    val exceptions = remember(context) {
        XkcdExceptions.getExceptions(context)
    }
    val exception = remember(cachedComic) {
        exceptions.firstOrNull { it.id == cachedComic?.id }
    }
    val imgUrl = remember(cachedComic) {
        return@remember if (cachedComic != null) {
            if (exception?.largeUrl != null) {
                exception.largeUrl
            } else if (
                cachedComic.id >= Constant.FIRST_2X_COMIC &&
                exception?.srcsetUnavailable != true
            ) {
                cachedComic.imgUrl.substringBeforeLast(".") + "_2x." + cachedComic.imgUrl.substringAfterLast(
                    "."
                )
            } else {
                cachedComic.imgUrl
            }
        } else {
            null
        }
    }
    val zoomState = rememberZoomState()
    val dynamicHtmlModel = remember(cachedComic) { cachedComic?.getDynamicHtmlModel() }
    if (cachedComic != null && dynamicHtmlModel != null && imgUrl != null) {
        val html = remember(dynamicHtmlModel) {
            """
<!DOCTYPE html>
<html>
    <body>
        ${dynamicHtmlModel.headerExtra}
        <div id="comic">
            ${dynamicHtmlModel.pre}
            <img
                src="${escapeHtml(cachedComic.imgUrl)}"
                title="${escapeHtml(cachedComic.mouseover)}"
                style="image-orientation:none" ${dynamicHtmlModel.imgAttr} />
            ${dynamicHtmlModel.post}
        </div>
    </body>
</html>"""
        }
        Log.d("xkcdviewer", html)
        val webViewState = rememberWebViewStateWithHTMLData(
            data = html,
            baseUrl = "https://xkcd.com/${cachedComic.id}"
        )
        WebView(
            state = webViewState,
            modifier = modifier.fillMaxSize(),
            captureBackPresses = false,
            onCreated = { it.settings.javaScriptEnabled = true }
        )
    } else if (id == 404) {
        var eggScale by remember { mutableFloatStateOf(1.0F) }
        val eggAnimatedScale by animateFloatAsState(
            targetValue = eggScale,
            label = "easter egg scale animation"
        )
        val eggWobble = remember { Animatable(0.0F) }
        val eggWobbleContext = rememberCoroutineScope()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Egg,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp * eggAnimatedScale)
                    .rotate(eggWobble.value)
                    .clip(CircleShape)
                    .clickable {
                        if (eggScale < 15.0F) eggScale *= 1.1F
                        eggWobbleContext.launch {
                            eggWobble.animateTo(
                                ((30F - Math.random() * 60F).toFloat()),
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                            eggWobble.animateTo(
                                0.0F,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioHighBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            )
                        }
                    }
            )
            Text(
                text = "404. That's an error. And for some reason you're seeing it in an Android app.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    PaddingValues(
                        24.dp,
                        12.dp,
                        24.dp,
                        0.dp
                    )
                )
            )
            Text(
                text = "xkcd #404, published on 01-01-1970",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    PaddingValues(
                        24.dp,
                        12.dp,
                        24.dp,
                        0.dp
                    )
                )
            )
        }
    } else {
        val imageModel: Any? = if (shouldPreprocessImage) ImageRequest.Builder(LocalContext.current)
            .data(imgUrl)
            .allowHardware(false) // Disable hardware bitmaps so pixels can be read
            .transformations(
                ThemingTransformation(
                    isSystemInDarkTheme(),
                    MaterialTheme.colorScheme.onBackground.toArgb(),
                    MaterialTheme.colorScheme.background.toArgb()
                )
            )
            .build()
        else imgUrl
        val hapticFeedback = LocalHapticFeedback.current
        SubcomposeAsyncImage(
            model = imageModel,
            contentDescription = cachedComic?.transcript,
            modifier = modifier
                .fillMaxSize()
                .rotate(exception?.rotate?.toFloat() ?: 0f)
                .zoomable(zoomState)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            showComicDetails()
                        }
                    )
                }
        ) {
            if (painter.state is AsyncImagePainter.State.Loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }
            } else if (painter.state is AsyncImagePainter.State.Error || cachedComic == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Failed to load image.",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Comic failed to load",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(PaddingValues(top = 12.dp))
                    )
                }
            } else {
                SubcomposeAsyncImageContent()
            }
        }

    }
}
