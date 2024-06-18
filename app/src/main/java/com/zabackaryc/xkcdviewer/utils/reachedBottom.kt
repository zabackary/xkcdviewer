package com.zabackaryc.xkcdviewer.utils

import androidx.compose.foundation.lazy.LazyListState

/**
 * From
 * https://medium.com/@giorgos.patronas1/endless-scrolling-in-android-with-jetpack-compose-af1f55a03d1a
 */
internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}
