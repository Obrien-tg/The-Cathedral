package com.obrien.thecathedral.model

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
            theme = "Clarity",
            quote = "You have power over your mind — not outside events. Realize this, and you will find strength.",
            author = "Marcus Aurelius",
            source = "Meditations 8.47",
            reflection = "The greater part of what disturbs us lies not in the world, but in the judgments we form about it. Today, practice distinguishing what is truly yours to command from what is not.",
            morningPrompt = "What is within my control this morning, and what must I release?",
            eveningPrompt = "Where did I waste energy on things outside my control today?",
            exercise = "When irritation arises, pause and name: “This is a judgment, not the thing itself.”"
        ),
        DailyCounsel(
            id = 2,
            theme = "Clarity",
            quote = "It is not things that disturb us, but our interpretation of their significance.",
            author = "Epictetus",
            source = "Enchiridion 5",
            reflection = "Events are neutral until the mind colours them. The work of philosophy begins in the moment of interpretation.",
            morningPrompt = "What story am I telling myself about the day ahead?",
            eveningPrompt = "Which of today’s difficulties were made heavier by my interpretation?"
        ),
        DailyCounsel(
            id = 3,
            theme = "Clarity",
            quote = "The happiness of your life depends upon the quality of your thoughts.",
            author = "Marcus Aurelius",
            source = "Meditations 4.3",
            reflection = "Guard the quality of the inner conversation. A disordered mind cannot produce an ordered life.",
            morningPrompt = "What quality of thought will I deliberately choose today?",
            eveningPrompt = "Did my thoughts today elevate or diminish me?"
        ),
        DailyCounsel(
            id = 4,
            theme = "Duty",
            quote = "Waste no more time arguing about what a good man should be. Be one.",
            author = "Marcus Aurelius",
            source = "Meditations 10.16",
            reflection = "Philosophy that remains only discussion is incomplete. The proof is in the living.",
            morningPrompt = "What single act of character will I perform today without needing recognition?",
            eveningPrompt = "Where did I speak of virtue instead of practicing it?"
        ),
        DailyCounsel(
            id = 5,
            theme = "Duty",
            quote = "First say to yourself what you would be; and then do what you have to do.",
            author = "Epictetus",
            source = "Discourses 3.23",
            reflection = "Identity precedes action. Decide who you are becoming, then let every deed serve that decision.",
            morningPrompt = "Who am I choosing to be today?",
            eveningPrompt = "Did my actions today match the man I claim to be?"
        ),
        DailyCounsel(
            id = 6,
            theme = "Action",
            quote = "Do not wait for the perfect moment. The perfect moment is now.",
            author = "Marcus Aurelius",
            source = "Meditations",
            reflection = "Delay is often a refined form of fear. The path is walked by beginning.",
            morningPrompt = "What am I postponing that I could begin in the next hour?",
            eveningPrompt = "What did I delay today that cost me progress?"
        ),
        DailyCounsel(
            id = 7,
            theme = "Self-Mastery",
            quote = "No man is free who is not master of himself.",
            author = "Epictetus",
            source = "Discourses",
            reflection = "Freedom is not the absence of restraint, but the presence of self-command. The man ruled by impulse is a slave.",
            morningPrompt = "Where is my greatest vulnerability to impulse today?",
            eveningPrompt = "In what moment did I master myself, and where did I fail?"
        ),
        DailyCounsel(
            id = 8,
            theme = "Self-Mastery",
            quote = "The impediment to action advances action. What stands in the way becomes the way.",
            author = "Marcus Aurelius",
            source = "Meditations 5.20",
            reflection = "Obstacles are not interruptions of the path; they are the path itself, properly understood.",
            morningPrompt = "What difficulty am I currently facing that can be turned into fuel?",
            eveningPrompt = "How did today’s obstacles shape me?"
        ),
        DailyCounsel(
            id = 9,
            theme = "Self-Mastery",
            quote = "If you are distressed by anything external, the pain is not due to the thing itself, but to your estimate of it; and this you have the power to revoke at any moment.",
            author = "Marcus Aurelius",
            source = "Meditations 8.47",
            reflection = "You are never without a remedy. The judgment can be withdrawn as quickly as it was formed.",
            morningPrompt = "What external thing still has power over my peace?",
            eveningPrompt = "Did I exercise the power to revoke a harmful judgment today?"
        ),
        DailyCounsel(
            id = 10,
            theme = "Mortality",
            quote = "You could leave life right now. Let that determine what you do and say and think.",
            author = "Marcus Aurelius",
            source = "Meditations 2.11",
            reflection = "Mortality is not morbid; it is clarifying. The awareness of limited time is the great editor of life.",
            morningPrompt = "If this were my last morning, how would I spend it?",
            eveningPrompt = "Did I live today as a man who knows his days are numbered?"
        ),
        DailyCounsel(
            id = 11,
            theme = "Mortality",
            quote = "It is not death that a man should fear, but he should fear never beginning to live.",
            author = "Marcus Aurelius",
            source = "Meditations",
            reflection = "Many die long before their bodies fail — through postponement, distraction, and the refusal to choose.",
            morningPrompt = "In what area of my life am I still refusing to begin?",
            eveningPrompt = "Where did I truly live today, and where did I merely exist?"
        ),
        DailyCounsel(
            id = 12,
            theme = "Amor Fati",
            quote = "Accept the things to which fate binds you, and love the people with whom fate brings you together, but do so with all your heart.",
            author = "Marcus Aurelius",
            source = "Meditations 6.39",
            reflection = "Love of fate is not passive resignation. It is the active decision to work with reality rather than against it.",
            morningPrompt = "What circumstance am I still resisting that I could instead cooperate with?",
            eveningPrompt = "Did I meet today’s events with resistance or with willing participation?"
        ),
        DailyCounsel(
            id = 13,
            theme = "Amor Fati",
            quote = "Do not seek for things to happen the way you want them to; rather, wish that what happens happen the way it happens. Then you will be happy.",
            author = "Epictetus",
            source = "Enchiridion 8",
            reflection = "The demand that reality conform to our preferences is the root of most unnecessary suffering.",
            morningPrompt = "Can I greet whatever comes today as material for virtue?",
            eveningPrompt = "Where did I demand that the world be other than it was?"
        ),
        DailyCounsel(
            id = 14,
            theme = "Virtue",
            quote = "Waste no more time arguing about what a good man should be. Be one.",
            author = "Marcus Aurelius",
            source = "Meditations 10.16",
            reflection = "Character is formed in the small, repeated decisions of ordinary days.",
            morningPrompt = "What virtue will I practice deliberately in the next twelve hours?",
            eveningPrompt = "In what moment was my character tested today, and how did I respond?"
        ),
        DailyCounsel(
            id = 15,
            theme = "Virtue",
            quote = "Be tolerant with others and strict with yourself.",
            author = "Marcus Aurelius",
            source = "Meditations 5.33",
            reflection = "Harshness toward others is often a form of self-avoidance. True discipline begins at home.",
            morningPrompt = "Where am I tempted to judge others more severely than myself?",
            eveningPrompt = "Was I more severe with myself or with others today?"
        ),
        DailyCounsel(
            id = 16,
            theme = "Resilience",
            quote = "The mind adapts and converts to its own purposes the obstacle to our acting.",
            author = "Marcus Aurelius",
            source = "Meditations 5.20",
            reflection = "Every difficulty contains the seed of an equivalent or greater benefit, if the mind is trained to find it.",
            morningPrompt = "What current difficulty can be converted into strength?",
            eveningPrompt = "How did I transform an obstacle today?"
        ),
        DailyCounsel(
            id = 17,
            theme = "Presence",
            quote = "Confine yourself to the present.",
            author = "Marcus Aurelius",
            source = "Meditations 7.29",
            reflection = "The past is gone; the future is not yet. The only place virtue can be practiced is now.",
            morningPrompt = "Where is my mind most likely to wander today — into regret or into anxiety?",
            eveningPrompt = "How present was I for the actual life I lived today?"
        ),
        DailyCounsel(
            id = 18,
            theme = "Service",
            quote = "What is not good for the hive is not good for the bee.",
            author = "Marcus Aurelius",
            source = "Meditations 6.54",
            reflection = "We are made for cooperation. A life turned entirely inward eventually collapses.",
            morningPrompt = "Whom can I serve today without seeking return?",
            eveningPrompt = "Did my actions today contribute to the common good?"
        ),
        DailyCounsel(
            id = 19,
            theme = "Discipline",
            quote = "First learn the meaning of what you say, and then speak.",
            author = "Epictetus",
            source = "Discourses",
            reflection = "Speech without understanding multiplies confusion. Silence is often the more disciplined choice.",
            morningPrompt = "Where am I speaking beyond my knowledge?",
            eveningPrompt = "Did I speak more than was necessary or wise today?"
        ),
        DailyCounsel(
            id = 20,
            theme = "Endurance",
            quote = "Here is a rule to remember in future, when anything tempts you to feel bitter: not “This is misfortune,” but “To bear this worthily is good fortune.”",
            author = "Marcus Aurelius",
            source = "Meditations 4.49",
            reflection = "The quality of a man is revealed not by the absence of hardship, but by the manner of his bearing it.",
            morningPrompt = "What hardship am I currently called to bear with dignity?",
            eveningPrompt = "Did I meet difficulty today with bitterness or with worthiness?"
        ),
        DailyCounsel(
            id = 21,
            theme = "Integrity",
            quote = "If it is not right do not do it; if it is not true do not say it.",
            author = "Marcus Aurelius",
            source = "Meditations 12.17",
            reflection = "The internal compass is simple. Most complexity is a way to negotiate with conscience.",
            morningPrompt = "In what area of my life am I negotiating with the truth?",
            eveningPrompt = "Did I allow any falsehood to pass my lips or any injustice to guide my hand today?"
        ),
        DailyCounsel(
            id = 22,
            theme = "Strength",
            quote = "Curb your desire—don’t set your heart on so many things and you will get what you need.",
            author = "Epictetus",
            source = "Discourses",
            reflection = "Strength is found in the pruning of desires. The more we need, the more we are vulnerable to fate.",
            morningPrompt = "Which of my current 'needs' is actually a preference I can live without?",
            eveningPrompt = "Did I find contentment in what I had, or was I ruled by wanting more?"
        ),
        DailyCounsel(
            id = 23,
            theme = "Fortitude",
            quote = "It’s a disgrace in this life when the soul surrenders before the body does.",
            author = "Marcus Aurelius",
            source = "Meditations 6.29",
            reflection = "Fatigue of the spirit often precedes fatigue of the muscles. Guard the inner flame with vigilance.",
            morningPrompt = "In what task am I most tempted to surrender my will today?",
            eveningPrompt = "Did I hold the line when the Soul grew weary?"
        ),
        DailyCounsel(
            id = 24,
            theme = "Humility",
            quote = "It is impossible for a man to learn what he thinks he already knows.",
            author = "Epictetus",
            source = "Discourses 2.11",
            reflection = "The greatest barrier to growth is the illusion of arrival. Approach every man and every hour as a teacher.",
            morningPrompt = "Whose perspective am I currently ignoring out of pride?",
            eveningPrompt = "What did I learn today by admitting my own ignorance?"
        ),
        DailyCounsel(
            id = 25,
            theme = "Focus",
            quote = "Let all your efforts be directed to something, let them keep that end in view.",
            author = "Seneca",
            source = "On Tranquility of Mind",
            reflection = "Activity without a defined end is merely motion. Ensure your labour is oriented toward the Purpose.",
            morningPrompt = "Which of today's tasks directly serves my primary Purpose?",
            eveningPrompt = "How much of my movement today was merely directionless activity?"
        ),
        DailyCounsel(
            id = 26,
            theme = "Time",
            quote = "We are not given a short life, but we make it short, and we are not ill-supplied but wasteful of it.",
            author = "Seneca",
            source = "On the Brevity of Life",
            reflection = "Time is the only resource that cannot be replenished. We are stingy with our money but reckless with our hours.",
            morningPrompt = "Where will I spend my hours most deliberately today?",
            eveningPrompt = "Which hours did I treat as if they were infinite and worthless?"
        ),
        DailyCounsel(
            id = 27,
            theme = "Simplicity",
            quote = "Ask yourself at every moment, 'Is this necessary?'",
            author = "Marcus Aurelius",
            source = "Meditations 4.24",
            reflection = "Much of what we do and say is not essential. Eliminating the trivial leaves room for the magnificent.",
            morningPrompt = "What is the one thing I must do today, above all others?",
            eveningPrompt = "What did I eliminate today to make room for what matters?"
        ),
        DailyCounsel(
            id = 28,
            theme = "Courage",
            quote = "Sometimes even to live is an act of courage.",
            author = "Seneca",
            source = "Letters to Lucilius",
            reflection = "There are days when the weight of the world is great. Fidelity on these days is the highest form of bravery.",
            morningPrompt = "What internal burden am I called to carry with grace today?",
            eveningPrompt = "Did I find the courage to keep walking the path today?"
        ),
        DailyCounsel(
            id = 29,
            theme = "Kindness",
            quote = "Wherever there is a human being, there is an opportunity for a kindness.",
            author = "Seneca",
            source = "On the Happy Life",
            reflection = "Discipline is the foundation, but kindness is the superstructure. A strong man who is cruel is merely a brittle stone.",
            morningPrompt = "To whom can I show unexpected patience or charity today?",
            eveningPrompt = "Did I use my strength to elevate others or only myself?"
        ),
        DailyCounsel(
            id = 30,
            theme = "Ascent",
            quote = "He who has a why to live can bear almost any how.",
            author = "Friedrich Nietzsche",
            source = "Twilight of the Idols",
            reflection = "The suffering of the path is made meaningful by the peak. Keep the vision of the 'Complete Man' clear in your mind.",
            morningPrompt = "Does my 'Why' burn brighter than my 'How' is heavy?",
            eveningPrompt = "How did my Purpose make today's labour lighter?"
        ),
        DailyCounsel(
            id = 31,
            theme = "Tranquility",
            quote = "The soul becomes dyed with the colour of its thoughts.",
            author = "Marcus Aurelius",
            source = "Meditations 5.16",
            reflection = "You are the sum of your contemplations. Steep your mind in high ideals to produce a high life.",
            morningPrompt = "What beautiful truth will I dwell upon during my walk today?",
            eveningPrompt = "What colour is my soul tonight after the day's reflections?"
        ),
        DailyCounsel(
            id = 32,
            theme = "Fidelity",
            quote = "Well-being is realized by small steps, but is truly no small thing.",
            author = "Zeno of Citium",
            source = "Attributed",
            reflection = "The Cathedral is built one stone at a time. The cumulative power of minor ritual is the engine of transformation.",
            morningPrompt = "Can I commit to the smallness of today's rituals with great love?",
            eveningPrompt = "Which small stone did I lay today that will support the whole structure?"
        ),
        DailyCounsel(
            id = 33,
            theme = "Perspective",
            quote = "Everything we hear is an opinion, not a fact. Everything we see is a perspective, not the truth.",
            author = "Marcus Aurelius",
            source = "Meditations",
            reflection = "The world you see is heavily filtered by your own biases. Seek the objective core behind the subjective noise.",
            morningPrompt = "What bias am I bringing into my work or relationships today?",
            eveningPrompt = "Where did I mistake my perspective for the absolute truth today?"
        ),
        DailyCounsel(
            id = 34,
            theme = "Moderation",
            quote = "Until we have begun to go without them, we fail to realize how unnecessary many things are.",
            author = "Seneca",
            source = "Letters from a Stoic",
            reflection = "Abundance masks dependency. Practice voluntary hardship to rediscover your own sufficiency.",
            morningPrompt = "What comfort will I deliberately forgo today to test my spirit?",
            eveningPrompt = "What did I learn about my own needs through today's restraint?"
        ),
        DailyCounsel(
            id = 35,
            theme = "Patience",
            quote = "No great thing is created suddenly.",
            author = "Epictetus",
            source = "Discourses",
            reflection = "Nature does not hurry, yet everything is accomplished. Trust the slow compounding of your discipline.",
            morningPrompt = "Where am I trying to force a result that requires time?",
            eveningPrompt = "Did I find peace in the process today, or was I anxious for the end?"
        ),
        DailyCounsel(
            id = 36,
            theme = "Leadership",
            quote = "The best revenge is to be unlike him who performed the injury.",
            author = "Marcus Aurelius",
            source = "Meditations 6.6",
            reflection = "To respond to baseness with base behaviour is to be conquered. Respond with the dignity of your own character.",
            morningPrompt = "Who has slighted me, and how can I respond with a virtue they did not show?",
            eveningPrompt = "Did I maintain my own standard when others failed theirs?"
        ),
        DailyCounsel(
            id = 37,
            theme = "Endurance",
            quote = "Fire is the test of gold; adversity, of strong men.",
            author = "Seneca",
            source = "On Providence",
            reflection = "Do not pray for an easy life, but for the strength to endure a difficult one. The heat is what purifies.",
            morningPrompt = "What part of my character is being refined by my current trials?",
            eveningPrompt = "Did I stand firm in the fire today?"
        ),
        DailyCounsel(
            id = 38,
            theme = "Wisdom",
            quote = "As long as you live, keep learning how to live.",
            author = "Seneca",
            source = "Letters from a Stoic",
            reflection = "Life is a craft that requires constant apprenticeship. Never assume you have mastered the art of existing.",
            morningPrompt = "In what aspect of living am I still a novice?",
            eveningPrompt = "What new insight into the art of life did I gain today?"
        ),
        DailyCounsel(
            id = 39,
            theme = "Reason",
            quote = "Your reason is your guide and master.",
            author = "Epictetus",
            source = "Discourses",
            reflection = "The emotions are powerful weather, but Reason is the pilot. Do not let the storm take the wheel.",
            morningPrompt = "Which emotion is most likely to cloud my reason today?",
            eveningPrompt = "Did I follow the Pilot or the Storm today?"
        ),
        DailyCounsel(
            id = 40,
            theme = "Order",
            quote = "Order your soul; reduce your wants; live in charity.",
            author = "Augustine of Hippo",
            source = "De Ordine",
            reflection = "An ordered soul is a reflection of the Eternal Order. Simplicity and Love are the paths to that arrangement.",
            morningPrompt = "How can I bring more Order to my environment and my mind today?",
            eveningPrompt = "Is my soul more ordered tonight than it was at dawn?"
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
