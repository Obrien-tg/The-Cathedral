package com.obrien.thelantern.model

import java.time.LocalDate

data class DailyCounsel(
    val id: Int,
    val theme: String,
    val quote: String,
    val author: String,
    val source: String,
    val reflection: String,
    val morningPrompt: String,
    val eveningPrompt: String,
    val exercise: String? = null
)

object DailyCounselData {

    val all: List<DailyCounsel> = listOf(
        DailyCounsel(
            id = 1,
            theme = "Becoming",
            quote = "I am still becoming. Today I show up. Tomorrow I show up again. That is how greatness begins.",
            author = "Lumi",
            source = "My words",
            reflection = "Great things aren't built in a day. They are built by showing up, one day at a time.",
            morningPrompt = "What do I want to get done today?",
            eveningPrompt = "What is one way I showed up today?"
        ),
        DailyCounsel(
            id = 2,
            theme = "Habit",
            quote = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Aristotle",
            source = "Thinking about it",
            reflection = "What you do every day defines who you are. Your small habits build your future.",
            morningPrompt = "What good thing will I do today?",
            eveningPrompt = "Did my choices today help me become better?"
        ),
        DailyCounsel(
            id = 3,
            theme = "Starting",
            quote = "The secret of getting ahead is getting started.",
            author = "Mark Twain",
            source = "Wisdom",
            reflection = "The hardest part of any task is often just beginning. Once you start, the rest follows.",
            morningPrompt = "What is one thing I can start right now?",
            eveningPrompt = "What did I start today that I'm proud of?"
        ),
        DailyCounsel(
            id = 4,
            theme = "Patience",
            quote = "It’s not that I’m so smart, it’s just that I stay with problems longer.",
            author = "Albert Einstein",
            source = "Science",
            reflection = "Struggling with a hard subject is how your brain grows. Don't give up when it gets tough.",
            morningPrompt = "Which 'hard' problem will I stick with today?",
            eveningPrompt = "Where did I show patience with myself today?"
        ),
        DailyCounsel(
            id = 5,
            theme = "Courage",
            quote = "Courage is not the absence of fear, but the triumph over it.",
            author = "Nelson Mandela",
            source = "History",
            reflection = "Being brave doesn't mean you aren't scared. It means you do the right thing even when you are.",
            morningPrompt = "What is one brave thing I might need to do today?",
            eveningPrompt = "When did I choose courage over fear today?"
        ),
        DailyCounsel(
            id = 6,
            theme = "Learning",
            quote = "A person who never made a mistake never tried anything new.",
            author = "Albert Einstein",
            source = "Science",
            reflection = "Mistakes are just proof that you are learning and trying. Wear them with pride.",
            morningPrompt = "What new thing will I try today, even if I might fail?",
            eveningPrompt = "What did a mistake teach me today?"
        ),
        DailyCounsel(
            id = 7,
            theme = "Kindness",
            quote = "Kindness is a language which the deaf can hear and the blind can see.",
            author = "Mark Twain",
            source = "Wisdom",
            reflection = "A small act of kindness can change someone's whole day. It costs nothing but means everything.",
            morningPrompt = "Who can I be kind to today?",
            eveningPrompt = "How did kindness make my day better?"
        ),
        DailyCounsel(
            id = 8,
            theme = "Friendship",
            quote = "The only way to have a friend is to be one.",
            author = "Ralph Waldo Emerson",
            source = "Wisdom",
            reflection = "If you want to be surrounded by good people, start by being a good person yourself.",
            morningPrompt = "How can I be a better friend today?",
            eveningPrompt = "Who was a good friend to me today, and how was I one to them?"
        ),
        DailyCounsel(
            id = 9,
            theme = "Effort",
            quote = "Do what you can, with what you have, where you are.",
            author = "Theodore Roosevelt",
            source = "History",
            reflection = "You don't need perfect conditions to do great work. Just do your best with what's in front of you.",
            morningPrompt = "What is the best I can do with today's tools?",
            eveningPrompt = "Did I give my honest effort today?"
        ),
        DailyCounsel(
            id = 10,
            theme = "Persistence",
            quote = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
            author = "Winston Churchill",
            source = "History",
            reflection = "Wins and losses come and go. The most important thing is that you keep moving forward.",
            morningPrompt = "What will I keep working on today, no matter what?",
            eveningPrompt = "How did I show persistence today?"
        ),
        DailyCounsel(
            id = 11,
            theme = "Character",
            quote = "Character is doing the right thing when nobody is looking.",
            author = "J.C. Watts",
            source = "Wisdom",
            reflection = "Your true self is shown in the quiet moments. Keep your word to yourself.",
            morningPrompt = "What 'right thing' will I do today, even if no one notices?",
            eveningPrompt = "Am I proud of the person I was when I was alone today?"
        ),
        DailyCounsel(
            id = 12,
            theme = "Growth",
            quote = "The beautiful thing about learning is that no one can take it away from you.",
            author = "B.B. King",
            source = "Music",
            reflection = "Every fact you learn and every skill you master becomes a permanent part of your greatness.",
            morningPrompt = "What is one thing I want to master today?",
            eveningPrompt = "What is one new thing I know now that I didn't know this morning?"
        )
    )

    fun forDayOfYear(dayOfYear: Int): DailyCounsel {
        val index = (dayOfYear - 1) % all.size
        return all[index]
    }

    fun today(): DailyCounsel {
        val dayOfYear = LocalDate.now().dayOfYear
        return forDayOfYear(dayOfYear)
    }
}
