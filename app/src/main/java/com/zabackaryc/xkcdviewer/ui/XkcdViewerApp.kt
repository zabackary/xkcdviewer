package com.zabackaryc.xkcdviewer.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zabackaryc.xkcdviewer.R
import com.zabackaryc.xkcdviewer.ui.comic.ComicScreen
import com.zabackaryc.xkcdviewer.ui.search.SearchScreen
import com.zabackaryc.xkcdviewer.ui.settings.AboutScreen
import com.zabackaryc.xkcdviewer.ui.settings.LicenseScreen
import com.zabackaryc.xkcdviewer.ui.settings.SettingsScreen

@Composable
fun XkcdViewerApp() {
    val navController = rememberNavController()
    val topLevelItems = listOf(Route.ComicList, Route.WhatIfList, Route.Settings)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navBarVisible = topLevelItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = navBarVisible,
                enter = slideInVertically {
                    it
                },
                exit = slideOutVertically {
                    it
                }
            ) {
                NavigationBar {
                    topLevelItems.forEach { route ->
                        NavigationBarItem(
                            icon = { Icon(route.icon, contentDescription = null) },
                            label = { Text(stringResource(route.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == route.route } == true,
                            onClick = {
                                navController.navigate(route.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.ComicList.route,
            modifier = Modifier
                .padding(
                    if (navBarVisible) PaddingValues(bottom = innerPadding.calculateBottomPadding())
                    else PaddingValues(0.dp)
                )
                .fillMaxSize(),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(210, delayMillis = 90, easing = LinearOutSlowInEasing)
                ) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(210, delayMillis = 90, easing = LinearOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(90, easing = FastOutLinearInEasing))
            }
        ) {
            composable(route = "${Route.Comic.route}/{${Argument.ComicId.name}}",
                arguments = listOf(
                    navArgument(Argument.ComicId.name) {
                        type = Argument.ComicId.type
                    }
                )) { _ ->
                ComicScreen(
                    onNavigationUp = {
                        navController.popBackStack()
                    },
                    viewModel = hiltViewModel()
                )
            }
            composable(Route.ComicList.route) { _ ->
                SearchScreen(
                    onComicSelected = { comicId ->
                        navController.navigate("${Route.Comic.route}/$comicId")
                    },
                    onTopLevelDestinationSelected = { route ->
                        navController.navigate(
                            route,
                            navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
                        )
                    },
                    viewModel = hiltViewModel()
                )
            }
            composable(route = "${Route.WhatIf.route}/{${Argument.WhatIfId.name}}",
                arguments = listOf(
                    navArgument(Argument.WhatIfId.name) {
                        type = Argument.WhatIfId.type
                    }
                )) { _ ->
                Text("what if article")
            }
            composable(Route.WhatIfList.route) { _ ->
                Text("what if listing")
            }
            composable(Route.Settings.route) { _ ->
                SettingsScreen(
                    onAboutScreenNavigation = {
                        navController.navigate(Route.SettingsAbout.route)
                    },
                    viewModel = hiltViewModel()
                )
            }
            composable(Route.SettingsAbout.route) { _ ->
                AboutScreen(
                    onNavigationUp = { navController.navigateUp() },
                    onLicenseNavigation = { navController.navigate(Route.SettingsAboutLicenses.route) }
                )
            }
            composable(Route.SettingsAboutLicenses.route) { _ ->
                LicenseScreen(
                    onNavigationUp = { navController.navigateUp() }
                )
            }
        }
    }
}

sealed class Route(val route: String) {
    sealed class NamedRoute(
        route: String,
        @StringRes val resourceId: Int,
        val icon: ImageVector
    ) : Route(route)

    data object ComicList : NamedRoute("comic_list", R.string.comic_list, Icons.Filled.AutoStories)
    data object WhatIfList : NamedRoute("what_if_list", R.string.what_if_list, Icons.Filled.Article)
    data object Settings : NamedRoute("settings", R.string.settings, Icons.Filled.Settings)
    data object SettingsAbout : Route("settings/about")
    data object SettingsAboutLicenses : Route("settings/about/licenses")
    data object Comic : Route("comic")
    data object WhatIf : Route("what_if")
}


sealed class Argument(val name: String, val type: NavType<*>) {
    data object ComicId : Argument("comic_id", NavType.IntType)
    data object WhatIfId : Argument("what_if_id", NavType.IntType)
}
