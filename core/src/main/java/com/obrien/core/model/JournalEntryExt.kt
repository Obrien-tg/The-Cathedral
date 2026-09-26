package com.obrien.core.model

/**
 * Extension functions for JournalEntry to compute scores and derived values.
 *
 * These were previously @Ignore fields on the entity, which caused issues with
 * Room serialization and stale data. Using extension functions ensures the score
 * is always computed from the current state.
 */

/**
 * Number of completed pillars for this journal entry.
 */
fun JournalEntry.calculateScore(): Int = listOf(
    morningCompleted,
    schoolCompleted,
    resetCompleted,
    studyCompleted,
    bodyCompleted,
    eveningCompleted
).count { it }

/**
 * Maximum possible score (number of tracked pillars).
 */
fun JournalEntry.maxScore(): Int = 6

/**
 * Completion percentage (0.0 to 1.0).
 */
fun JournalEntry.completionRate(): Float =
    if (maxScore() == 0) 0f else calculateScore().toFloat() / maxScore()

/**
 * Whether all pillars were completed.
 */
fun JournalEntry.isPerfectDay(): Boolean = calculateScore() == maxScore()
