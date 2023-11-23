package com.zabackaryc.xkcdviewer.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.zabackaryc.xkcdviewer.utils.librarylist.LibraryList

@Composable
fun LicenseScreen(
    onNavigationUp: () -> Unit
) {
    val context = LocalContext.current
    val libraryInfo = remember(context) {
        LibraryList.getLibraryList(context)
    }
    var activeLicenseDialog by remember {
        mutableStateOf<License?>(null)
    }
    var activeLibraryDialog by remember {
        mutableStateOf<Library?>(null)
    }
    val launchURL = { url: String ->
        ContextCompat.startActivity(
            context,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            ),
            null
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val license = activeLicenseDialog
    if (license != null) {
        AlertDialog(
            onDismissRequest = { activeLicenseDialog = null },
            icon = { Icon(imageVector = Icons.Default.TextSnippet, contentDescription = null) },
            title = { Text(license.name) },
            text = {
                Text(
                    text = license.licenseContent ?: "Press 'Open license' to view the license",
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(onClick = {

                }) {
                    Text("Open license")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    activeLicenseDialog = null
                }) {
                    Text("Done")
                }
            }
        )
    }

    val library = activeLibraryDialog
    if (library != null) {
        AlertDialog(
            onDismissRequest = { activeLibraryDialog = null },
            title = {
                Text(library.name)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    library.description?.let {
                        Text(
                            text = it,
                            fontStyle = FontStyle.Italic
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                    Row {
                        Text(
                            text = "Artifact ID: ",
                            fontWeight = FontWeight.Bold
                        )
                        Text(library.artifactId)
                    }
                    library.artifactVersion?.let {
                        Row {
                            Text(
                                text = "Artifact version: ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(it)
                        }
                    }
                    library.organization?.let {
                        Row {
                            Text(
                                text = "Organization name: ",
                                fontWeight = FontWeight.Bold
                            )
                            Text(it.name)
                        }
                    }
                    if (library.developers.isNotEmpty()) {
                        Row {
                            Text(
                                text = "Developers: ",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            LazyRow {
                                items(library.developers) { developer ->
                                    SuggestionChip(
                                        onClick = {
                                            developer.organisationUrl?.let { launchURL(it) }
                                        },
                                        label = { Text(developer.name ?: "Unknown developer") },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (library.funding.isNotEmpty()) {
                        Row {
                            Text(
                                text = "Funding: ",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            val funding = remember { library.funding.toList() }
                            LazyRow {
                                items(funding) { fund ->
                                    SuggestionChip(
                                        onClick = {
                                            launchURL(fund.url)
                                        },
                                        label = { Text(fund.platform) },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (library.licenses.isNotEmpty()) {
                        Row {
                            Text(
                                text = "Licenses: ",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            val licenses = remember { library.licenses.toList() }
                            LazyRow {
                                items(licenses) { license ->
                                    SuggestionChip(
                                        onClick = {
                                            activeLicenseDialog = license
                                        },
                                        label = { Text(license.name) },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        library.website?.let {
                            FilledTonalButton(onClick = { launchURL(it) }) {
                                Text("Visit website")
                            }
                        }
                        library.scm?.url?.let {
                            FilledTonalButton(onClick = { launchURL(it) }) {
                                Text("Open repository")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    activeLibraryDialog = null
                }) {
                    Text("Done")
                }
            }
        )
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Third-party licenses") },
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
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        val licenses = remember(libraryInfo) { libraryInfo.licenses.toList() }
        LazyColumn(contentPadding = paddingValues) {
            item {
                LazyRow {
                    items(licenses) { license ->
                        SuggestionChip(
                            onClick = { activeLicenseDialog = license },
                            label = { Text(license.name) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            items(libraryInfo.libraries) { library ->
                ListItem(
                    headlineContent = { Text(library.name) },
                    supportingContent = { Text(library.licenses.joinToString(", ") { it.name }) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View library information"
                        )
                    },
                    modifier = Modifier.clickable {
                        activeLibraryDialog = library
                    }
                )
            }
        }
    }
}
