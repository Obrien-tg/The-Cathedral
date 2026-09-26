package com.obrien.thelantern.model

data class FocusQuote(
    val text: String,
    val author: String,
    val source: String
)

object FocusQuotes {
    val all = listOf(
        FocusQuote(
            "The man who moves a mountain begins by carrying away small stones.",
            "Confucius",
            "Wisdom"
        ),
        FocusQuote(
            "Do what you can, with what you have, where you are.",
            "Theodore Roosevelt",
            "History"
        ),
        FocusQuote(
            "It is not that I'm so smart, it is just that I stay with problems longer.",
            "Albert Einstein",
            "Science"
        ),
        FocusQuote(
            "Quality is not an act, it is a habit.",
            "Aristotle",
            "Philosophy"
        ),
        FocusQuote(
            "Concentrate all your thoughts upon the work at hand. The sun's rays do not burn until brought to a focus.",
            "Alexander Graham Bell",
            "Science"
        ),
        FocusQuote(
            "The secret of getting ahead is getting started.",
            "Mark Twain",
            "Wisdom"
        ),
        FocusQuote(
            "Don't watch the clock; do what it does. Keep going.",
            "Sam Levenson",
            "Wisdom"
        ),
        FocusQuote(
            "Focus on being productive instead of busy.",
            "Tim Ferriss",
            "Wisdom"
        ),
        FocusQuote(
            "Start where you are. Use what you have. Do what you can.",
            "Arthur Ashe",
            "Wisdom"
        ),
        FocusQuote(
            "It always seems impossible until it's done.",
            "Nelson Mandela",
            "History"
        )
    )
}
