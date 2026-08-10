# The Cathedral Codex

> "I am one of many. But I am the master of my own two hands."

A daily discipline tracker for Android, built with Kotlin and Jetpack Compose.

## What It Does

The Cathedral structures your day into six sacred pillars — from morning ignition to evening sanctuary — and tracks your ritual completion with persistence, real-time awareness, and a monastic aesthetic.

This is not a productivity app.  
It is a digital sanctuary designed to turn daily actions into sacred rituals.

## Features

- **Six Daily Pillars** — Awakening, Forge, Archive, Afternoon Grind, Arena, Sanctuary
- **Real-Time Active Pillar** — Home screen highlights the current pillar based on device time
- **Task Completion Tracking** — Tap to mark rituals complete; persists across restarts
- **Daily Score** — Visual progress indicator showing completed rituals vs. total
- **Focus Mode** — 25-minute Pomodoro timer with rotating philosophical quotes
- **Journal** — 4-pillar scorecard (Techne, Historia, Gymnos, Sophia) + free reflection
- **Philosophy Screen** — Purpose, Mantra, Emergency Protocols, Primary Source tracker
- **Formation Path (Skill Tree)** — Lifetime constellation of 17 nodes that unlock only through consistent ritual practice
- **Sunflower Charm** — A quiet good-luck symbol woven throughout the app

## The Formation Path

Progress is earned exclusively through **lifetime** ritual consistency (not daily streaks).

| Tier | Theme              | Nodes |
|------|--------------------|-------|
| 0    | Foundation         | Ignition |
| 1    | First Formation    | Deep Work I · The Archive · First Light |
| 2    | Strengthening      | Deep Work II · Living Sources · Physical Fortitude · Evening Vigil |
| 3    | Integration        | The Forge Master · The Chronicler · Embodied Discipline · Quiet Mind |
| 4    | Synthesis          | Builder’s Hand · Scholar’s Compass · Guardian of the Day |
| 5    | Capstone           | Master of Two Hands → The Cathedral Complete |

A node unlocks only when **all** of its parents are fully completed.  
A node is completed only when every requirement (completions + focus sessions + journal days) reaches 100 %.

## Tech Stack

| Layer          | Technology                                      |
|----------------|-------------------------------------------------|
| **UI**         | Jetpack Compose + Material 3                    |
| **Navigation** | Type-safe Compose Navigation (Kotlin Serialization) |
| **State**      | ViewModel + StateFlow                           |
| **DI**         | Hilt                                            |
| **Domain**     | Use-case layer (clean separation)               |
| **Persistence**| DataStore Preferences + Room (journal)          |
| **Time**       | java.time (desugared for API 24+)               |
| **Build**      | Gradle Version Catalogs                         |

## Architecture (current)

```
MainActivity
├── NavHost (type-safe routes)
│   ├── HomeScreen          ← HomeViewModel
│   ├── FocusModeScreen     ← FocusViewModel
│   ├── JournalScreen       ← JournalViewModel
│   ├── PhilosophyScreen    ← PhilosophyViewModel
│   ├── SkillTreeScreen     ← SkillTreeViewModel
│   ├── WeeklyReviewScreen
│   └── SettingsScreen      ← SettingsViewModel
│
Domain Use Cases
├── GetActivePillarUseCase
├── GetCurrentPillarsUseCase
├── GetDailyScoreUseCase
├── GetNextPillarUseCase
├── GetSkillProgressUseCase
└── ToggleRitualUseCase
│
ScheduleRepository → DataStoreManager + JournalDatabase
```

## Build

```bash
./gradlew assembleDebug
```

**Requirements:** Android Studio Ladybug+, JDK 17+, Android SDK 37

## License

MIT — Built by Obrien TG
