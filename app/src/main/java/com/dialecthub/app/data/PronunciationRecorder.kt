package com.dialecthub.app.data

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import java.io.File

/**
 * Records and plays back the learner's own pronunciation clips (for example
 * a native speaker saying the word), one AAC file per vocab item in
 * `files/recordings/`. Android's text-to-speech has no Hokkien voice, so
 * these are the app's only audio. Clips live in app-private storage and
 * are included in Android's normal backup.
 *
 * Not thread-safe; call everything from the main thread.
 */
class PronunciationRecorder(private val context: Context) {

    private val directory = File(context.filesDir, "recordings")

    private var recorder: MediaRecorder? = null
    private var recordingItemId: String? = null
    private var recordingStartedAt = 0L
    private var player: MediaPlayer? = null

    fun recordedItemIds(): Set<String> =
        directory.listFiles { file -> file.extension == EXTENSION }
            ?.map { it.nameWithoutExtension }
            ?.toSet()
            .orEmpty()

    /** Throws if the microphone can't be opened (e.g. another app is using it). */
    fun startRecording(itemId: String) {
        stopPlayback()
        cancelRecording()
        directory.mkdirs()
        val output = tempFileFor(itemId)
        val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        try {
            newRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            newRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            newRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            newRecorder.setAudioSamplingRate(44_100)
            newRecorder.setAudioEncodingBitRate(96_000)
            newRecorder.setOutputFile(output.absolutePath)
            newRecorder.prepare()
            newRecorder.start()
        } catch (e: Exception) {
            newRecorder.release()
            output.delete()
            throw e
        }
        recorder = newRecorder
        recordingItemId = itemId
        recordingStartedAt = SystemClock.elapsedRealtime()
    }

    /**
     * Stops and keeps the clip, replacing any earlier one for the same item.
     * Returns false, keeping the earlier clip, if nothing usable was
     * captured, such as a stop tapped straight after start.
     */
    fun stopRecording(): Boolean {
        val activeRecorder = recorder ?: return false
        val itemId = recordingItemId ?: return false
        val longEnough = SystemClock.elapsedRealtime() - recordingStartedAt >= MIN_RECORDING_MS
        recorder = null
        recordingItemId = null

        // stop() throws if no audio was captured yet.
        val stopped = runCatching { activeRecorder.stop() }.isSuccess
        activeRecorder.release()

        val temp = tempFileFor(itemId)
        if (stopped && longEnough && temp.renameTo(fileFor(itemId))) return true
        temp.delete()
        return false
    }

    fun cancelRecording() {
        val activeRecorder = recorder ?: return
        val itemId = recordingItemId
        recorder = null
        recordingItemId = null
        runCatching { activeRecorder.stop() }
        activeRecorder.release()
        if (itemId != null) tempFileFor(itemId).delete()
    }

    /** Throws if the clip can't be played; [onComplete] runs when it finishes on its own. */
    fun play(itemId: String, onComplete: () -> Unit) {
        stopPlayback()
        val newPlayer = MediaPlayer()
        try {
            newPlayer.setDataSource(fileFor(itemId).absolutePath)
            newPlayer.setOnCompletionListener {
                stopPlayback()
                onComplete()
            }
            newPlayer.prepare()
            newPlayer.start()
        } catch (e: Exception) {
            newPlayer.release()
            throw e
        }
        player = newPlayer
    }

    fun stopPlayback() {
        player?.release()
        player = null
    }

    fun delete(itemId: String) {
        fileFor(itemId).delete()
    }

    fun release() {
        cancelRecording()
        stopPlayback()
    }

    private fun fileFor(itemId: String) = File(directory, "$itemId.$EXTENSION")

    // Recorded to a temp file first so a failed or too-short take never
    // overwrites a good clip.
    private fun tempFileFor(itemId: String) = File(directory, "$itemId.$EXTENSION.tmp")

    companion object {
        private const val EXTENSION = "m4a"

        /** Anything shorter is almost certainly an accidental double tap. */
        private const val MIN_RECORDING_MS = 500L
    }
}
