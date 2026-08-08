package com.example.thecathedral.model

data class FocusQuote(
    val text: String,
    val author: String,
    val source: String
)

object FocusQuotes {
    val all = listOf(
        FocusQuote(
            "The impediment to action advances action. What stands in the way becomes the way.",
            "Marcus Aurelius",
            "Meditations"
        ),
        FocusQuote(
            "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            "Aristotle",
            "Nicomachean Ethics"
        ),
        FocusQuote(
            "Your visions will become clear only when you can look into your own heart. Who looks outside, dreams; who looks inside, awakes.",
            "Carl Jung",
            "Memories, Dreams, Reflections"
        ),
        FocusQuote(
            "The cave you fear to enter holds the treasure you seek.",
            "Joseph Campbell",
            "The Hero with a Thousand Faces"
        ),
        FocusQuote(
            "No man is free who is not master of himself.",
            "Epictetus",
            "Discourses"
        ),
        FocusQuote(
            "I count him braver who overcomes his desires than him who conquers his enemies; for the hardest victory is over self.",
            "Aristotle",
            "Nicomachean Ethics"
        ),
        FocusQuote(
            "He who has a why to live can bear almost any how.",
            "Friedrich Nietzsche",
            "Twilight of the Idols"
        ),
        FocusQuote(
            "The mind is everything. What you think you become.",
            "Buddha",
            "Dhammapada"
        ),
        FocusQuote(
            "Do not wait to strike till the iron is hot, but make it hot by striking.",
            "William Butler Yeats",
            "The Celtic Twilight"
        ),
        FocusQuote(
            "It is not that I'm so smart, it is just that I stay with problems longer.",
            "Albert Einstein",
            "Letters to Solovine"
        ),
        FocusQuote(
            "The man who moves a mountain begins by carrying away small stones.",
            "Confucius",
            "Analects"
        ),
        FocusQuote(
            "First say to yourself what you would be; and then do what you have to do.",
            "Epictetus",
            "Discourses"
        )
    )
}
