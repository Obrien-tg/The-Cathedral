package com.obrien.thecathedral.data

import com.obrien.core.model.Alarm
import com.obrien.core.model.Pillar
import com.obrien.core.model.WeeklyIntention
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Builds the day's pillars from the weekly declaration: the declared
 * wake/sleep envelope and the protected focus hours. Times are COMPUTED
 * from the envelope — not offset from a canonical day — so no midnight-wrap
 * or string-shift fragility. Task text still belongs to [ScheduleShaper];
 * this layer only decides when the rituals happen and how long they run.
 *
 * Shape of the day (fixed order, elastic evening):
 *   Awakening (30m) -> Forge (focus + theory + commit) -> Archive (90m)
 *   -> Afternoon Grind (150m) -> Arena (120m) -> Sanctuary (rest of day,
 *   ending exactly at the declared sleep / digital sunset).
 */
object DayScheduler {

    private const val AWAKENING_MIN = 30
    private const val THEORY_MIN = 45
    private const val COMMIT_MIN = 15
    private const val ARCHIVE_MIN = 90      // nourishment + primary source + walk
    private const val AFTERNOON_MIN = 150   // admin + bug hunt + community
    private const val ARENA_MIN = 120       // physical + playground
    private const val SANCTUARY_MIN = 90

    private val fmt = DateTimeFormatter.ofPattern("HH:mm")

    sealed interface Result {
        data class Schedule(val pillars: List<Pillar>) : Result
        data class Invalid(val reason: String) : Result
    }

    /** True when the user has declared the structural vows for this week. */
    fun hasStructuralPlan(i: WeeklyIntention): Boolean =
        i.targetWakeTime.isNotBlank() &&
                i.targetSleepTime.isNotBlank() &&
                i.dailyFocusHours > 0f

    /**
     * Checks a declared envelope. Returns a monastic-voiced reason it cannot
     * hold, or null if sound. Blank fields mean "not declared yet" — no error.
     */
    fun validate(wakeStr: String, sleepStr: String, focusHours: Float): String? {
        if (wakeStr.isBlank() || sleepStr.isBlank() || focusHours <= 0f) return null

        val wake = try { LocalTime.parse(wakeStr) } catch (_: Exception) {
            return "The hour of rising is unreadable. Declare it again."
        }
        val sleep = try { LocalTime.parse(sleepStr) } catch (_: Exception) {
            return "The hour of rest is unreadable. Declare it again."
        }
        if (!sleep.isAfter(wake)) return "The day must end after it begins."

        val dayMin = Duration.between(wake, sleep).toMinutes().toInt()
        val focusMin = (focusHours * 60).roundToInt()
        val forgeMin = focusMin + THEORY_MIN + COMMIT_MIN
        val required = AWAKENING_MIN + forgeMin + ARCHIVE_MIN +
                AFTERNOON_MIN + ARENA_MIN + SANCTUARY_MIN

        return if (dayMin < required) {
            val fh = if (focusHours == focusHours.toInt().toFloat())
                focusHours.toInt().toString() else String.format("%.1f", focusHours)
            "A ${fh}h Forge cannot stand inside a ${dayMin / 60}h day. " +
                    "Lengthen the day, or temper the vow."
        } else null
    }

    /** Forge window + Sanctuary end for the ritual screen's live preview. */
    fun preview(wakeStr: String, sleepStr: String, focusHours: Float): Pair<String, String>? {
        if (validate(wakeStr, sleepStr, focusHours) != null) return null
        val wake = LocalTime.parse(wakeStr)
        val sleep = LocalTime.parse(sleepStr)
        val focusMin = (focusHours * 60).roundToInt()
        val forgeStart = wake.plusMinutes(AWAKENING_MIN.toLong())
        val forgeEnd = forgeStart.plusMinutes((focusMin + THEORY_MIN + COMMIT_MIN).toLong())
        return "${forgeStart.format(fmt)} - ${forgeEnd.format(fmt)}" to
                "ends ${sleep.format(fmt)}"
    }

    fun build(intention: WeeklyIntention): Result {
        if (!intention.isActiveForCurrentWeek()) {
            return Result.Schedule(ScheduleShaper.shape(intention))
        }
        validate(intention.targetWakeTime, intention.targetSleepTime, intention.dailyFocusHours)
            ?.let { return Result.Invalid(it) }

        val wake = LocalTime.parse(intention.targetWakeTime)
        val sleep = LocalTime.parse(intention.targetSleepTime)
        val dayMin = Duration.between(wake, sleep).toMinutes().toInt()
        val focusMin = (intention.dailyFocusHours * 60).roundToInt()
        val perBlock = focusMin / 2

        val shaped = ScheduleShaper.shape(intention)
        val shapedById = shaped.associateBy { it.id }

        var cursor = wake
        val out = mutableListOf<Pillar>()

        fun emit(pillarId: String, blocks: List<Pair<String, Int>>) {
            val template = shapedById[pillarId] ?: return
            val byAlarmId = template.alarms.associateBy { it.id }
            val newAlarms = blocks.map { (alarmId, len) ->
                val orig = byAlarmId[alarmId]
                Alarm(
                    id = alarmId,
                    time = cursor,
                    name = orig?.name ?: alarmId,
                    tasks = orig?.tasks.orEmpty()
                ).also { cursor = cursor.plusMinutes(len.toLong()) }
            }
            out += template.copy(
                alarms = newAlarms,
                timeRange = "${newAlarms.first().time.format(fmt)} - ${cursor.format(fmt)}"
            )
        }

        emit("awakening", listOf("ignition" to 15, "grounding" to 15))
        emit("forge", listOf(
            "deep_work_1" to perBlock,
            "theory" to THEORY_MIN,
            "deep_work_2" to perBlock,
            "commit" to COMMIT_MIN
        ))
        emit("archive", listOf("nourishment" to 30, "primary_source" to 35, "peripatetic" to 25))
        emit("afternoon_grind", listOf("admin" to 30, "bug_hunt" to 90, "community" to 30))
        emit("arena", listOf("physical" to 60, "playground" to 60))

        // Sanctuary absorbs the slack so the day ends exactly at sleep.
        val usedMin = Duration.between(wake, cursor).toMinutes().toInt()
        val sanctuaryMin = dayMin - usedMin
        val (recovery, compass, sunset) = when {
            sanctuaryMin >= 150 -> Triple(60, 45, 30)
            sanctuaryMin >= 115 -> Triple(45, 30, 30)
            else -> Triple(30, 20, 25)
        }
        val buffer = (sanctuaryMin - recovery - compass - sunset).coerceAtLeast(0)
        emit("sanctuary", listOf(
            "evening_recovery" to recovery,
            "scholar_compass" to compass,
            "buffer_zone" to buffer,
            "digital_sunset" to sunset
        ))

        return Result.Schedule(out)
    }
}
