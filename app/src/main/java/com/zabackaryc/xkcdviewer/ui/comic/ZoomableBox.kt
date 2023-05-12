package com.zabackaryc.xkcdviewer.ui.comic

import android.view.ViewConfiguration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.withSign

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 5f,
    isRotatable: Boolean = false,
    resetRotationOnRelease: Boolean = true,
    setScrollEnabled: (Boolean) -> Unit,
    onLongPress: (() -> Unit)? = null,
    onTransformChange: (Transform) -> Unit = {},
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    var targetScale by remember { mutableStateOf(1f) }
    val scale by animateFloatAsState(targetValue = maxOf(minScale, minOf(maxScale, targetScale)))
    var targetRotation by remember { mutableStateOf(0f) }
    val rotation by animateFloatAsState(targetValue = targetRotation)
    var offsetX by remember { mutableStateOf(1f) }
    var offsetY by remember { mutableStateOf(1f) }
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .clip(RectangleShape)
            .background(Color.Transparent)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { },
                onDoubleClick = {
                    if (targetScale >= 2f) {
                        targetScale = 1f
                        offsetX = 1f
                        offsetY = 1f
                        setScrollEnabled(true)
                    } else targetScale = 3f
                },
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    val start = Instant.now()
                    var hasMoved = false
                    do {
                        val event = awaitPointerEvent()
                        val zoom = event.calculateZoom()
                        targetScale *= zoom
                        val offset = event.calculatePan()
                        if (targetScale <= 1) {
                            offsetX = 1f
                            offsetY = 1f
                            targetScale = 1f
                            setScrollEnabled(true)
                        } else {
                            offsetX += offset.x
                            offsetY += offset.y
                            if (zoom > 1) {
                                setScrollEnabled(false)
                                targetRotation += event.calculateRotation()
                            }
                            val contentWidth = screenWidthPx * scale
                            val borderReached = contentWidth - screenWidthPx - 2 * abs(offsetX)
                            setScrollEnabled(borderReached <= 0)
                            if (borderReached < 0) {
                                offsetX =
                                    ((contentWidth - screenWidthPx) / 2f).withSign(offsetX)
                                if (offset.x != 0f) offsetY -= offset.y
                            }
                        }
                        if (zoom != 1f || offset != Offset(0f, 0f)) {
                            hasMoved = true
                        }
                        onTransformChange(
                            Transform(
                                scale = scale,
                                offsetX = offsetX,
                                offsetY = offsetY,
                                rotation = rotation
                            )
                        )
                    } while (event.changes.any { it.pressed })
                    if (
                        !hasMoved &&
                        onLongPress != null &&
                        start.until(
                            Instant.now(),
                            ChronoUnit.MILLIS
                        ) > ViewConfiguration.getLongPressTimeout()
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                    if (resetRotationOnRelease) targetRotation = 0f
                }
            }

    ) {
        Box(
            modifier = modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    if (isRotatable) {
                        rotationZ = rotation
                    }
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            val scope = ZoomableBoxScope(
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                rotation = rotation
            )
            scope.content()
        }
    }
}

// I get that everyone likes DRY. I tried...

data class Transform(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val rotation: Float
)


data class ZoomableBoxScope(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val rotation: Float
)
