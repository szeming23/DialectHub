package com.dialecthub.app.data.model

/**
 * A single Hokkien word or phrase.
 *
 * [tailo] uses the Tâi-lô romanization system (the standard promoted by
 * Taiwan's Ministry of Education). [pronunciationHint] respells it the
 * way Hanyu Pinyin would (b/d/g/z for unaspirated, p/t/k/c for aspirated),
 * with ⁿ marking nasal vowels. It carries no tones -- those live in [tailo].
 */
data class VocabItem(
    val id: String,
    val hanji: String,
    val tailo: String,
    val english: String,
    val pronunciationHint: String
)
