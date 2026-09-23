package com.dialecthub.app.data

import com.dialecthub.app.data.model.ReviewState
import org.junit.Assert.assertEquals
import org.junit.Test

class SpacedRepetitionTest {

    private val today = 20_000L

    @Test
    fun newWordAnsweredCorrectlyGoesToBoxOneDueTomorrow() {
        assertEquals(ReviewState(box = 1, dueEpochDay = today + 1), SpacedRepetition.next(null, correct = true, today))
    }

    @Test
    fun newWordAnsweredWronglyAlsoStartsInBoxOne() {
        assertEquals(ReviewState(box = 1, dueEpochDay = today + 1), SpacedRepetition.next(null, correct = false, today))
    }

    @Test
    fun dueWordAnsweredCorrectlyMovesUpABox() {
        val due = ReviewState(box = 2, dueEpochDay = today)
        assertEquals(ReviewState(box = 3, dueEpochDay = today + 7), SpacedRepetition.next(due, correct = true, today))
    }

    @Test
    fun topBoxStaysAtTopBox() {
        val due = ReviewState(box = SpacedRepetition.MAX_BOX, dueEpochDay = today - 3)
        assertEquals(
            ReviewState(box = SpacedRepetition.MAX_BOX, dueEpochDay = today + 30),
            SpacedRepetition.next(due, correct = true, today)
        )
    }

    @Test
    fun correctAnswerBeforeDueDateChangesNothing() {
        val notDue = ReviewState(box = 3, dueEpochDay = today + 4)
        assertEquals(notDue, SpacedRepetition.next(notDue, correct = true, today))
    }

    @Test
    fun wrongAnswerResetsToBoxOneEvenBeforeDueDate() {
        val notDue = ReviewState(box = 4, dueEpochDay = today + 10)
        assertEquals(ReviewState(box = 1, dueEpochDay = today + 1), SpacedRepetition.next(notDue, correct = false, today))
    }

    @Test
    fun dueItemsAreMostOverdueFirstThenWeakestBox() {
        val states = mapOf(
            "later" to ReviewState(box = 1, dueEpochDay = today + 1),
            "today_box3" to ReviewState(box = 3, dueEpochDay = today),
            "today_box1" to ReviewState(box = 1, dueEpochDay = today),
            "overdue" to ReviewState(box = 5, dueEpochDay = today - 5)
        )
        assertEquals(
            listOf("overdue", "today_box1", "today_box3"),
            SpacedRepetition.dueItemIds(states, today)
        )
    }

    @Test
    fun dueItemsAreCappedAtLimit() {
        val states = (1..30).associate { "item_$it" to ReviewState(box = 1, dueEpochDay = today - it) }
        assertEquals(SpacedRepetition.MAX_REVIEW_SIZE, SpacedRepetition.dueItemIds(states, today).size)
    }
}
