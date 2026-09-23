package com.dialecthub.app.data.model

/**
 * Where one word sits in its spaced-repetition schedule: its Leitner [box]
 * (1-5; higher means better known) and the day it's next due for review,
 * as a [java.time.LocalDate.toEpochDay] value.
 */
data class ReviewState(
    val box: Int,
    val dueEpochDay: Long
) {
    fun isDue(todayEpochDay: Long): Boolean = dueEpochDay <= todayEpochDay
}
