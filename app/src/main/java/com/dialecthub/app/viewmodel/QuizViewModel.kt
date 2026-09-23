package com.dialecthub.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dialecthub.app.data.LessonContent
import com.dialecthub.app.data.ProgressRepository
import com.dialecthub.app.data.SpacedRepetition
import com.dialecthub.app.data.model.VocabItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

data class QuizQuestion(
    val vocab: VocabItem,
    val options: List<String>,
    val correctIndex: Int
)

/**
 * Runs a multiple-choice quiz over one category, or, when [categoryId] is
 * null, over the words currently due for spaced-repetition review. Every
 * answer updates that word's review schedule.
 */
class QuizViewModel(
    private val categoryId: String?,
    private val repository: ProgressRepository
) : ViewModel() {

    val title: String =
        if (categoryId == null) "Review" else "${LessonContent.findCategory(categoryId).titleEn} Quiz"

    /** Null while the review words are loading; empty if nothing is due. */
    var questions by mutableStateOf<List<QuizQuestion>?>(null)
        private set

    var currentIndex by mutableStateOf(0)
        private set
    var selectedOption by mutableStateOf<Int?>(null)
        private set
    var score by mutableStateOf(0)
        private set
    var isFinished by mutableStateOf(false)
        private set

    val totalQuestions: Int get() = questions?.size ?: 0
    val currentQuestion: QuizQuestion get() = questions!![currentIndex]
    val scorePercent: Int get() = if (totalQuestions == 0) 0 else (score * 100) / totalQuestions

    init {
        if (categoryId != null) {
            questions = buildQuestions(LessonContent.findCategory(categoryId).items.shuffled())
        } else {
            viewModelScope.launch {
                val itemsById = LessonContent.categories.flatMap { it.items }.associateBy { it.id }
                // Words removed from the lesson file may still have a saved schedule.
                val dueIds = SpacedRepetition.dueItemIds(
                    repository.reviewStates.first().filterKeys { it in itemsById },
                    today()
                )
                questions = buildQuestions(dueIds.map { itemsById.getValue(it) }.shuffled())
            }
        }
    }

    fun selectAnswer(index: Int) {
        if (selectedOption != null) return
        selectedOption = index
        val correct = index == currentQuestion.correctIndex
        if (correct) score++
        val itemId = currentQuestion.vocab.id
        viewModelScope.launch { repository.recordAnswer(itemId, correct, today()) }
    }

    fun nextQuestion() {
        if (currentIndex < totalQuestions - 1) {
            currentIndex++
            selectedOption = null
        } else {
            isFinished = true
            if (categoryId != null) {
                viewModelScope.launch { repository.saveQuizResult(categoryId, scorePercent) }
            }
        }
    }

    fun restart() {
        currentIndex = 0
        selectedOption = null
        score = 0
        isFinished = false
    }

    /** Distractors come from the word's own category so the choices stay comparable. */
    private fun buildQuestions(items: List<VocabItem>): List<QuizQuestion> {
        val categoryMeanings = LessonContent.categories
            .flatMap { category -> category.items.map { it.id to category.items.map(VocabItem::english) } }
            .toMap()
        return items.map { vocab ->
            val distractors = categoryMeanings.getValue(vocab.id)
                .filter { it != vocab.english }
                .shuffled()
                .take(3)
            val options = (distractors + vocab.english).shuffled()
            QuizQuestion(
                vocab = vocab,
                options = options,
                correctIndex = options.indexOf(vocab.english)
            )
        }
    }

    private fun today(): Long = LocalDate.now().toEpochDay()

    class Factory(
        private val categoryId: String?,
        private val repository: ProgressRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(QuizViewModel::class.java))
            return QuizViewModel(categoryId, repository) as T
        }
    }
}
