package com.obrien.thecathedral.model

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
        PrimarySource("Physics", "Isaac Newton", "Principia Mathematica", 560),
        PrimarySource("Biology", "Charles Darwin", "On the Origin of Species", 502),
        PrimarySource("Logic", "George Boole", "The Laws of Thought", 424),
        PrimarySource("Computer Science", "Alan Turing", "On Computable Numbers", 36),
        PrimarySource("Economics", "Adam Smith", "The Wealth of Nations", 1200),
        PrimarySource("Ethics", "Aristotle", "Nicomachean Ethics", 340),
        PrimarySource("Epistemology", "René Descartes", "Discourse on Method", 120)
    )
}
