package com.zabackaryc.xkcdviewer.ui.comic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun ComicOverflowMenu(
    onDetailsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = "Show more actions",
        )
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("Show details") },
            onClick = {
                onDetailsClick()
                showMenu = false
            },
            leadingIcon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) })
        DropdownMenuItem(
            text = { Text("Show recent history") },
            onClick = {
                onHistoryClick()
                showMenu = false
            },
            leadingIcon = { Icon(imageVector = Icons.Default.History, contentDescription = null) })
    }
}
