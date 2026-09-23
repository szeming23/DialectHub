package com.dialecthub.app.data

import com.dialecthub.app.data.model.ReviewState

/**
 * A simple Leitner-box schedule. A word enters the schedule the first time
 * it's answered in any quiz, and each box waits longer than the last before
 * the word comes up for review again.
 */
object SpacedRepetition {

    /** Days until the next review, indexed by box - 1. */
    private val INTERVAL_DAYS = listOf(1L, 3L, 7L, 14L, 30L)

    val MAX_BOX = INTERVAL_DAYS.size

    /** Longest review session, so a long break doesn't produce a 50-word quiz. */
    const val MAX_REVIEW_SIZE = 20

    /**
     * The schedule after answering a word. A correct answer only counts if
     * the word was new or due, so retaking a quiz the same day can't push
     * words out to 30 days; a wrong answer always sends it back to box 1.
     */
    fun next(previous: ReviewState?, correct: Boolean, todayEpochDay: Long): ReviewState {
        if (correct && previous != null && !previous.isDue(todayEpochDay)) return previous
        val box = if (correct) minOf((previous?.box ?: 0) + 1, MAX_BOX) else 1
        return ReviewState(box = box, dueEpochDay = todayEpochDay + INTERVAL_DAYS[box - 1])
    }

    /** Ids of due words, most overdue first (weakest box breaks ties), capped at [limit]. */
    fun dueItemIds(
        states: Map<String, ReviewState>,
        todayEpochDay: Long,
        limit: Int = MAX_REVIEW_SIZE
    ): List<String> =
        states.entries
            .filter { it.value.isDue(todayEpochDay) }
            .sortedWith(compareBy({ it.value.dueEpochDay }, { it.value.box }))
            .take(limit)
            .map { it.key }
}
