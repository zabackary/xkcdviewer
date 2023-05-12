package com.zabackaryc.xkcdviewer.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zabackaryc.xkcdviewer.ui.comic.ComicScreen
import com.zabackaryc.xkcdviewer.ui.search.SearchScreen

@Composable
fun XkcdViewerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.SEARCH) {
        composable(route = "${Route.COMIC}/{${Argument.COMIC_ID}}", arguments = listOf(
            navArgument(Argument.COMIC_ID) {
                type = NavType.IntType
            }
        )) { _ ->
            ComicScreen(
                onNavigationUp = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }
        composable(Route.SEARCH) { _ ->
            SearchScreen(
                onComicSelected = { comicId ->
                    navController.navigate("${Route.COMIC}/$comicId")
                },
                viewModel = hiltViewModel()
            )
        }
    }
}

object Route {
    const val COMIC = "comic"
    const val SEARCH = "search"
}

object Argument {
    const val COMIC_ID = "comic_id"
}
