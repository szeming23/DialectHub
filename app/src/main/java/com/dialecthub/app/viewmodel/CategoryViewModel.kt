package com.dialecthub.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dialecthub.app.data.LessonContent
import com.dialecthub.app.data.PronunciationRecorder
import com.dialecthub.app.data.model.Category

/** Flashcards for one category, plus recording and replaying a pronunciation clip per card. */
class CategoryViewModel(
    categoryId: String,
    private val recorder: PronunciationRecorder
) : ViewModel() {

    val category: Category = LessonContent.findCategory(categoryId)

    var recordedItemIds by mutableStateOf(recorder.recordedItemIds())
        private set
    var recordingItemId by mutableStateOf<String?>(null)
        private set
    var playingItemId by mutableStateOf<String?>(null)
        private set

    /** Returns false if the microphone couldn't be opened. */
    fun startRecording(itemId: String): Boolean {
        stopPlayback()
        return try {
            recorder.startRecording(itemId)
            recordingItemId = itemId
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Returns false if the take was too short to keep. */
    fun stopRecording(): Boolean {
        val itemId = recordingItemId ?: return false
        recordingItemId = null
        val saved = recorder.stopRecording()
        if (saved) recordedItemIds = recordedItemIds + itemId
        return saved
    }

    /** Returns false if the clip couldn't be played. */
    fun play(itemId: String): Boolean =
        try {
            recorder.play(itemId, onComplete = { playingItemId = null })
            playingItemId = itemId
            true
        } catch (e: Exception) {
            playingItemId = null
            false
        }

    fun stopPlayback() {
        recorder.stopPlayback()
        playingItemId = null
    }

    fun deleteRecording(itemId: String) {
        stopPlayback()
        recorder.delete(itemId)
        recordedItemIds = recordedItemIds - itemId
    }

    override fun onCleared() {
        recorder.release()
    }

    class Factory(
        private val categoryId: String,
        private val appContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CategoryViewModel::class.java))
            return CategoryViewModel(categoryId, PronunciationRecorder(appContext)) as T
        }
    }
}
