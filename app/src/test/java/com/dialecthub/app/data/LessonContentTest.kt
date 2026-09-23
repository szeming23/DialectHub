package com.dialecthub.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.File

class LessonContentTest {

    @Test
    fun bundledLessonFileIsValid() {
        // Unit tests run with the app module as the working directory.
        val json = File("src/main/assets/${LessonContent.ASSET_FILE}").readText()
        val categories = LessonContent.parse(json)
        assertEquals(5, categories.size)
        assertEquals(50, categories.sumOf { it.items.size })
    }

    @Test
    fun parsesCategoryAndItemFields() {
        val category = LessonContent.parse(lessons(items = fourItems())).single()
        assertEquals("food", category.id)
        assertEquals("食物", category.titleHokkien)
        assertEquals("Rice", category.items.first().english)
        assertEquals("bng", category.items.first().pronunciationHint)
    }

    @Test
    fun rejectsDuplicateItemIds() {
        val items = fourItems().replace("\"food_2\"", "\"food_1\"")
        assertThrows(IllegalArgumentException::class.java) { LessonContent.parse(lessons(items = items)) }
    }

    @Test
    fun rejectsDuplicateEnglishMeaningsInACategory() {
        val items = fourItems().replace("\"Water\"", "\"Rice\"")
        assertThrows(IllegalArgumentException::class.java) { LessonContent.parse(lessons(items = items)) }
    }

    @Test
    fun rejectsCategoryTooSmallToQuiz() {
        val items = fourItems().substringBeforeLast(",")
        assertThrows(IllegalArgumentException::class.java) { LessonContent.parse(lessons(items = items)) }
    }

    @Test
    fun rejectsBlankField() {
        val items = fourItems().replace("\"Tê\"", "\" \"")
        assertThrows(IllegalArgumentException::class.java) { LessonContent.parse(lessons(items = items)) }
    }

    private fun fourItems() = listOf(
        item("food_1", "飯", "Pn̄g", "Rice", "bng"),
        item("food_2", "水", "Tsuí", "Water", "zui"),
        item("food_3", "茶", "Tê", "Tea", "de"),
        item("food_4", "麵", "Mī", "Noodles", "mi")
    ).joinToString(",")

    private fun item(id: String, hanji: String, tailo: String, english: String, hint: String) =
        """{"id":"$id","hanji":"$hanji","tailo":"$tailo","english":"$english","pronunciationHint":"$hint"}"""

    private fun lessons(items: String) =
        """{"categories":[{"id":"food","titleEn":"Food","titleHokkien":"食物","emoji":"🍜","items":[$items]}]}"""
}
