package com.dialecthub.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dialecthub.app.data.LessonContent
import com.dialecthub.app.data.ProgressRepository
import com.dialecthub.app.data.model.VocabItem
import kotlinx.coroutines.launch

data class QuizQuestion(
    val vocab: VocabItem,
    val options: List<String>,
    val correctIndex: Int
)

class QuizViewModel(
    private val categoryId: String,
    private val repository: ProgressRepository
) : ViewModel() {

    val categoryTitle: String = LessonContent.findCategory(categoryId).titleEn

    private val questions: List<QuizQuestion> = buildQuestions(categoryId)
    val totalQuestions: Int = questions.size

    var currentIndex by mutableStateOf(0)
        private set
    var selectedOption by mutableStateOf<Int?>(null)
        private set
    var score by mutableStateOf(0)
        private set
    var isFinished by mutableStateOf(false)
        private set

    val currentQuestion: QuizQuestion get() = questions[currentIndex]
    val scorePercent: Int get() = if (totalQuestions == 0) 0 else (score * 100) / totalQuestions

    fun selectAnswer(index: Int) {
        if (selectedOption != null) return
        selectedOption = index
        if (index == currentQuestion.correctIndex) score++
    }

    fun nextQuestion() {
        if (currentIndex < questions.lastIndex) {
            currentIndex++
            selectedOption = null
        } else {
            isFinished = true
            viewModelScope.launch {
                repository.saveQuizResult(categoryId, scorePercent)
            }
        }
    }

    fun restart() {
        currentIndex = 0
        selectedOption = null
        score = 0
        isFinished = false
    }

    private fun buildQuestions(categoryId: String): List<QuizQuestion> {
        val category = LessonContent.findCategory(categoryId)
        val allMeanings = category.items.map { it.english }
        return category.items.shuffled().map { vocab ->
            val distractors = allMeanings
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

    class Factory(
        private val categoryId: String,
        private val repository: ProgressRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(QuizViewModel::class.java))
            return QuizViewModel(categoryId, repository) as T
        }
    }
}
