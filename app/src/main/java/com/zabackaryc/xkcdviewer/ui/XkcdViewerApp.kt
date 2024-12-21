package com.zabackaryc.xkcdviewer.ui

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowWidthSizeClass
import com.zabackaryc.xkcdviewer.R
import com.zabackaryc.xkcdviewer.ui.comic.ComicScreen
import com.zabackaryc.xkcdviewer.ui.search.SearchScreen
import com.zabackaryc.xkcdviewer.ui.settings.AboutScreen
import com.zabackaryc.xkcdviewer.ui.settings.LicenseScreen
import com.zabackaryc.xkcdviewer.ui.settings.SettingsScreen

@Composable
fun XkcdViewerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
        modifier = Modifier
            .fillMaxSize(),
        enterTransition = {
            fadeIn(
                animationSpec = tween(210, delayMillis = 90, easing = LinearOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300, easing = LinearOutSlowInEasing)
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(90, easing = FastOutLinearInEasing)
            ) + scaleOut(
                targetScale = 1.1f,
                animationSpec = tween(300, easing = LinearOutSlowInEasing)
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(210, delayMillis = 90, easing = LinearOutSlowInEasing)
            ) + scaleIn(
                initialScale = 1.1f,
                animationSpec = tween(300, easing = LinearOutSlowInEasing)
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(90, easing = FastOutLinearInEasing)
            ) + scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(300, easing = LinearOutSlowInEasing)
            )
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
        composable(route = "${Route.WhatIf.route}/{${Argument.WhatIfId.name}}",
            arguments = listOf(
                navArgument(Argument.WhatIfId.name) {
                    type = Argument.WhatIfId.type
                }
            )) { _ ->
            Text("what if article")
        }
        composable(Route.Home.route) {
            val homeNavController = rememberNavController()
            val adaptiveInfo = currentWindowAdaptiveInfo()
            val navSuiteType =
                with(adaptiveInfo) {
                    if (windowPosture.isTabletop) {
                        NavigationSuiteType.NavigationBar
                    } else if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
                        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
                    ) {
                        NavigationSuiteType.NavigationRail
                    } else {
                        NavigationSuiteType.NavigationBar
                    }
                }
            val homeNavBackStackEntry by homeNavController.currentBackStackEntryAsState()
            val homeCurrentDestination = homeNavBackStackEntry?.destination

            NavigationSuiteScaffold(
                layoutType = navSuiteType,
                navigationSuiteItems = {
                    val topLevelItems = listOf(
                        TopLevelRoute.ComicList,
                        TopLevelRoute.WhatIfList,
                        TopLevelRoute.Settings
                    )
                    topLevelItems.forEach { route ->
                        item(
                            icon = { Icon(route.icon, contentDescription = null) },
                            label = { Text(stringResource(route.resourceId)) },
                            selected = homeCurrentDestination?.hierarchy?.any { it.route == route.route } == true,
                            onClick = {
                                homeNavController.navigate(route.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(homeNavController.graph.findStartDestination().id) {
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
            ) {
                NavHost(
                    navController = homeNavController,
                    startDestination = TopLevelRoute.ComicList.route,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        fadeIn(
                            animationSpec = tween(
                                210,
                                delayMillis = 90,
                                easing = LinearOutSlowInEasing
                            )
                        ) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(300, easing = LinearOutSlowInEasing)
                        )
                    },
                    exitTransition = {
                        fadeOut(
                            animationSpec = tween(90, easing = FastOutLinearInEasing)
                        ) + scaleOut(
                            targetScale = 1.1f,
                            animationSpec = tween(300, easing = LinearOutSlowInEasing)
                        )
                    },
                    popEnterTransition = {
                        fadeIn(
                            animationSpec = tween(
                                210,
                                delayMillis = 90,
                                easing = LinearOutSlowInEasing
                            )
                        ) + scaleIn(
                            initialScale = 1.1f,
                            animationSpec = tween(300, easing = LinearOutSlowInEasing)
                        )
                    },
                    popExitTransition = {
                        fadeOut(
                            animationSpec = tween(90, easing = FastOutLinearInEasing)
                        ) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(300, easing = LinearOutSlowInEasing)
                        )
                    }
                ) {
                    composable(TopLevelRoute.ComicList.route) { _ ->
                        SearchScreen(
                            onComicSelected = { comicId ->
                                navController.navigate("${Route.Comic.route}/$comicId")
                            },
                            viewModel = hiltViewModel()
                        )
                    }
                    composable(TopLevelRoute.WhatIfList.route) { _ ->
                        Text("what if listing")
                    }
                    composable(TopLevelRoute.Settings.route) { _ ->
                        SettingsScreen(
                            onAboutScreenNavigation = {
                                navController.navigate(Route.SettingsAbout.route)
                            },
                            viewModel = hiltViewModel()
                        )
                    }
                }
            }
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

sealed class TopLevelRoute(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    data object ComicList :
        TopLevelRoute("comic_list", R.string.comic_list, Icons.Filled.AutoStories)

    data object WhatIfList : TopLevelRoute(
        "what_if_list", R.string.what_if_list,
        Icons.AutoMirrored.Filled.Article
    )

    data object Settings : TopLevelRoute("settings", R.string.settings, Icons.Filled.Settings)
}

sealed class Route(val route: String) {
    data object Home : Route("/")
    data object SettingsAbout : Route("/settings/about")
    data object SettingsAboutLicenses : Route("/settings/about/licenses")
    data object Comic : Route("/comic")
    data object WhatIf : Route("/what_if")
}


sealed class Argument(val name: String, val type: NavType<*>) {
    data object ComicId : Argument("comic_id", NavType.IntType)
    data object WhatIfId : Argument("what_if_id", NavType.IntType)
}
