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
- **Your own pronunciation recordings** -- under each flashcard, record
  someone saying the word (a native-speaking relative, a teacher, a video
  you're copying) and replay it whenever you review that card. One clip
  per word; Re-record replaces it and Delete removes it.
- **Quizzes** -- multiple-choice practice per category, with a results
  screen and a "best score" saved per category.
- **Spaced-repetition review** -- every quiz answer schedules that word
  for review (Leitner boxes: 1, 3, 7, 14, then 30 days; a wrong answer
  starts it over). The Review card on Home quizzes you on whatever is due,
  across all categories, up to 20 words at a time.
- **Progress tracking** -- best score and completion badges, persisted
  on-device (no account, no network required).
- **Light/dark/system theme**, with a "reset all progress" option in
  Settings.
- **Ask Claude** (optional, needs your own Claude API key) -- type an
  English word or phrase and get its Hokkien equivalent as Hanji, Tâi-lô
  and a pinyin-style spelling. The only feature that uses the network.

## Pronunciation recordings

Recording asks for microphone permission the first time you tap Record;
nothing else in the app uses the mic. Clips are saved as AAC (`.m4a`) in
the app's private storage (`files/recordings/<word id>.m4a`), never leave
the device except through Android's own backup, and survive "Reset all
progress". A new take only replaces the old clip once you tap Stop and
save, and takes under half a second are discarded as accidental taps.
Swiping between cards is locked while recording, and leaving the screen
or the app saves whatever was recorded so far.

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
    model/               VocabItem, Category, CategoryProgress, ReviewState, ThemeMode
    LessonContent.kt      Loads and validates assets/lessons.json
    ProgressRepository.kt DataStore-backed progress/review/theme/API-key persistence
    SpacedRepetition.kt   Leitner-box scheduling rules
    ClaudeTranslator.kt   Ask Claude prompt, API call and reply parsing
    PronunciationRecorder.kt  Record/play/delete per-word audio clips
  viewmodel/             Home, Category, Quiz (category + review), Settings and Translate view models
  ui/
    navigation/          NavHost + route definitions
    screens/             Home, Category, Quiz, Settings and Translate screens
    theme/                Color/Type/Theme (Material 3)
  DialectHubApplication.kt
  MainActivity.kt
app/src/main/assets/     lessons.json (all vocabulary)
app/src/test/.../data/   Reply parser, lesson file and review scheduling unit tests
```

No backend and no Room -- vocabulary lives in a bundled JSON file, and
progress, settings and the Claude API key live in Jetpack DataStore
Preferences. This keeps the app simple to build on and
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

## Adding vocabulary

All words live in `app/src/main/assets/lessons.json`, one item per line:

```json
{"id": "food_11", "hanji": "果子", "tailo": "Kué-tsí", "english": "Fruit", "pronunciationHint": "gue-ji"}
```

Add items to an existing category, or add a new category object (`id`,
`titleEn`, `titleHokkien`, `emoji`, `items`). Ids may only use lowercase
letters, digits and `_`. Keep them unique, and never rename an existing
one or reuse it for a different word: saved progress, review schedules
and pronunciation recordings are all keyed by id, so a renamed word
loses them and a reused id inherits the old word's.
Each category needs at least 4 items, since a quiz question shows the
answer plus three others, and no two items in a category may share an
English meaning. `./gradlew testDebugUnitTest` checks all of this, and
the app refuses to start on an invalid file rather than showing a broken
screen.

## Roadmap ideas (deliberately out of scope for v1)

- Bundled native-speaker audio for every word, so learners have a
  reference before recording their own (Android's built-in TTS doesn't
  support Hokkien).
- More categories and a proper lesson sequence with difficulty levels.
- Sentence-building / listening exercises, not just word-level recall.
- Cloud sync / accounts if multi-device progress becomes a priority.
