package com.dialecthub.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dialecthub.app.data.ClaudeTranslator
import com.dialecthub.app.data.ProgressRepository
import com.dialecthub.app.data.TranslationOutcome
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TranslateViewModel(private val repository: ProgressRepository) : ViewModel() {

    // Null until DataStore has loaded, so the screen doesn't flash the "add a key" prompt.
    val hasApiKey: StateFlow<Boolean?> = repository.claudeApiKey
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    var input by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var outcome by mutableStateOf<TranslationOutcome?>(null)
        private set

    fun onInputChange(value: String) {
        input = value.take(ClaudeTranslator.MAX_INPUT_LENGTH)
    }

    fun translate() {
        val english = input.trim()
        if (english.isEmpty() || isLoading) return
        isLoading = true
        viewModelScope.launch {
            outcome = ClaudeTranslator.translate(repository.claudeApiKey.first(), english)
            isLoading = false
        }
    }

    class Factory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(TranslateViewModel::class.java))
            return TranslateViewModel(repository) as T
        }
    }
}
