# DialectHub

A native Android app for learning Hokkien (Bân-lâm-gú), starting simple:
flashcards + multiple-choice quizzes across a handful of everyday topics.
Built with Kotlin and Jetpack Compose; everything but the optional
Ask Claude lookup works offline.

## Features (v1)

- **5 starter categories** (50 words/phrases total): Greetings, Numbers,
  Family, Food, Common Phrases.
- **Flashcards** -- swipe through each category, tap a card to flip between
  Hanji + Tâi-lô romanization and the English meaning + a pinyin-style
  pronunciation hint.
- **Quizzes** -- multiple-choice practice per category, with a results
  screen and a "best score" saved per category.
- **Progress tracking** -- best score and completion badges, persisted
  on-device (no account, no network required).
- **Light/dark/system theme**, with a "reset all progress" option in
  Settings.
- **Ask Claude** (optional, needs your own Claude API key) -- type an
  English word or phrase and get its Hokkien equivalent as Hanji, Tâi-lô
  and a pinyin-style spelling. The only feature that uses the network.

## Ask Claude

Paste an API key from console.anthropic.com into Settings; it's stored
only on the device and excluded from Android backups. Each lookup is one
`claude-opus-5` request through the official Anthropic Java SDK
(`data/ClaudeTranslator.kt`), kept small on purpose: a short system prompt,
three few-shot examples that fix the one-line `Hanji|Tâi-lô|pinyin-style`
reply format, input capped at 60 characters, and low effort. Change
`MODEL` in that file to trade quality for cost (e.g. `claude-haiku-4-5`).

## Project structure

```
app/src/main/java/com/dialecthub/app/
  data/                  Lesson content, persistence, Claude lookup
    model/               VocabItem, Category, CategoryProgress, ThemeMode
    LessonContent.kt      Hardcoded starter vocabulary (Kotlin, not JSON)
    ProgressRepository.kt DataStore-backed progress/theme/API-key persistence
    ClaudeTranslator.kt   Ask Claude prompt, API call and reply parsing
  viewmodel/             Home, Quiz, Settings and Translate view models
  ui/
    navigation/          NavHost + route definitions
    screens/             Home, Category, Quiz, Settings and Translate screens
    theme/                Color/Type/Theme (Material 3)
  DialectHubApplication.kt
  MainActivity.kt
app/src/test/.../data/   ClaudeTranslatorTest (reply parser unit tests)
```

No backend, no Room, no JSON parsing -- content lives directly in
`LessonContent.kt`, and progress, settings and the Claude API key live in
Jetpack DataStore Preferences. This keeps the app simple to build on and
easy to reason about for a v1.

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
./gradlew testDebugUnitTest
```

## Content notes

Romanization uses **Tâi-lô**, the system promoted by Taiwan's Ministry
of Education. Each entry also has a pinyin-style respelling (read it like
Hanyu Pinyin: 再會 Tsài-huē → "zai-hue"), with ⁿ for nasal vowels. It
drops tones, and it spells voiced b/g the same as unaspirated p/k, so
Tâi-lô stays the authoritative spelling. The Settings screen explains
how to read it.

## Roadmap ideas (deliberately out of scope for v1)

- Real audio pronunciation (recorded native-speaker clips, since
  Android's built-in TTS doesn't support Hokkien).
- Spaced repetition (SRS) instead of simple best-score tracking.
- More categories and a proper lesson sequence with difficulty levels.
- Sentence-building / listening exercises, not just word-level recall.
- Cloud sync / accounts if multi-device progress becomes a priority.
- Move lesson content to a data file (JSON/SQLite) if it grows large
  enough that hardcoding in Kotlin becomes unwieldy.
