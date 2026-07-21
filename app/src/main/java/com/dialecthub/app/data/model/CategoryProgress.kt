package com.dialecthub.app.data.model

data class CategoryProgress(
    val bestScorePercent: Int = 0,
    val timesPracticed: Int = 0
) {
    val isCompleted: Boolean get() = bestScorePercent >= 70
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}
