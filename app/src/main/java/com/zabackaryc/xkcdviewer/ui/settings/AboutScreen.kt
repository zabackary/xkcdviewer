package com.zabackaryc.xkcdviewer.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
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
    onNavigationUp: () -> Unit,
    onLicenseNavigation: () -> Unit
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "xkcd viewer logo",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(12.dp, 24.dp, 12.dp, 4.dp)
                        .clip(CircleShape)
                        .size(86.dp)
                        .background(Color.White)
                )
                Text(
                    text = "xkcd viewer",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
            item {
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
                            contentDescription = "Git commit tag/hash"
                        )
                        Text(
                            text = BuildConfig.GIT_VERSION_LONG,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            item {
                val context = LocalContext.current
                ListItem(
                    headlineContent = { Text("Source available on GitHub") },
                    supportingContent = { Text("zabackary/xkcdviewer") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
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
            }
            item {
                ListItem(
                    headlineContent = { Text("View open source licenses") },
                    supportingContent = { Text("Thank you to all the maintainers and contributors that make xkcd viewer possible! xkcd.com/2347") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View"
                        )
                    },
                    modifier = Modifier.clickable {
                        onLicenseNavigation()
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Buy me a coffee") },
                    supportingContent = { Text("Actually, I don't drink coffee, so feel free to donate your money elsewhere. Leaving a review encourages me and helps me know what to work on next, though!") }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Made with \ud83d\udc96 by @zabackary in \u65E5\u672c") },
                    // Romans 5:6-8 (NIV, SKY 2017)
                    supportingContent = { Text("You see, at just the right time, when we were still powerless, Christ died for the ungodly. Very rarely will anyone die for a righteous person, though for a good person someone might possibly dare to die. But God demonstrates his own love for us in this: While we were still sinners, Christ died for us.\n実にキリストは、私たちがまだ弱かったころ、定められた時に、不敬虔な者たちのために死んでくださいました。正しい人のためであっても、死ぬ人はほとんどいません。善良な人のためなら、進んで死ぬ人がいるかもしれません。しかし、私たちがまだ罪人であったとき、キリストが私たちのために死なれたことによって、神は私たちに対するご自分の愛を明らかにしておられます。") }
                )
            }
        }
    }
}
