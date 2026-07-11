<div align="center">

<img src="src/com/mathquiz/resources/logo.png" width="120" height="120" style="border-radius: 12px; margin-bottom: 10px;" />

# 🦉 Atelier Arithmetic
### *A Premium Adaptive Math Mastery Desktop Application for Children*

<br/>

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Database](https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![UI](https://img.shields.io/badge/UI-Java%20Swing-5C6BC0?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2B%20Services-2E7D32?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-0277BD?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-37474F?style=for-the-badge)

<br/>

> **"Transforming arithmetic practice from a chore into a daily adventure — one question at a time."**

<br/>

Atelier Arithmetic is a **production-quality, feature-rich Java Swing desktop application** built for children aged 8–17. It transforms standard arithmetic practice into an engaging, self-directed learning experience through adaptive question generation, a persistent progress system, a real-time per-question timer, a comprehensive answer review screen, and a fully interactive guided onboarding tour narrated by **Archie the Owl** — the app's brand mascot.

This project demonstrates strong product thinking, clean software engineering, modern child-friendly UX design, and genuine educational value — far beyond a typical quiz application.

</div>

---

## 📋 Table of Contents

1. [Overview & Vision](#overview--vision)
2. [Key Features by Phase](#key-features-by-phase)
3. [Screenshots & Screen Descriptions](#screenshots--screen-descriptions)
4. [Project Architecture](#project-architecture)
5. [File Structure](#file-structure)
6. [Installation & Running](#installation--running)
7. [Grading & Remarks System](#grading--remarks-system)
8. [Data Persistence & SQLite Architecture](#data-persistence--sqlite-architecture)
9. [Immersive Audio System](#immersive-audio-system)
10. [Global Exception Handling](#global-exception-handling)
11. [Design Philosophy](#design-philosophy)
12. [Technical Highlights](#technical-highlights)
13. [Requirements](#requirements)
14. [License](#license)

---

## Overview & Vision

### The Problem

Most arithmetic quiz applications are **disposable** — you answer questions, see a score, close the app, and remember nothing. They offer no insight into what went wrong, no reason to return tomorrow, and no sense of progress over time. For children in particular, punitive grading language ("BAD", "VERY VERY BAD") actively discourages continued use.

### The Solution

Atelier Arithmetic is positioned not as a quiz app, but as a **personal math training companion**. It:

- Tells children **exactly** which questions they got wrong and what the correct answers were.
- Tracks **how fast** they answered each question, encouraging computational fluency.
- **Saves every session** to a unified relational SQLite database under user profiles to show long-term analytics and streak highlights.
- Uses **encouraging, growth-mindset language** in every piece of feedback.
- Onboards every new user with a **friendly interactive tour** so they immediately feel confident using the full feature set.

The result is an application children *want* to come back to — rather than one that makes them feel judged and want to leave.

---

## Key Features by Phase

Atelier Arithmetic has been fully realized through a rigorous 5-phase roadmap, supplemented by a post-launch database and exception logger overhaul:

| Phase | Title | Key Additions |
|---|---|---|
| **Phase 1** | **Core Foundation** | MVC architecture, live per-question timer, scrollable review tables, initial JSON file storage, and growth-mindset grading. |
| **Phase 2** | **Intelligence Layer** | Custom Java2D bezier line graphs and radar analytics charts, adaptive difficulty prompts, and Smart Practice recommendations. |
| **Phase 3** | **Engagement & Audio** | Persistence streak tracking with flame indicators, 10 unlockable achievement badges, sound chimes, and Dark Mode theme toggle. |
| **Phase 4** | **Bespoke Features** | Multi-user profiles, step-by-step scaffolded hints, PDF/HTML report exports, and high-DPI scaling (125%/150%). |
| **Phase 5** | **Customizer & Tour** | Interactive 10-category quiz engine, custom parent/teacher Quiz Builder, and a comprehensive 19-step guided tour overlay. |
| **Production Upgrade** | **Enterprise Hardening** | Relational **SQLite persistence engine**, global **uncaught exception handler**, layout alignments, and a modular volume settings popup. |

---

## Screenshots & Screen Descriptions

> *The application runs as a native desktop window. Below is a description of each screen.*

### 🏠 Welcome Screen (Personal Dashboard)
Redesigned into a premium 2-column SaaS dashboard:
- **Left Column**: Branding mascot logo, greeting speech bubbles from Archie, and game parameters (question counts, difficulty dropdown, and parent custom builder launch keys) terminating in the prominent gold CTA button `CHOOSE CATEGORY →`.
- **Right Column**: Displays interactive cards:
  - **Daily Quest Tracker**: Launch buttons, 7-day calendar strip matching active challenges, and streak day counts.
  - **Performance Card**: Count summaries of completed sessions and unlocked achievements badges.
  - **Guide Card**: Quick launchers to start smart practice, Parent guides, or replay the onboarding tour.
- **Top Bar controls**: Include a Profile Selector dropdown, Dark Mode toggle button, Help panel guide button, and a **Sound Settings button** which triggers a slider popup for volume levels (100%, 75%, 50%, 25%) and mute controls.

### 🗂️ Category Selection Screen
Ten discipline cards displayed in a 2×5 grid, each with an icon, name, short description, and a `START` button. Categories:
- ➕ **Addition** — Multi-operand summation
- ➖ **Difference** — Multi-operand subtraction (always positive result)
- ✖️ **Multiplication** — Product of 2–3 factors
- ➗ **Division** — Perfect integer division (no remainders)
- 🔀 **Mixed** — Random selection from all core types
- ⭐ **Special** — Compound bracket expressions (BODMAS required)
- 📊 **Fractions** — Solve fractional proportions & percentages
- 📈 **Patterns** — Find the missing numbers in sequences
- ⚖️ **Algebra** — Solve algebraic linear equations for $x$
- 📏 **Measurement** — Convert metric units & estimate areas

*All advanced generators are fully randomized, generating distinct mathematical sequences on every run.*

### 🎮 Game Screen
The active quiz interface. Shows:
- Question counter and progress bar (top)
- Live per-question timer (top right)
- The arithmetic expression in large serif font (centre)
- Step-by-step hints drawer (accessible via `💡 Hint`)
- Answer input field with Enter-key support

### 📊 Results Screen (Performance Dashboard)
Post-session performance report rendered as a 4-card dashboard:
- **Key Metrics Card**: Displays correct question counts, success percentage rates, letter grades with emoji badges, total duration, and average speed (seconds per question).
- **Session Mastery Card**: Highlights your fastest solved expression and Challenge Area (longest question solved) to pinpoint strengths and areas needing focus.
- **Archie's Insights Card**: Archie the Owl offers customized educational remarks and learning tips based on accuracy percentages.
- **Suggested Path Card**: Suggests next steps (e.g. smart practice launcher or next difficulty level step-up prompts) with an interactive button to launch the practice run directly.
- Footer buttons include: `❓ Guide`, `📋 Review Answers`, `📄 Export Report`, and `🔄 PLAY AGAIN`.

### 📈 Analytics Dashboard Screen
A comprehensive view displaying the child's learning statistics over time:
- **Stat Cards Grid**: Redesigned cards utilizing standard `BorderLayout` and large gold icons (with `✏` representing complete questions) presenting Overall Accuracy %, Completed Questions, Average Response Speeds, and Active Streaks.
- **Accuracy Line Trend**: Smooth Bezier curve graph representing the score trajectory of the last 20 sessions.
- **Category Radar Chart**: Enlarged, highly readable 10-spoke radar visualization indicating performance metrics across topics, with safe margins preventing label overlaps.
- **Activity Heatmap**: A `4x7` grid representing active session contributions over the last 28 days.
- **Smart Recommendations & Checklist**: Mutually exclusive list of strengths and weaknesses based on a 70% threshold, custom advice, and daily learning checkbox goals.

---

## Project Architecture

The application follows a strict **Model-View-Controller (MVC)** pattern, enhanced with a **Service layer** and a **Config/Utility** layer. All inter-panel communication is mediated through the `QuizNavigator` interface, ensuring panels are loosely coupled and independently testable.

```
┌─────────────────────────────────────────────────────────────────┐
│                        QuizApp.java                             │
│       (Entry Point + Uncaught Handler + EDT Launch)             │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       QuizFrame.java                            │
│              (Orchestrator — implements QuizNavigator)           │
│  CardLayout: welcome | categories | game | results | review | help │
│  Glass Pane: TourOverlay (transparent spotlight panel)           │
└─────────────────────────────────────────────────────────────────┘
```

---

## File Structure

```
d:/MathQuizApp/
├── README.md               # Complete project documentation
├── run.bat                 # Windows launch script
├── lib/                    # Library dependencies targets folder
│   └── sqlite-jdbc-3.42.0.0.jar
├── bin/                    # Compiled .class and resources target directory
└── src/
    └── com/
        └── mathquiz/
            ├── QuizApp.java        # Main class (EDT entry point)
            ├── config/             # Configuration & Theme engine
            │   ├── AppConfig.java
            │   └── AppTheme.java
            ├── model/              # Domain models (Question, QuizSession)
            │   ├── Question.java
            │   ├── QuestionResult.java
            │   └── QuizSession.java
            ├── resources/          # Brand assets (logo.png)
            │   └── logo.png
            ├── service/            # Core business logic & computations
            │   ├── AchievementService.java
            │   ├── AdaptiveDifficultyEngine.java
            │   ├── AnalyticsService.java
            │   ├── CustomQuizService.java
            │   ├── HintService.java
            │   ├── QuestionGenerator.java
            │   ├── SessionRepository.java
            │   ├── SoundService.java
            │   └── TourManager.java
            └── view/               # Swing GUI panels and tour overlay
                ├── AchievementsPanel.java
                ├── AnalyticsPanel.java
                ├── CategoryPanel.java
                ├── GamePanel.java
                ├── HelpPanel.java
                ├── QuizBuilderPanel.java
                ├── QuizFrame.java
                ├── QuizNavigator.java
                ├── ResultsPanel.java
                ├── ReviewPanel.java
                ├── SmartPracticePanel.java
                ├── TourOverlay.java
                └── WelcomePanel.java
```

---

## Installation & Running

### Prerequisites

- **Java Development Kit (JDK) 17 or higher** installed and available on your system path.
- The `sqlite-jdbc-3.42.0.0.jar` binary placed in the `/lib/` folder (automatically included).

### Execution

Double-click `run.bat` or run the following command in PowerShell/CMD:

```cmd
run.bat
```

The script automatically compiles source files with classpaths, synchronizes resources, and launches the application.

---

## Grading & Remarks System

Atelier Arithmetic uses a child-friendly grading and remarks scale designed to encourage growth mindsets:

| Score Range | Grade | Encouragement Remarks |
|:---:|:---:|---|
| **95% – 100%** | A++ | Outstanding — True Mastery! |
| **85% – 94%** | A+ | Excellent — Impressive Skills! |
| **78% – 84%** | A | Very Good — Strong Performance! |
| **65% – 77%** | B+ | Good — Solid Foundation! |
| **53% – 64%** | B | Progressing — Keep Practicing! |
| **40% – 52%** | C+ | Developing — You're Learning! |
| **33% – 39%** | C | Working Hard — Try Again! |
| **Under 33%** | D | Keep Trying — Every Step Counts! |

---

## Data Persistence & SQLite Architecture

To ensure enterprise-grade stability, data protection, and profile scaling, the application uses an **embedded relational SQLite database** stored at `~/.atelier-arithmetic/atelier_arithmetic.db`.

```
                  ┌──────────────────────┐
                  │       sessions       │
                  ├──────────────────────┤
                  │ (PK) id (Integer)    │◄──────┐
                  │ profile_name (Text)  │       │
                  │ timestamp (Text)     │       │
                  │ ...                  │       │  Foreign Key
                  └──────────────────────┘       │  (Cascading Delete)
                                                 │
                  ┌──────────────────────┐       │
                  │  session_questions   │       │
                  ├──────────────────────┤       │
                  │ (PK) id (Integer)    │       │
                  │ (FK) session_id (Int)├───────┘
                  │ expression (Text)    │
                  │ ...                  │
                  └──────────────────────┘
```

- **Atomic Transactions**: All session metadata and multiple question results are stored inside a single relational transaction block. If an insertion fails, changes are completely rolled back to maintain integrity.
- **Unified Profile Storage**: Rather than splitting files across profiles, all user records are stored in a single database indexed by a `profile_name` column.
- **Cascading Purges**: Purging history via `repository.clear()` utilizes SQLite's standard foreign-key cascades (`ON DELETE CASCADE`) to clean up database tables cleanly.

---

## 🔒 Security, Child Safety & Privacy Compliance (COPPA / GDPR-K)

Atelier Arithmetic implements strict privacy-by-design principles to ensure compliance with the Children's Online Privacy Protection Act (**COPPA**) and **GDPR-K** guidelines for minors:

### 1. Zero PII Data Collection (Data Minimization)
- **Local-First Isolation**: All user profile data, stats, achievements, settings, and learning histories are stored strictly offline locally on the client machine. The application makes zero external network queries, protecting children from data leaks or cloud breaches.
- **COPPA Profile Creation Dialog**: When adding a profile, the user is presented with a privacy directive stating: *"To protect child safety, please do not use your real first or last name!"*
- **Mascot Name Generator**: Children can click the **🎭 Spark Mascot Name** button to generate fun, adjective-animal pseudonym profile names (e.g. *"Calm Dolphin"*, *"Clever Fox"*) without revealing personally identifiable information.

### 2. Secure Local Data Encryption (AES-128)
To prevent local tampering, cheating (unlocking badges or altering score percentages), or unauthorized reading of a child's study records, the application implements AES-128 symmetric encryption:
- **Unique Machine Key Derivation**: Encryption keys are dynamically derived from hardware/OS parameters (`user.name`, `os.name`, `user.home`) processed via a SHA-256 hash. This binds the database to the host machine.
- **Secure SQLite Persistence**: Sensitive fields inside the relational database (profile names, math categories, difficulty ratings, accuracy scores, and custom expression keys) are stored as encrypted Base64 ciphertexts.
- **Secure Preferences**: User options stored inside `config.properties` are fully encrypted.

### 3. Word-Based Math Parental Gates
Administrative controls and data deletion parameters are locked behind a **Parental Gate** to ensure parent/teacher oversight:
- **Math Verification Locks**: Opening the **Quiz Builder**, editing custom lists, or clicking **🗑 Reset History** launches a random text-based arithmetic lock challenge (e.g., *"Parent Verification: What is eight times nine?"* in words). The action proceeds only upon entering the correct mathematical value.

---

## Immersive Audio System

The application features a modular sound synthesizer engine (`SoundService.java`) which preloads audio files into memory for low-latency triggers:
- **Global Instrumentation**: Using `AppTheme.scaleComponentFont()`, hover and click listeners are dynamically attached to *every JButton* inside the application, giving instant physical feedback.
- **Diverse Sound Cues**: Tone triggers include startup fanfares, clicks, hovers, quiz completions, count-down sound checks, correct/incorrect bells, and badge unlock alerts.
- **Settings Popup**: A custom popup panel on the Welcome screen provides independent volume sliders and mute controls, triggering test tones when adjusted.

---

## Global Exception Handling

Atelier Arithmetic is designed to never freeze or crash silently.
- **JVM Uncaught Interceptor**: A global handler registered via `Thread.setDefaultUncaughtExceptionHandler()` catches all uncaught exceptions in worker thread pools and the main Swing Event Dispatch Thread (EDT).
- **Disk File Logging**: When an exception occurs, a clean stack trace is automatically generated and written to `~/.atelier-arithmetic/logs/error.log`.
- **User Alert Dialog**: Triggers a child-friendly error popup window notifying the user of a safe recovery route, preventing frustrating app freezes.

---

## Design Philosophy

1.  **Vibrant & Accessible Styling**: Employs warm typography (Serif headings, SansSerif body), custom rounded card panels with drop-shadow margins, and contrasting HSL-based palettes (including Dark Mode).
2.  **Growth Mindset**: No punitive text or visual triggers. Replaces typical "game-over" red screens with soft alerts and supportive tips from Archie the Owl.
3.  **Parent & Teacher Empowerment**: Includes an embedded Quiz Builder allowing custom mathematical prompts and answer templates to test classroom objectives.

---

## Technical Highlights

### Base64 Embedded Report Export
The report exporter reads the local brand icon `logo.png` dynamically, converts it to an offline-compatible Base64 data URI, and writes it directly inside the generated HTML report file, ensuring zero broken image icons when shared across machines.

---

## License

This project is licensed under the MIT License. Archie the Owl logo artwork copyright Atelier Arithmetic Studio. All rights reserved.
