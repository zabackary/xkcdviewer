package com.zabackaryc.xkcdviewer.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceIntSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSwitch
import com.zabackaryc.xkcdviewer.utils.SettingsItem

@Composable
fun SettingsScreen() {
    val comicActionsExpandState =
        rememberPreferenceBooleanSettingState(
            key = SettingsItem.ComicActionsExpand.preferenceKey,
            defaultValue = SettingsItem.ComicActionsExpand.defaultValue
        )
    val comicSaveHistoryState =
        rememberPreferenceBooleanSettingState(
            key = SettingsItem.ComicSaveHistory.preferenceKey,
            defaultValue = SettingsItem.ComicSaveHistory.defaultValue
        )
    val comicDownloadState =
        rememberPreferenceIntSettingState(
            key = SettingsItem.ComicDownload.preferenceKey,
            defaultValue = SettingsItem.ComicDownload.defaultValue
        )
    val comicDarkThemeState =
        rememberPreferenceBooleanSettingState(
            key = SettingsItem.ComicDarkTheme.preferenceKey,
            defaultValue = SettingsItem.ComicDarkTheme.defaultValue
        )
    val articleDarkThemeState =
        rememberPreferenceBooleanSettingState(
            key = SettingsItem.ArticleDarkTheme.preferenceKey,
            defaultValue = SettingsItem.ArticleDarkTheme.defaultValue
        )

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
        }
    }
}
