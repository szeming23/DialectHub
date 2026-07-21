package com.dialecthub.app.data.model

/**
 * A single Hokkien word or phrase.
 *
 * [tailo] uses the Tâi-lô romanization system (the standard promoted by
 * Taiwan's Ministry of Education). [pronunciationHint] is a rough,
 * English-spelling approximation for learners who aren't yet reading
 * Tâi-lô tone marks.
 */
data class VocabItem(
    val id: String,
    val hanji: String,
    val tailo: String,
    val english: String,
    val pronunciationHint: String
)
