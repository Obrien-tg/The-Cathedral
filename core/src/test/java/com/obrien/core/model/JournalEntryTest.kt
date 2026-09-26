package com.obrien.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class JournalEntryTest {

    @Test
    fun `calculateScore returns correct count of completed pillars`() {
        val entry = JournalEntry(
            date = "2024-01-01",
            morningCompleted = true,
            schoolCompleted = false,
            resetCompleted = true,
            studyCompleted = true,
            bodyCompleted = false,
            eveningCompleted = true
        )
        
        assertEquals(4, entry.calculateScore())
    }

    @Test
    fun `calculateScore returns 0 when no pillars completed`() {
        val entry = JournalEntry(date = "2024-01-01")
        assertEquals(0, entry.calculateScore())
    }

    @Test
    fun `calculateScore returns 6 when all pillars completed`() {
        val entry = JournalEntry(
            date = "2024-01-01",
            morningCompleted = true,
            schoolCompleted = true,
            resetCompleted = true,
            studyCompleted = true,
            bodyCompleted = true,
            eveningCompleted = true
        )
        assertEquals(6, entry.calculateScore())
    }
}
