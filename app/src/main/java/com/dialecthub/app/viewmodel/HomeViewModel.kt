package com.dialecthub.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dialecthub.app.data.LessonContent
import com.dialecthub.app.data.ProgressRepository
import com.dialecthub.app.data.model.Category
import com.dialecthub.app.data.model.CategoryProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    class Factory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(repository) as T
        }
    }
}
