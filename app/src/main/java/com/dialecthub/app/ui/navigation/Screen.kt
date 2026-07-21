package com.dialecthub.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")

    data object Category : Screen("category/{categoryId}") {
        const val ARG_CATEGORY_ID = "categoryId"
        fun createRoute(categoryId: String) = "category/$categoryId"
    }

    data object Quiz : Screen("quiz/{categoryId}") {
        const val ARG_CATEGORY_ID = "categoryId"
        fun createRoute(categoryId: String) = "quiz/$categoryId"
    }
}
