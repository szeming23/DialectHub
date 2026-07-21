# DialectHub

A native Android app for learning Hokkien (Bân-lâm-gú), starting simple:
flashcards + multiple-choice quizzes across a handful of everyday topics.
Built with Kotlin and Jetpack Compose, 100% offline.

## Features (v1)

- **5 starter categories** (50 words/phrases total): Greetings, Numbers,
  Family, Food, Common Phrases.
- **Flashcards** -- swipe through each category, tap a card to flip between
  Hanji + Tâi-lô romanization and the English meaning + a plain-English
  pronunciation hint.
- **Quizzes** -- multiple-choice practice per category, with a results
  screen and a "best score" saved per category.
- **Progress tracking** -- best score and completion badges, persisted
  on-device (no account, no network required).
- **Light/dark/system theme**, with a "reset all progress" option in
  Settings.

## Project structure

```
app/src/main/java/com/dialecthub/app/
  data/                  Static lesson content + progress persistence
    model/               VocabItem, Category, CategoryProgress, ThemeMode
    LessonContent.kt      Hardcoded starter vocabulary (Kotlin, not JSON)
    ProgressRepository.kt DataStore-backed progress/theme persistence
  viewmodel/             HomeViewModel, QuizViewModel, SettingsViewModel
  ui/
    navigation/          NavHost + route definitions
    screens/             HomeScreen, CategoryScreen, QuizScreen, SettingsScreen
    theme/                Color/Type/Theme (Material 3)
  DialectHubApplication.kt
  MainActivity.kt
```

No backend, no Room, no JSON parsing -- content lives directly in
`LessonContent.kt` and progress lives in Jetpack DataStore Preferences.
This keeps the app simple to build on and easy to reason about for a v1.

## Building

1. Open the project root in Android Studio (Koala/2024.1 or newer
   recommended) and let it sync -- the Gradle wrapper will download
   Gradle 8.7 automatically the first time.
2. Run on an emulator or device running Android 8.0 (API 26) or newer.

Or from the command line, once you have an Android SDK installed and
`ANDROID_HOME`/`local.properties` configured:

```
./gradlew assembleDebug
./gradlew installDebug
```

> This repository was scaffolded in a sandboxed environment with no
> Android SDK and no network access to Google's Maven/Gradle
> distribution servers, so the build could not be executed end-to-end
> here. The Gradle wrapper JAR was generated from a real local Gradle
> install and its bootstrap behavior was smoke-tested (it correctly
> attempts to download the pinned Gradle 8.7 distribution). Please run
> a full build in Android Studio before relying on this as production
> code, and file/fix any issues that surface -- ordinary Compose/Kotlin
> code, but it hasn't been compiled by a real Android toolchain yet.

## Content notes

Romanization uses **Tâi-lô**, the system promoted by Taiwan's Ministry
of Education. Each entry also has a rough, non-technical
"sounds like" hint for learners who aren't yet reading tone marks.

## Roadmap ideas (deliberately out of scope for v1)

- Real audio pronunciation (recorded native-speaker clips, since
  Android's built-in TTS doesn't support Hokkien).
- Spaced repetition (SRS) instead of simple best-score tracking.
- More categories and a proper lesson sequence with difficulty levels.
- Sentence-building / listening exercises, not just word-level recall.
- Cloud sync / accounts if multi-device progress becomes a priority.
- Move lesson content to a data file (JSON/SQLite) if it grows large
  enough that hardcoding in Kotlin becomes unwieldy.
