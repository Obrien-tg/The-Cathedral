# The Cathedral Codex

> "I am one of many. But I am the master of my own two hands."

A daily discipline tracker for Android, built with Kotlin and Jetpack Compose.

## What It Does
The Cathedral structures your day into six sacred pillars — from morning ignition to evening sanctuary — and tracks your ritual completion with persistence, real-time awareness, and a monastic aesthetic.

## Features
- **Six Daily Pillars** — Awakening, Forge, Archive, Afternoon Grind, Arena, Sanctuary
- **Real-Time Active Pillar** — Home screen highlights the current pillar based on device time
- **Task Completion Tracking** — Tap to mark rituals complete; persists across restarts
- **Daily Score** — Visual progress indicator showing completed rituals vs. total
- **Focus Mode** — 25-minute Pomodoro timer with rotating philosophical quotes
- **Journal** — 4-pillar scorecard (Techne, Historia, Gymnos, Sophia) + free reflection
- **Philosophy Screen** — Purpose, Mantra, Emergency Protocols, Primary Source tracker
- **Sunflower Charm** — A quiet good-luck symbol woven throughout the app

## Tech Stack
| Layer | Technology |
|---|---|
| **UI** | Jetpack Compose + Material 3 |
| **Navigation** | Type-safe Compose Navigation (Kotlin Serialization) |
| **State** | ViewModel + StateFlow |
| **DI** | Hilt |
| **Persistence** | DataStore Preferences |
| **Time** | java.time (desugared for API 24+) |
| **Build** | Gradle Version Catalogs |

## Build
```bash
./gradlew assembleDebug
```
**Requirements:** Android Studio Ladybug+, JDK 17+, Android SDK 37

## Architecture
```
MainActivity
├── NavHost (type-safe routes)
│   ├── HomeScreen ← ScheduleViewModel
│   ├── FullScheduleScreen ← ScheduleViewModel
│   ├── FocusModeScreen ← ScheduleViewModel
│   ├── JournalScreen ← ScheduleViewModel
│   └── PhilosophyScreen ← ScheduleViewModel
│
ScheduleViewModel
├── ScheduleRepository
│   └── DataStoreManager (persistent prefs)
├── ScheduleData (static schedule source)
└── Time-based state computation
```

## License
MIT — Built by Obrien TG
