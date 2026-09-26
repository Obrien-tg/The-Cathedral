# Formation Apps Monorepo

> "I am still becoming. Today I show up. Tomorrow I show up again."

This repository contains two distinct Android applications built on a shared core engine, designed to turn daily actions into sacred rituals.

## The Apps

### 1. The Cathedral (`:app`)
**Branding:** Monastic Gold & Monastery Black  
**Focus:** Adult discipline and long-term formation.  
**Schedule:** Six sacred pillars from Awakening to Sanctuary.  
**Formation Path:** 17-node constellation spanning months of consistent effort.

### 2. Lumi (`:lantern`)
**Branding:** Pastel Violet, Pink & Mint  
**Focus:** Grade 7 learner formation (South Africa).  
**Schedule:** Structured for school days (07:30 – 13:30) and study resets.  
**Formation Path:** 12-node constellation tuned for growth and kindness.

---

## Shared Core (`:core`)

Both apps run on a unified engine providing:
- **Daily Pillars Engine** — Time-aware scheduling and ritual tracking.
- **Weekly Intentions** — Dynamic schedule reshaping based on current focus.
- **Enhanced Focus Mode** — ADHD-friendly Pomodoro (Deep Work) and Mindfulness sessions.
- **Formation Path Logic** — Lifetime progress calculation and node unlocking.
- **Shared Persistence** — DataStore Preferences and Room (Journaling).

## Features

- **Pillar-Aware Focus Mode** — Deep Work prompts automatically seed from your active schedule block.
- **Lifetime Progress** — No streak shaming; every completion contributes to a durable historical record.
- **Visual Heatmaps** — Track fidelity over months of formation.
- **Dual Visual Identities** — Distinct themes (Gold monastic vs. Pastel "Still Becoming") sharing the same high-performance UI components.

## Tech Stack

| Layer          | Technology                                      |
|----------------|-------------------------------------------------|
| **UI**         | Jetpack Compose + Material 3                    |
| **Navigation** | Type-safe Compose Navigation (Kotlin Serialization) |
| **DI**         | Hilt                                            |
| **Persistence**| DataStore Preferences + Room                    |
| **Architecture**| MVI/MVVM with Clean Domain Use Cases           |

## Build & Run

```bash
# To build The Cathedral
./gradlew :app:assembleDebug

# To build Lumi
./gradlew :lantern:assembleDebug
```

**Requirements:** Android Studio Ladybug+, JDK 17+, Android SDK 37

## Project Structure

```text
The-Cathedral/
├── core/       ← Shared models, focus engine, and data repositories
├── app/        ← The Cathedral app module
└── lantern/    ← Lumi app module
```

## License

MIT — Built by Obrien TG
