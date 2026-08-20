package com.obrien.thelantern.model

data class PrimarySource(
    val field: String,
    val pioneer: String,
    val book: String,
    val totalPages: Int,
    val currentPage: Int = 0,
    val isActive: Boolean = false
)

object PrimarySources {
    val curriculum = listOf(
        PrimarySource("Mathematics", "Patterns", "The Joy of Math", 200),
        PrimarySource("Natural Sciences", "Nature", "The Living World", 250),
        PrimarySource("Social Sciences", "History", "Our Shared Story", 300),
        PrimarySource("Technology", "Invention", "How Things Work", 180),
        PrimarySource("Literature", "Imagination", "Selected Stories", 400),
        PrimarySource("Life Orientation", "Self", "Growing Well", 150)
    )
}
