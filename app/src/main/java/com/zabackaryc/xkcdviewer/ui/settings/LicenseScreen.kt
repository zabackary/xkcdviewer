package com.zabackaryc.xkcdviewer.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    var activeLicenseDialog by remember { // not rememberSaveable since `License?` can't be `Bundle`d
        mutableStateOf<License?>(null)
    }
    var activeLibraryDialog by remember { // not rememberSaveable since `License?` can't be `Bundle`d
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
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                    contentDescription = null
                )
            },
            title = { Text(license.name) },
            text = {
                val scrollState = rememberScrollState()
                Column {
                    val topDividerAlpha by animateFloatAsState(
                        label = "top divider alpha fade",
                        targetValue = if (scrollState.canScrollBackward) {
                            1.0F
                        } else {
                            0.0F
                        }
                    )
                    HorizontalDivider(modifier = Modifier.alpha(topDividerAlpha))
                    Text(
                        text = license.licenseContent.let {
                            if (it?.isNotEmpty() == true) {
                                it
                            } else {
                                "No preview is available for this license."
                            }
                        },
                        modifier = Modifier.verticalScroll(scrollState),
                        fontFamily = FontFamily.Monospace
                    )
                    val bottomDividerAlpha by animateFloatAsState(
                        label = "bottom divider alpha fade",
                        targetValue = if (scrollState.canScrollForward) {
                            1.0F
                        } else {
                            0.0F
                        }
                    )
                    HorizontalDivider(modifier = Modifier.alpha(bottomDividerAlpha))
                }
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
        Dialog(
            onDismissRequest = { activeLibraryDialog = null }
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .widthIn(280.dp, 560.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = library.name,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        )
                        library.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Column {
                        Text(
                            text = "Artifact ID",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(library.artifactId)

                        library.artifactVersion?.let {
                            Text(
                                text = "Artifact version",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            Text(it)
                        }
                        library.organization?.let {
                            Text(
                                text = "Organization name",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            Text(it.name)

                        }
                        if (library.developers.isNotEmpty()) {
                            Text(
                                text = "Developers",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            LazyRow {
                                items(library.developers) { developer ->
                                    SuggestionChip(
                                        onClick = {
                                            developer.organisationUrl?.let { launchURL(it) }
                                        },
                                        label = { Text(developer.name ?: "Unknown developer") },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }

                        }
                        if (library.funding.isNotEmpty()) {
                            Text(
                                text = "Funding",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            val funding = remember { library.funding.toList() }
                            LazyRow {
                                items(funding) { fund ->
                                    SuggestionChip(
                                        onClick = {
                                            launchURL(fund.url)
                                        },
                                        label = { Text(fund.platform) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }

                        }
                        if (library.licenses.isNotEmpty()) {
                            Text(
                                text = "Licenses",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            val licenses = remember { library.licenses.toList() }
                            LazyRow {
                                items(licenses) { license ->
                                    SuggestionChip(
                                        onClick = {
                                            activeLicenseDialog = license
                                        },
                                        label = { Text(license.name) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }

                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        library.website?.let {
                            TextButton(onClick = { launchURL(it) }) {
                                Text("Visit website")
                            }
                        }
                        library.scm?.url?.let {
                            TextButton(onClick = { launchURL(it) }) {
                                Text("Open repository")
                            }
                        }
                        TextButton(onClick = {
                            activeLibraryDialog = null
                        }) {
                            Text("Done")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Third-party licenses and libraries") },
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
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        val licenses = remember(libraryInfo) { libraryInfo.licenses.toList() }
        LazyColumn(contentPadding = paddingValues) {
            item {
                HorizontalDivider()
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Text(
                            text = "View license:",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    items(licenses) { license ->
                        SuggestionChip(
                            onClick = { activeLicenseDialog = license },
                            label = { Text(license.name) },
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            item {
                HorizontalDivider()
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
