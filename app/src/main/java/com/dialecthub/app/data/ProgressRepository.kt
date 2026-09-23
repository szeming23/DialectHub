package com.dialecthub.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dialecthub.app.data.model.CategoryProgress
import com.dialecthub.app.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "dialecthub_prefs")

// Kept in its own file so the backup rules in res/xml can exclude it.
private val Context.secretsDataStore by preferencesDataStore(name = "secrets")

/**
 * Persists lightweight learning progress and app preferences locally via
 * DataStore. There's no backend -- everything lives on-device, including
 * the user's own Claude API key.
 */
class ProgressRepository(private val context: Context) {

    fun observeProgress(categoryId: String): Flow<CategoryProgress> =
        context.dataStore.data.map { prefs ->
            CategoryProgress(
                bestScorePercent = prefs[bestScoreKey(categoryId)] ?: 0,
                timesPracticed = prefs[timesPracticedKey(categoryId)] ?: 0
            )
        }

    fun observeAllProgress(categoryIds: List<String>): Flow<Map<String, CategoryProgress>> =
        context.dataStore.data.map { prefs ->
            categoryIds.associateWith { categoryId ->
                CategoryProgress(
                    bestScorePercent = prefs[bestScoreKey(categoryId)] ?: 0,
                    timesPracticed = prefs[timesPracticedKey(categoryId)] ?: 0
                )
            }
        }

    suspend fun saveQuizResult(categoryId: String, scorePercent: Int) {
        context.dataStore.edit { prefs ->
            val previousBest = prefs[bestScoreKey(categoryId)] ?: 0
            prefs[bestScoreKey(categoryId)] = maxOf(previousBest, scorePercent)
            prefs[timesPracticedKey(categoryId)] = (prefs[timesPracticedKey(categoryId)] ?: 0) + 1
        }
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE_KEY]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[THEME_MODE_KEY] = mode.name }
    }

    val claudeApiKey: Flow<String> = context.secretsDataStore.data.map { prefs ->
        prefs[CLAUDE_API_KEY_KEY].orEmpty()
    }

    suspend fun setClaudeApiKey(apiKey: String) {
        context.secretsDataStore.edit { prefs ->
            if (apiKey.isBlank()) prefs.remove(CLAUDE_API_KEY_KEY) else prefs[CLAUDE_API_KEY_KEY] = apiKey.trim()
        }
    }

    suspend fun resetAllProgress() {
        context.dataStore.edit { prefs ->
            val keysToRemove = prefs.asMap().keys.filter {
                it.name.endsWith("_best_score") || it.name.endsWith("_times_practiced")
            }
            keysToRemove.forEach { prefs.remove(it) }
        }
    }

    private fun bestScoreKey(categoryId: String): Preferences.Key<Int> =
        intPreferencesKey("${categoryId}_best_score")

    private fun timesPracticedKey(categoryId: String): Preferences.Key<Int> =
        intPreferencesKey("${categoryId}_times_practiced")

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val CLAUDE_API_KEY_KEY = stringPreferencesKey("claude_api_key")
    }
}
