package com.dialecthub.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dialecthub.app.data.LessonContent
import com.dialecthub.app.data.ProgressRepository
import com.dialecthub.app.data.SpacedRepetition
import com.dialecthub.app.data.model.Category
import com.dialecthub.app.data.model.CategoryProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class CategoryUiState(
    val category: Category,
    val progress: CategoryProgress
)

class HomeViewModel(private val repository: ProgressRepository) : ViewModel() {

    private val categories = LessonContent.categories

    val uiState: StateFlow<List<CategoryUiState>> =
        repository.observeAllProgress(categories.map { it.id })
            .map { progressMap ->
                categories.map { category ->
                    CategoryUiState(category, progressMap[category.id] ?: CategoryProgress())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = categories.map { CategoryUiState(it, CategoryProgress()) }
            )

    /** Words due for review today, across all categories (not capped at a session's size). */
    val dueCount: StateFlow<Int> =
        repository.reviewStates
            .map { states ->
                val knownIds = categories.flatMap { it.items }.map { it.id }.toSet()
                SpacedRepetition.dueItemIds(
                    states.filterKeys { it in knownIds },
                    LocalDate.now().toEpochDay(),
                    limit = Int.MAX_VALUE
                ).size
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    class Factory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(repository) as T
        }
    }
}
