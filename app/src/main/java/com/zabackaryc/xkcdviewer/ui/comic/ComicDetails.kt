package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic
import com.zabackaryc.xkcdviewer.utils.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@Composable
fun ComicDetails(
    listedComic: ListedComic?,
    cachedComic: CachedComic?,
    initialPage: Int
) {
    val pagerState = rememberPagerState(initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()
    val titles = listOf("About", "Mouseover", "Transcript")

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        HorizontalPager(
            count = titles.size,
            state = pagerState
        ) { page ->
            Box(
                modifier = Modifier
                    .height(220.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (listedComic != null && cachedComic != null) {
                    when (page) {
                        0 -> {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row {
                                    Text("Nothing here yet :)")
                                }
                            }
                        }
                        1 -> {
                            Text(cachedComic.mouseover, modifier = Modifier.padding(16.dp))
                        }
                        2 -> {
                            if (cachedComic.transcript != null && cachedComic.transcript != "") {
                                Text(cachedComic.transcript, modifier = Modifier.padding(16.dp))
                            } else {
                                Text(
                                    "This comic doesn't have a transcript.",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
