<div align="center">

<img src="src/com/mathquiz/resources/logo.png" width="130" height="130" style="border-radius: 16px; margin-bottom: 12px;" />

# 🦉 Atelier Arithmetic
### *A Premium Adaptive Math Mastery Desktop Application for Children*

<br/>

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/Database-SQLite%203.42-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![UI](https://img.shields.io/badge/UI-Java%20Swing-5C6BC0?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2B%20Services-2E7D32?style=for-the-badge)
![Security](https://img.shields.io/badge/Security-AES--128%20Encrypted-B71C1C?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Windows%20Native-37474F?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-0277BD?style=for-the-badge)

<br/>

> **"Transforming arithmetic practice from a chore into a daily adventure — one question at a time."**

<br/>

Atelier Arithmetic is a **production-quality, feature-rich Java Swing desktop application** built for children aged 8–17. It transforms standard arithmetic practice into an engaging, self-directed learning experience through adaptive question generation, encrypted persistent progress, a live per-question timer, comprehensive answer review, a full analytics dashboard, a gamified star reward economy, and a fully interactive guided onboarding tour narrated by **Archie the Owl** — the app's brand mascot.

</div>

---

## 📋 Table of Contents

1. [Overview & Vision](#overview--vision)
2. [Feature Summary](#feature-summary)
3. [Screens & UI Guide](#screens--ui-guide)
4. [Quiz Categories & Question Engine](#quiz-categories--question-engine)
5. [Project Architecture](#project-architecture)
6. [File Structure](#file-structure)
7. [Installation & Running](#installation--running)
8. [Grading & Remarks System](#grading--remarks-system)
9. [Stars Reward Economy & Mascot Shop](#-stars-reward-economy--mascot-shop)
10. [Analytics & Intelligence Layer](#analytics--intelligence-layer)
11. [Data Persistence & SQLite Architecture](#data-persistence--sqlite-architecture)
12. [Security, Child Safety & Privacy (COPPA/GDPR-K)](#-security-child-safety--privacy-coppa--gdpr-k)
13. [Parental Gate System](#-parental-gate-system)
14. [Immersive Audio System](#-immersive-audio-system)
15. [Global Exception Handling & Logging](#-global-exception-handling--logging)
16. [Windows Packaging & Distribution](#-windows-packaging--distribution)
17. [Design Philosophy](#design-philosophy)
18. [Technical Highlights](#technical-highlights)
19. [Requirements](#requirements)
20. [License](#license)

---

## Overview & Vision

### The Problem

Most arithmetic quiz applications are **disposable** — you answer questions, see a score, close the app, and remember nothing. They offer no insight into what went wrong, no reason to return tomorrow, and no sense of progress over time. For children in particular, punitive grading language actively discourages continued use.

### The Solution

Atelier Arithmetic is positioned not as a quiz app, but as a **personal math training companion**. It:

- Tells children **exactly** which questions they got wrong and what the correct answers were via a scrollable review screen.
- Tracks **how fast** they answered each question, encouraging computational fluency.
- **Saves every session** to a relational SQLite database indexed by user profile for long-term analytics.
- Uses **encouraging, growth-mindset language** in every piece of feedback, narrated by Archie the Owl.
- Onboards every new user with a **19-step interactive guided tour** so they immediately feel confident using the full feature set.
- Motivates daily return through **streaks, achievements, and a star economy** that lets children customize Archie.

---

## Feature Summary

Atelier Arithmetic is fully realized across 6 shipped phases plus a comprehensive security, gamification, and distribution layer:

| Phase | Title | Core Deliverables |
|:---:|---|---|
| **Phase 1** | Core Foundation | MVC architecture, live per-question timer, progress bar, scrollable review, growth-mindset grading |
| **Phase 2** | Intelligence Layer | Custom Java2D Bezier line graph, 10-spoke radar chart, 28-day activity heatmap, Smart Practice mode, Adaptive Difficulty Engine |
| **Phase 3** | Engagement & Audio | Streak tracker with 7-day calendar strip, 10 unlockable achievement badges, programmatic sound synthesizer, Dark Mode |
| **Phase 4** | Bespoke Features | Multi-user profiles, step-by-step scaffolded hints, Base64-embedded HTML report export, high-DPI font scaling (100%/125%/150%) |
| **Phase 5** | Customizer & Tour | 10-category adaptive quiz engine, Custom Quiz Builder (parent/teacher), 19-step guided onboarding tour with mascot overlay |
| **Production** | Enterprise Hardening | Relational SQLite persistence with ACID transactions, global JVM uncaught exception handler, AES-128 encryption, volume settings popup |
| **UX & Safety** | Security & Engagement | COPPA-compliant mascot name generator, encrypted config/DB, extended word-based parental gates, **Stars/Coins reward economy**, **12-item Mascot Shop** |
| **Distribution** | Windows Packaging | `jlink` custom minimal JRE, `jpackage` native launcher, WiX MSI installer, EV code signing pipeline, portable ZIP fallback |

---

## Screens & UI Guide

### 🏠 Home Dashboard (Welcome Screen)

A premium 2-column SaaS-style dashboard. All top bar controls and dashboard cards are live and update dynamically on navigation.

**Top Bar** (always visible):
- 👤 **Profile Selector** — dropdown to switch or create profiles, with a COPPA-aware creation dialog
- ⭐ **Star Balance Indicator** — live display of earned stars for the active profile
- 🔊 **Sound Settings Button** — opens volume/mute popup (parental gate protected)
- 🌙 **Dark Mode Toggle** — switches between the warm light and rich dark themes
- 🔠 **Font Scale Toggle** — cycles 100% → 125% → 150% for accessibility

**Left Column:**
- Brand logo + Archie's greeting speech bubble (updates with equipped accessory)
- Question count input and difficulty selector (`Easy` / `Medium` / `Hard`)
- `CHOOSE CATEGORY →` primary CTA button
- Custom Quiz Builder launch buttons (parent/teacher, parental gate protected)

**Right Column Cards (4 total):**
- **Daily Quest Tracker** — date-aware daily challenge launcher, 7-day calendar strip with streak flame indicators
- **Performance & Badges Card** — session count, streak count, and unlocked badge count; links to Analytics and Achievements panels
- **🛍️ Archie's Shop Card** — live star balance and currently equipped accessory name; links to Mascot Shop
- **Guide Card** — launchers for Smart Practice, guided tour replay, and Help panel

---

### 🗂️ Category Selection Screen

Ten discipline cards in a responsive 2×5 grid. Each card displays a large emoji icon, category name, a one-line description, and a `START` button.

| Icon | Category | Description |
|:---:|---|---|
| ➕ | **Addition** | Multi-operand summation (2–4 operands by difficulty) |
| ➖ | **Difference** | Multi-operand subtraction, always positive result |
| ✖️ | **Multiplication** | Product of 2–3 factors |
| ➗ | **Division** | Perfect integer division, no remainders |
| 🔀 | **Mixed** | Random selection across all four core types per question |
| ⭐ | **Special** | Compound BODMAS bracket expressions |
| 📊 | **Fractions** | Fractional parts, percentages, ratios, mixed number conversions |
| 📈 | **Patterns** | Find the missing term in number sequences |
| ⚖️ | **Algebra** | Solve linear equations for *x* |
| 📏 | **Measurement** | Metric conversions, areas, perimeters, volumes |

---

### 🎮 Game Screen (Active Quiz)

The live quiz interface:
- **Progress bar & question counter** (top)
- **Live per-question count-up timer** — ⏱ seconds ticking, recorded per question
- **Large arithmetic expression** displayed in a prominent serif font card
- **💡 Hint drawer** — animated slide-down panel with step-by-step scaffolded hints (context-aware for each category)
- **Answer input field** with Enter-key support
- **Real-time feedback** on submission (correct/wrong with the right answer shown on incorrect)
- **Adaptive Difficulty Banner** — non-intrusive banner suggesting difficulty change after 5 consecutive perfect answers or 3 consecutive wrong answers
- **❓ Help button** — opens the Help panel without losing progress

---

### 📊 Results Screen (Performance Dashboard)

A 4-card post-session performance report:

1. **KPI Metrics Card** — Score (X/N), success %, letter grade, total session time, average response speed (seconds/question), and **⭐ +N Stars Earned!** label with a hover tooltip showing the full reward breakdown
2. **Session Mastery Card** — fastest solved expression with its response time, and the hardest (slowest) question encountered
3. **Archie's Insights Card** — dynamically generated encouraging feedback from Archie based on accuracy tier
4. **Suggested Next Steps Card** — actionable recommendation button (e.g., step-up difficulty or retry category)

**Footer buttons:** `❓ Guide` | `📋 Review Answers` | `📄 Export Report` | `🔄 PLAY AGAIN`

---

### 📋 Review Answers Screen

A scrollable table of every question answered in the session:
- Expression, your answer, correct answer, ✅/❌ indicator, and time taken (ms)
- Sorted in question order for post-session analysis

---

### 📈 Analytics Dashboard

A comprehensive learning analytics view (parental gate protected):

| Component | Description |
|---|---|
| **Stat Cards (×4)** | Overall Accuracy %, Total Questions Solved, Average Response Speed, Longest Streak |
| **Accuracy Trend Chart** | Custom Java2D Bezier curve line graph of last 20 session scores |
| **Category Radar Chart** | 10-spoke radar visualization showing per-category performance |
| **Activity Heatmap** | 4×7 grid of the last 28 active days |
| **Strengths & Weaknesses** | Top 2 best vs. worst categories based on a 70% accuracy threshold |
| **Archie's Recommendations** | Personalized speech-bubble advice based on current performance |
| **Recent Sessions Log** | Last 10 sessions with timestamp, category, grade, and score |
| **Goals Checklist** | Interactive daily learning goals with action buttons |

**Quick actions:** `🔄 Refresh` | `🗑 Reset History` (parental gate) | `← Back`

---

### 🏅 Achievements Panel

10 dynamically computed achievement badges, each with emoji, name, description, and locked/unlocked state:

| Badge | Name | Unlock Condition |
|:---:|---|---|
| 🌱 | First Steps | Completed your very first quiz |
| 👑 | Arithmetic Genius | Scored 100% on any quiz |
| ⚡ | Speed Demon | Answered any question correctly in under 2 seconds |
| 📚 | Math Maven | Completed 10+ quiz sessions |
| ➕ | Addition Ace | Scored 90%+ on an Addition quiz |
| ➖ | Difference Champion | Scored 90%+ on a Difference quiz |
| ✖️ | Multiplication Master | Scored 90%+ on a Multiplication quiz |
| ➗ | Division Wizard | Scored 90%+ on a Division quiz |
| 🔬 | Special Specialist | Scored 80%+ on a Hard Special quiz |
| 🔥 | Consistency Champion | Maintained a 3+ day practice streak |

Unlocking a new badge awards **+15 bonus stars**.

---

### 🛍️ Mascot Shop

12 cosmetic accessories to unlock for Archie the Owl using earned stars. See the [Stars Reward Economy](#-stars-reward-economy--mascot-shop) section for item details.

---

### 🧠 Smart Practice Panel

Analyses session history to identify the child's weakest category and lowest accuracy difficulty. Displays a personalized recommendation card and an instant `Launch Practice` button to start a targeted 10-question session. Shows a friendly empty-state card if no history exists yet.

---

### 🔨 Custom Quiz Builder (Parent/Teacher)

An editable table interface for building custom quiz sessions:
- Add custom expressions with their exact expected numeric answers
- Remove questions from the table
- Name and save the quiz set to disk via `CustomQuizService`
- Load saved quizzes from the Home Screen with a `Load Saved Quiz` button
- Fully protected by the Parental Gate

---

### ❓ Help Panel

An 8-section inline help guide accessible from every screen via the `❓` button. Written in plain, child-friendly language with emoji section headers. The back button returns to the screen the user came from.

---

### 🗺️ Guided Onboarding Tour

A 19-step interactive tour with a translucent `TourOverlay` glass pane that spotlights each UI element, narrated by Archie. Features:
- Auto-launches on first run, skippable at any time
- Can be replayed from the Home Screen Guide Card
- Spotlights each major control with an animated highlight and Archie's mascot speech bubble
- Completion is persisted in `config.properties` (`tourSeen = true`)

---

## Quiz Categories & Question Engine

All 10 categories use the `QuestionGenerator` service to generate **fully randomized, non-repeating questions** seeded per session. Each difficulty level meaningfully changes operand ranges and structural complexity:

### Core Categories (Phase 1)

| Category | Easy | Medium | Hard |
|---|---|---|---|
| **Addition** | 2 operands (10–99) | 3 operands (100–999) | 4 operands (1000–9999) |
| **Difference** | 2 operands, guaranteed positive | 3 operands | 4 operands |
| **Multiplication** | 2 factors (2–12 × 2–12) | 2 larger factors | 3 factors |
| **Division** | Perfect divisors up to 10 | Larger perfect quotients | Multi-step exact division |
| **Mixed** | Randomly picks one of the 4 core types per question | ← | ← |
| **Special** | BODMAS bracket expressions with 1 inner op | Nested expressions | 3-term compound brackets |

### Advanced Categories (Phase 5)

| Category | Easy | Medium | Hard |
|---|---|---|---|
| **Fractions** | Simple fractional parts of integers | Mixed number conversions, ratios | Percentage queries, complex fraction multiplication |
| **Patterns** | Linear progressions (±step) | Alternating steps, progressive increments | Geometric sequences, Fibonacci-like, polynomial `n*(n+1)` |
| **Algebra** | Single-step `x + B = C` | Two-step `Ax + B = C`, `(x+B)/A = C` | Double-sided equations, simultaneous systems |
| **Measurement** | Metric conversions (m→cm, L→mL) | Time calculations, rectangular area/perimeter | Box volumes, multi-unit complex word problems |

### Daily Challenge Mode
The Home Screen's **Daily Quest Tracker** launches a date-seeded 10-question daily challenge. The same questions are generated for all users on the same day (deterministic seed), and completion awards **+8 bonus stars**. Completion state resets each calendar day.

---

## Project Architecture

The application follows a strict **Model-View-Controller (MVC)** pattern, enhanced with a **Service layer** and a **Config/Utility** layer. All inter-panel navigation is mediated through the `QuizNavigator` interface, ensuring panels remain loosely coupled and independently testable.

```
┌─────────────────────────────────────────────────────────────────┐
│                     QuizApp.java (Entry Point)                  │
│   Global uncaught exception handler → logs + friendly dialog    │
│   System property AA font smoothing → setLookAndFeel()          │
│   SwingUtilities.invokeLater(QuizFrame)                         │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   QuizFrame.java (Orchestrator)                 │
│         implements QuizNavigator — zero business logic          │
│  CardLayout:                                                    │
│    welcome │ categories │ game │ results │ review │ help        │
│    analytics │ smart_practice │ achievements │ quiz_builder     │
│    mascot_shop                                                  │
│  Glass Pane: TourOverlay (transparent spotlight panel)          │
└─────────────────────────────────────────────────────────────────┘
         │                               │
         ▼ Services                      ▼ Config / Util
┌──────────────────────┐       ┌─────────────────────────┐
│  QuestionGenerator   │       │  AppConfig (encrypted   │
│  AdaptiveDifficulty  │       │    config.properties)   │
│  SessionRepository   │       │  AppTheme (tokens +     │
│  AnalyticsService    │       │    Dark Mode + Sound    │
│  AchievementService  │       │    instrumentation)     │
│  RewardService       │       │  CryptoHelper (AES-128) │
│  HintService         │       │  IcoConverter           │
│  SoundService        │       └─────────────────────────┘
│  TourManager         │
│  ParentalGate        │
│  CustomQuizService   │
└──────────────────────┘
         │
         ▼ Models
┌──────────────────────┐
│  Question            │
│  QuestionResult      │
│  QuizSession         │
└──────────────────────┘
```

### Key Design Principles
- **QuizNavigator interface**: All panels depend only on this interface — never on `QuizFrame` directly — making each panel independently portable.
- **Single panel instances**: All 11 panels are created once in `QuizFrame` and reused via `CardLayout`, minimizing memory overhead and enabling smooth transitions.
- **Service-Panel separation**: No panel contains business logic. All computation (scoring, grading, reward calculation, hint generation, etc.) lives in dedicated service classes.
- **Encrypted persistence**: Both `config.properties` and all sensitive SQLite columns are encrypted at rest via AES-128.

---

## File Structure

```
MathQuizApp/
├── README.md                        # Project documentation (this file)
├── DEPLOYMENT.md                    # Packaging, signing & distribution guide
├── run.bat                          # Developer compile-and-run script
├── package.ps1                      # Automated Windows build & packaging pipeline
├── sign.ps1                         # EV code signing automation script
├── .gitignore
├── lib/
│   └── sqlite-jdbc-3.42.0.0.jar    # SQLite JDBC driver (only dependency)
├── dist/                            # Generated build outputs (gitignored)
│   ├── AtelierArithmetic/           # jpackage native app-image
│   ├── AtelierArithmetic-1.0.0.msi  # Windows installer (if WiX present)
│   └── AtelierArithmetic-v1.0.0-Portable.zip
└── src/
    └── com/
        └── mathquiz/
            ├── QuizApp.java               # Entry point + global exception handler
            ├── config/
            │   ├── AppConfig.java         # Encrypted preferences (profiles, stars,
            │   │                          #   shop items, sound, dark mode, tour)
            │   └── AppTheme.java          # Semantic color tokens + Dark Mode +
            │                              #   recursive sound instrumentation
            ├── model/
            │   ├── Question.java          # Expression + expected answer
            │   ├── QuestionResult.java    # Per-answer record (time, correct flag)
            │   └── QuizSession.java       # Full session lifecycle & grade logic
            ├── resources/
            │   └── logo.png              # 1024×1024 brand logo (Archie the Owl)
            ├── service/
            │   ├── AchievementService.java      # 10 dynamic badge calculations
            │   ├── AdaptiveDifficultyEngine.java # Live difficulty recommendation
            │   ├── AnalyticsService.java         # Stats, trends, streaks, weaknesses
            │   ├── CustomQuizService.java        # Save/load parent-built quizzes
            │   ├── HintService.java              # Context-aware step-by-step hints
            │   ├── ParentalGate.java             # Word-math challenge lock dialog
            │   ├── QuestionGenerator.java        # All 10 category generators
            │   ├── RewardService.java            # Stars economy engine
            │   ├── SessionRepository.java        # SQLite ACID read/write
            │   ├── SoundService.java             # Programmatic audio synthesizer
            │   └── TourManager.java              # 19-step guided tour controller
            ├── util/
            │   ├── CryptoHelper.java             # AES-128 encrypt/decrypt helper
            │   ├── IcoConverter.java             # PNG → ICO packaging converter
            │   └── JsonHelper.java               # JSON serialization helpers
            └── view/
                ├── AchievementsPanel.java        # Badge gallery screen
                ├── AnalyticsPanel.java           # Full analytics dashboard
                ├── CategoryPanel.java            # 10-category selection grid
                ├── GamePanel.java                # Live quiz interface
                ├── HelpPanel.java                # 8-section help guide
                ├── MascotShopPanel.java          # 12-item cosmetic accessory shop
                ├── QuizBuilderPanel.java         # Custom quiz editor table
                ├── QuizFrame.java                # Main frame + CardLayout orchestrator
                ├── QuizNavigator.java            # Navigation contract interface
                ├── ResultsPanel.java             # 4-card post-quiz dashboard
                ├── ReviewPanel.java              # Scrollable question review table
                ├── SmartPracticePanel.java       # AI-targeted practice launcher
                ├── WelcomePanel.java             # Home dashboard
                └── tour/
                    ├── MascotPainter.java        # Custom Archie mascot renderer
                    ├── TourOverlay.java          # Glass-pane spotlight overlay
                    └── TourStep.java             # Step data model
```

---

## Installation & Running

### 📦 End-User Installation (Windows Native — No Java Required)

Atelier Arithmetic ships as a fully self-contained Windows application bundled with a stripped custom Java runtime:

1. Download `AtelierArithmetic-1.0.0.msi` (or extract `AtelierArithmetic-v1.0.0-Portable.zip`).
2. Run the installer. It will:
   - Install the application and a bundled ~35MB custom JRE (no system Java needed).
   - Create **Desktop** and **Start Menu** shortcuts.
   - Register a clean uninstaller in **Add/Remove Programs**.
3. Launch from the desktop shortcut. No terminal or command line required.

**Silent Installation (classrooms/bulk deployment):**
```cmd
msiexec /i AtelierArithmetic-1.0.0.msi /qn /norestart
```

### 💻 Developer Quick Launch

Requires **JDK 17+** installed and on `PATH`. Clone or download the repository and run:

```cmd
run.bat
```

This compiles all source files with the SQLite classpath, copies logo resources to the binary output, and launches the app.

### 🛠️ Rebuild Native Installer

Requires JDK 21 and [WiX Toolset v3.11+](https://wixtoolset.org/) on `PATH`:

```powershell
powershell -File package.ps1
```

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed packaging, code signing, and distribution instructions.

---

## Grading & Remarks System

Atelier Arithmetic uses a child-friendly grading and remarks scale designed to reinforce a growth mindset. No punitive language is used at any score tier.

| Score Range | Grade | Encouragement |
|:---:|:---:|---|
| 95% – 100% | **A++** | Outstanding — True Mastery! |
| 85% – 94% | **A+** | Excellent — Impressive Skills! |
| 78% – 84% | **A** | Very Good — Strong Performance! |
| 65% – 77% | **B+** | Good — Solid Foundation! |
| 53% – 64% | **B** | Progressing — Keep Practicing! |
| 40% – 52% | **C+** | Developing — You're Learning! |
| 33% – 39% | **C** | Working Hard — Try Again! |
| Under 33% | **D** | Keep Trying — Every Step Counts! |

---

## ⭐ Stars Reward Economy & Mascot Shop

Atelier Arithmetic includes a built-in engagement economy that motivates consistent daily practice. Stars (⭐) are earned automatically after every completed quiz and can be spent in the **Mascot Shop** to unlock cosmetic accessories for Archie the Owl.

### Star Reward Formula

| Reward Source | Stars Earned |
|:---|:---:|
| Base reward (any completed quiz) | +5 |
| Each correct answer | +1 per answer |
| Perfect score (100%) | +10 bonus |
| Grade A+ or A++ (85–99%) | +5 bonus |
| Grade A (78–84%) | +3 bonus |
| Daily Challenge completion | +8 bonus |
| Streak bonus (×2 per day, max +10) | +2 to +10 |
| New achievement badge unlocked | +15 bonus |

The **⭐ N Stars** balance is displayed in the top bar at all times, updated dynamically on each navigation event, and stored encrypted in `config.properties` per profile.

### Mascot Shop — 12 Items

Children spend earned stars to unlock purely cosmetic accessories for Archie the Owl. Items are stored per-profile and persist across restarts.

| Category | Item | Emoji | Price |
|---|---|:---:|:---:|
| Hats | Top Hat | 🎩 | 30 ⭐ |
| Hats | Royal Crown | 👑 | 50 ⭐ |
| Hats | Wizard Hat | 🔮 | 40 ⭐ |
| Hats | Party Hat | 🎉 | 20 ⭐ |
| Glasses | Nerd Glasses | 👓 | 20 ⭐ |
| Glasses | Cool Shades | 😎 | 25 ⭐ |
| Glasses | Star Frames | ⭐ | 35 ⭐ |
| Colors | Golden Archie | ✨ | 60 ⭐ |
| Colors | Rainbow Glow | 🌈 | 80 ⭐ |
| Badges | Scholar Badge | 🎓 | 35 ⭐ |
| Badges | Star Champion | 🌟 | 45 ⭐ |
| Badges | Kind Heart | 💖 | 30 ⭐ |

**Shop mechanics:**
- **Purchase** via a confirmation dialog — balance is checked before deducting.
- **Equip** any owned item — it appears in Archie's greeting tagline on the Home Screen.
- **Unequip** to return Archie to his default appearance.
- Guard against duplicate purchases — the Buy button is permanently replaced with Equip once an item is owned.
- The **Home Screen Shop Card** shows the live balance and equipped item name at a glance.

---

## Analytics & Intelligence Layer

### Smart Practice Mode
Scans all session history to identify the weakest category and lowest accuracy difficulty. Displays a personalized recommendation card. The `Launch Practice` button immediately starts a targeted 10-question session at that exact category/difficulty combination.

### Adaptive Difficulty Engine
During an active quiz session, `AdaptiveDifficultyEngine` evaluates every submitted answer:
- **Upgrade suggestion**: 5 consecutive perfect answers → banner suggests stepping up.
- **Downgrade suggestion**: 3 consecutive wrong answers → banner suggests stepping down.
- A 5-question cooldown suppresses repeated suggestions to avoid interrupting flow.

### Analytics Service
`AnalyticsService` computes:
- Overall accuracy percentage across all sessions
- Per-category average accuracy (used for radar chart)
- Accuracy trend data for the Bezier line chart (last 20 sessions)
- Current and longest practice streaks (date-aware)
- Weakest and strongest category identification

---

## Data Persistence & SQLite Architecture

All session data is stored in an embedded SQLite database at `~/.atelier-arithmetic/atelier_arithmetic.db`.

```
┌──────────────────────────────┐
│           sessions           │
├──────────────────────────────┤
│ id          INTEGER PK AUTO  │◄──────────────┐
│ profile_name  TEXT (encrypted)│               │
│ timestamp     TEXT           │               │ ON DELETE
│ category      TEXT (encrypted)│               │ CASCADE
│ difficulty    TEXT (encrypted)│               │
│ total_questions  INTEGER     │               │
│ correct_answers  INTEGER     │               │
│ percentage    REAL           │               │
│ grade         TEXT (encrypted)│               │
│ duration_ms   INTEGER        │               │
└──────────────────────────────┘               │
                                               │
┌──────────────────────────────┐               │
│       session_questions      │               │
├──────────────────────────────┤               │
│ id          INTEGER PK AUTO  │               │
│ session_id  INTEGER FK ───────────────────────┘
│ expression  TEXT (encrypted) │
│ correct_answer  INTEGER      │
│ user_answer     INTEGER      │
│ correct     INTEGER (0/1)    │
│ time_ms     INTEGER          │
└──────────────────────────────┘
```

**Key guarantees:**
- **ACID Transactions**: Session and all its question records are written atomically. A failure rolls back everything.
- **Cascading Deletes**: Clearing history purges both tables cleanly via `ON DELETE CASCADE`.
- **Profile Isolation**: All rows include an encrypted `profile_name` column — a single database supports unlimited profiles.
- **Foreign Key Enforcement**: `PRAGMA foreign_keys = ON` is executed on every connection.

**User preferences** are stored in `~/.atelier-arithmetic/config.properties`, including:
- Profile list and active profile
- Star balance (per-profile)
- Unlocked and equipped shop items (per-profile)
- Sound enabled/volume, dark mode, font scale
- Tour completion flag, last daily challenge date

---

## 🔐 Security, Child Safety & Privacy (COPPA / GDPR-K)

Atelier Arithmetic implements strict **privacy-by-design** principles:

### Zero Network Activity
The application makes **zero external network requests**. All data is stored strictly offline on the child's machine. There are no analytics trackers, no cloud syncs, and no third-party SDKs.

### AES-128 Encryption at Rest

The `CryptoHelper` class provides AES-128/ECB/PKCS5Padding encryption using a machine-unique key derived from:
```
SHA-256(user.name + os.name + user.home) → first 16 bytes → AES key
```

This binds encrypted data to the specific machine, making copied database files non-readable on other systems.

**What is encrypted:**
- Entire `config.properties` file (serialized as plaintext, then encrypted as Base64)
- `profile_name`, `category`, `difficulty`, `grade` columns in the `sessions` table
- `expression` column in the `session_questions` table

**Graceful fallback:** If decryption fails (e.g., legacy plain-text config), the raw value is used without crashing.

### COPPA-Aware Profile Management
- **Privacy directive** shown at profile creation: *"To protect child safety, please do not use your real first or last name!"*
- **🎭 Spark Mascot Name generator** creates fun adjective-animal pseudonyms (e.g., *"Joyful Penguin"*, *"Clever Fox"*) at the click of a button
- Profile name constraints: no spaces, no special characters, max 15 characters

### Data Minimization
The application collects only what is required for its educational function:
- Quiz performance records (encrypted)
- User preferences (encrypted)
- No photos, audio recordings, geolocation, or biometrics

---

## 🛡️ Parental Gate System

All sensitive settings and data management controls are protected by a **word-based math challenge dialog** to prevent children from accidentally accessing or modifying them.

### Protected Areas

| Feature | Gate Trigger |
|---|---|
| 📊 Analytics Dashboard | On entry from Home Screen |
| 🛠️ Custom Quiz Builder | On launch from Home Screen |
| 🗑 Reset History | On click in Analytics Dashboard |
| 🔊 Sound Settings Popup | On click from top bar |

### Challenge Format

A randomly generated multiplication problem presented entirely in English words:

> *"This screen is for parents or teachers only.*
> *Please solve this math puzzle to proceed:*
> ★ What is **eight** times **seven**? ★"

- Factors are randomly selected between 3 and 12 on each invocation.
- Access is granted only upon entering the exact correct numeric answer.
- Clicking **Cancel** or entering a wrong answer silently denies access.

---

## 🔉 Immersive Audio System

`SoundService` generates all audio **programmatically at runtime** using `javax.sound.sampled` — no audio files are bundled. This achieves zero-dependency audio with full volume control.

### Sound Events

| Event | Sound |
|---|---|
| App launch | Rising warm C-major chord chime |
| Button hover | Quiet high-frequency tick |
| Button click | Brief clean pop |
| Category selected | Bright A5 bell chime |
| Quiz start | Energetic 4-note rising arpeggio |
| Correct answer | C5 → E5 → G5 major triad arpeggio |
| Wrong answer | Descending soft buzz slide |
| Achievement unlocked | Sparkling double high-bell arpeggio |
| Streak milestone | Uplifting quick slide up |
| Timer tick | Quiet wooden clock tick |
| Navigation transition | Quick slide-up chime |

### Global Instrumentation

`AppTheme.scaleComponentFont()` recursively walks the entire Swing component tree and attaches hover and click `MouseListener` instances to every `JButton`. This means **all buttons across all panels** automatically produce audio feedback with zero per-button wiring.

### Sound Settings
A parental-gate-protected popup provides:
- **Master mute toggle**
- **Volume slider** (0–100%) with live preview tone on change

---

## 🚨 Global Exception Handling & Logging

The application is designed to **never freeze or crash silently**.

```java
Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
    // 1. Write full stack trace to ~/.atelier-arithmetic/logs/error.log
    // 2. Show friendly child-facing dialog via SwingUtilities.invokeLater()
});
```

- **Scope**: Catches all uncaught exceptions on every thread, including the Swing EDT.
- **Disk Logging**: Stack trace, thread name, and timestamp are appended to `error.log`.
- **User Dialog**: A friendly popup lets children know to restart, preventing silent hangs:
  > *"Oops! Archie the Owl encountered a tiny calculation hiccup. We have saved a report. You can restart or continue playing!"*

---

## 📦 Windows Packaging & Distribution

See [DEPLOYMENT.md](DEPLOYMENT.md) for the full guide. Summary:

### `package.ps1` — Automated Build Pipeline

1. **Verify tools** — checks `JAVA_HOME`, locates `jlink`, `jpackage`, and WiX
2. **Compile** — `javac` with the SQLite classpath
3. **Icon conversion** — compiles and runs `IcoConverter.java` to translate `logo.png` → `logo.ico`
4. **Custom JRE (`jlink`)** — links a stripped ~35MB runtime from only required modules:
   `java.base, java.desktop, java.sql, java.xml, java.naming, jdk.charsets`
5. **Native launcher (`jpackage`)** — bundles the JRE + JAR into `dist/AtelierArithmetic/AtelierArithmetic.exe`
6. **MSI Installer** (if WiX present) — produces `dist/AtelierArithmetic-1.0.0.msi` with:
   - Start Menu + Desktop shortcuts
   - Directory chooser
   - Automatic uninstaller hook
   - Upgrade UUID `71a2e7c3-3882-4df4-94fa-7e9b422a59cc` for clean version upgrades
7. **Portable ZIP fallback** (if WiX absent) — `dist/AtelierArithmetic-v1.0.0-Portable.zip`

### `sign.ps1` — EV Code Signing Pipeline

- Dynamically locates `signtool.exe` from Windows SDK paths
- Supports EV Hardware USB Tokens (Smart Card / YubiKey) and `.pfx` file signing
- Signs with SHA-256, applies a DigiCert RFC 3161 timestamp
- Runs `signtool verify /pa` to confirm a valid signature after signing

---

## Design Philosophy

1. **Growth Mindset First** — No punitive text or red-screen panic at any score tier. Every grade, including D, carries an encouraging message. Archie's feedback adapts to the child's accuracy without shaming.

2. **Parent & Teacher Empowerment** — The Quiz Builder and Analytics Dashboard give adults visibility and control. The Parental Gate ensures children cannot accidentally modify administrative settings.

3. **Vibrant, Premium Aesthetics** — Warm editorial color palette (`#FAF9F6` parchment, `#B8966E` bronze gold), custom rounded card panels with shadow margins, serif display headings, and a full Dark Mode using a rich graphite-charcoal palette (`#18181C`).

4. **Accessible by Design** — Font scaling at 100%/125%/150% accommodates different visual needs. Emoji labels are paired with `Segoe UI Emoji` font tags in HTML labels to guarantee correct rendering on Windows.

5. **Zero Friction Engagement** — Stars, streaks, and daily challenges create natural return loops without addiction mechanics. All rewards are balanced for educational motivation, not compulsive engagement.

---

## Technical Highlights

### Base64-Embedded Report Export
The HTML report exporter reads `logo.png` at runtime, converts it to a Base64 data URI, and writes it inline into the exported HTML file. Reports are self-contained — they display the branding correctly when shared across machines with no broken image links.

### Seeded Daily Challenge
`QuestionGenerator.generateSeeded(difficulty, category, dateSeed, questionIndex)` accepts a `long` date seed derived from today's date. This guarantees that all children using the app on the same day get the exact same set of daily challenge questions, making the daily challenge shareable and comparable.

### Component-Level Sound Instrumentation
`AppTheme.scaleComponentFont()` uses the `JComponent` client-property API to mark each button as `soundAttached` before adding listeners, preventing duplicate listener registration even when themes are refreshed.

### Custom Java2D Charts
All analytics charts are hand-coded using Java2D `Graphics2D`:
- **Line Chart**: Cubic Bezier curves with gradient fills and axis labels.
- **Radar Chart**: Trigonometric polygon with per-category color fills, anti-aliased.
- **Heatmap**: 28-cell `4×7` grid with opacity-mapped daily activity intensities.

### Programmatic Sound Synthesis
`SoundService` generates all audio in real time using PCM sine-wave generation, avoiding any runtime audio file dependency. Tone slides are implemented using frequency interpolation over sample frames.

---

## Requirements

### End Users (Native Windows Installer)
- **OS**: Windows 10 or later (64-bit)
- **RAM**: 256 MB minimum
- **Storage**: ~80 MB installed
- Java: **None required** — bundled in the installer

### Developers
- **JDK**: 17 or 21 (full JDK, not JRE)
- **OS**: Windows (packaging), any platform (source compilation)
- **Optional for MSI build**: [WiX Toolset v3.11](https://wixtoolset.org/)
- **Optional for code signing**: Windows SDK (`signtool.exe`)

---

## License

This project is licensed under the **MIT License**.

Archie the Owl logo artwork copyright © Atelier Arithmetic Studio. All rights reserved.

---

<div align="center">

Made with ❤️ for young mathematicians everywhere.

**🦉 Atelier Arithmetic** — *Where every question is a step forward.*

</div>
