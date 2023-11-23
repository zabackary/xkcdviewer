package com.zabackaryc.xkcdviewer.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSwitch
import com.zabackaryc.xkcdviewer.BuildConfig
import com.zabackaryc.xkcdviewer.utils.SettingsItem

@Composable
fun SettingsScreen(
    onAboutScreenNavigation: () -> Unit
) {
    val appNewsBannerState = SettingsItem.AppNewsBanner.rememberPreferenceState()
    val comicActionsExpandState = SettingsItem.ComicActionsExpand.rememberPreferenceState()
    val comicSaveHistoryState = SettingsItem.ComicSaveHistory.rememberPreferenceState()
    val comicDownloadState = SettingsItem.ComicDownload.rememberPreferenceState()
    val comicDarkThemeState = SettingsItem.ComicDarkTheme.rememberPreferenceState()
    val comicMetadataPopupState = SettingsItem.ComicMetadataPopup.rememberPreferenceState()
    val articleDarkThemeState = SettingsItem.ArticleDarkTheme.rememberPreferenceState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
        ) {
            item {
                Text(
                    text = "General preferences",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 0.dp)
                )
            }
            item {
                SettingsSwitch(
                    title = { Text("Show news") },
                    subtitle = { Text("Shows the news shown below the xkcd logo on xkcd.com on the homepage, if any") },
                    state = appNewsBannerState
                )
            }
            item {
                Text(
                    text = "Comic viewer",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 0.dp)
                )
            }
            item {
                SettingsSwitch(
                    title = { Text("Adapt to dark theme") },
                    subtitle = { Text("Processes the comic content to match the theme") },
                    state = comicDarkThemeState
                )
            }
            item {
                SettingsSwitch(
                    title = { Text("Expand comic actions by default") },
                    subtitle = { Text("Shows all actions in the overflow menu by default") },
                    state = comicActionsExpandState
                )
            }
            item {
                SettingsSwitch(
                    title = { Text("Save history") },
                    subtitle = { Text("Saves comics you view to History. This does not delete your preview viewing history.") },
                    state = comicSaveHistoryState
                )
            }
            item {
                SettingsList(
                    title = { Text("Download comics for offline viewing") },
                    items = listOf("All comics", "Only favorites", "Never"),
                    state = comicDownloadState
                )
            }
            item {
                SettingsSwitch(
                    title = { Text("Show metadata popup") },
                    subtitle = { Text("Shows a popup when a comic with extra metadata like a link or news is opened") },
                    state = comicMetadataPopupState
                )
            }
            item {
                Text(
                    text = "What If reader",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 0.dp),
                )
            }
            item {
                SettingsSwitch(
                    title = { Text("Adapt to dark theme") },
                    subtitle = { Text("Processes the article content to make it more pleasant to read in dark areas") },
                    state = articleDarkThemeState
                )
            }
            item {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.large)
                        .clickable { onAboutScreenNavigation() },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "xkcd viewer ${BuildConfig.GIT_TAG}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open about view"
                        )
                    }
                }
            }
        }
    }
}
