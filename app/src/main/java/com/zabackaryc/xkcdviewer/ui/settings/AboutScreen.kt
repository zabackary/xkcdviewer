package com.zabackaryc.xkcdviewer.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.zabackaryc.xkcdviewer.BuildConfig
import com.zabackaryc.xkcdviewer.R


@Composable
fun AboutScreen(
    onNavigationUp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("About") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { onNavigationUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "xkcd viewer logo",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(12.dp, 24.dp, 12.dp, 4.dp)
                    .size(86.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Text(
                text = "xkcd viewer",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            )
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.large),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null
                    )
                    Text(
                        text = BuildConfig.GIT_VERSION_LONG,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            val context = LocalContext.current
            ListItem(
                headlineContent = { Text("Source available on GitHub") },
                supportingContent = { Text("zabackary/xkcdviewer") },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Click to open in browser"
                    )
                },
                modifier = Modifier.clickable {
                    startActivity(
                        context,
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/zabackary/xkcdviewer")
                        ),
                        null
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Made with \ud83d\udc96 by @zabackary in \u65E5\u672c") },
                supportingContent = { Text("Jesus loves you and cares for you, even in the darkest depths of your life.") }
            )
            ListItem(
                headlineContent = { Text("View open source licenses") },
                supportingContent = { Text("This application depends on a variety of open source libraries others have put time and effort into.") },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View"
                    )
                },
                modifier = Modifier.clickable {
                }
            )
        }
    }
}
