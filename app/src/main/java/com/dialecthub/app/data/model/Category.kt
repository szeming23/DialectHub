package com.dialecthub.app.data.model

data class Category(
    val id: String,
    val titleEn: String,
    val titleHokkien: String,
    val emoji: String,
    val items: List<VocabItem>
)
