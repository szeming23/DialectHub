package com.dialecthub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dialecthub.app.DialectHubApplication
import com.dialecthub.app.ui.screens.CategoryScreen
import com.dialecthub.app.ui.screens.HomeScreen
import com.dialecthub.app.ui.screens.QuizScreen
import com.dialecthub.app.ui.screens.SettingsScreen
import com.dialecthub.app.viewmodel.HomeViewModel
import com.dialecthub.app.viewmodel.QuizViewModel
import com.dialecthub.app.viewmodel.SettingsViewModel

@Composable
fun DialectHubNavHost(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as DialectHubApplication
    val repository = app.progressRepository

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = viewModel,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Category.createRoute(categoryId))
                },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument(Screen.Category.ARG_CATEGORY_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(Screen.Category.ARG_CATEGORY_ID).orEmpty()
            CategoryScreen(
                categoryId = categoryId,
                onStartQuiz = { navController.navigate(Screen.Quiz.createRoute(categoryId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument(Screen.Quiz.ARG_CATEGORY_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(Screen.Quiz.ARG_CATEGORY_ID).orEmpty()
            val viewModel: QuizViewModel = viewModel(
                key = "quiz_$categoryId",
                factory = QuizViewModel.Factory(categoryId, repository)
            )
            QuizScreen(
                viewModel = viewModel,
                onFinish = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
