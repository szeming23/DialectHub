package com.dialecthub.app.data

import android.content.Context
import com.dialecthub.app.data.model.Category
import com.dialecthub.app.data.model.VocabItem
import org.json.JSONObject

/**
 * Hokkien (Bân-lâm-gú) vocabulary, bundled in `assets/lessons.json` so the
 * app works fully offline. Romanization follows Tâi-lô. [load] runs once
 * from [com.dialecthub.app.DialectHubApplication] before any screen reads
 * [categories]; see the README for the file format.
 */
object LessonContent {

    const val ASSET_FILE = "lessons.json"

    /** A quiz question needs the right answer plus three distractors. */
    const val MIN_ITEMS_PER_CATEGORY = 4

    /**
     * Ids become file names (pronunciation clips), preference keys and
     * navigation routes, so they're limited to characters safe in all three.
     */
    private val ID_PATTERN = Regex("[a-z0-9_]+")

    lateinit var categories: List<Category>
        private set

    fun load(context: Context) {
        if (::categories.isInitialized) return
        val json = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }
        categories = parse(json)
    }

    fun findCategory(categoryId: String): Category =
        categories.first { it.id == categoryId }

    /**
     * Parses and validates the lesson file. Throws on a missing field, an
     * empty value, a duplicate or malformed id or a category too small to
     * quiz, so a bad edit fails loudly at startup (and in the unit tests)
     * rather than showing a broken screen later.
     */
    fun parse(json: String): List<Category> {
        val categoriesJson = JSONObject(json).getJSONArray("categories")
        val categories = (0 until categoriesJson.length()).map { i ->
            val categoryJson = categoriesJson.getJSONObject(i)
            val itemsJson = categoryJson.getJSONArray("items")
            Category(
                id = categoryJson.requireString("id"),
                titleEn = categoryJson.requireString("titleEn"),
                titleHokkien = categoryJson.requireString("titleHokkien"),
                emoji = categoryJson.requireString("emoji"),
                items = (0 until itemsJson.length()).map { j ->
                    val itemJson = itemsJson.getJSONObject(j)
                    VocabItem(
                        id = itemJson.requireString("id"),
                        hanji = itemJson.requireString("hanji"),
                        tailo = itemJson.requireString("tailo"),
                        english = itemJson.requireString("english"),
                        pronunciationHint = itemJson.requireString("pronunciationHint")
                    )
                }
            )
        }

        (categories.map { it.id } + categories.flatMap { it.items }.map { it.id }).forEach { id ->
            require(ID_PATTERN.matches(id)) { "Id '$id' may only use a-z, 0-9 and _" }
        }
        requireUnique(categories.map { it.id }, "category id")
        requireUnique(categories.flatMap { it.items }.map { it.id }, "item id")
        categories.forEach { category ->
            require(category.items.size >= MIN_ITEMS_PER_CATEGORY) {
                "Category '${category.id}' has ${category.items.size} items; quizzes need at least $MIN_ITEMS_PER_CATEGORY"
            }
            requireUnique(category.items.map { it.english }, "English meaning in '${category.id}'")
        }
        return categories
    }

    private fun JSONObject.requireString(key: String): String {
        val value = getString(key).trim()
        require(value.isNotEmpty()) { "Empty '$key' in $this" }
        return value
    }

    private fun requireUnique(values: List<String>, what: String) {
        val duplicates = values.groupingBy { it }.eachCount().filterValues { it > 1 }.keys
        require(duplicates.isEmpty()) { "Duplicate $what: $duplicates" }
    }
}
