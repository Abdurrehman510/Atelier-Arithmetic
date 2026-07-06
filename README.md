<div align="center">

# 🦉 Atelier Arithmetic
### *A Premium Adaptive Math Mastery Desktop Application for Children*

<br/>

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-5C6BC0?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Architecture-MVC-2E7D32?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-0277BD?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-37474F?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Phase%201%20Complete-43A047?style=for-the-badge)

<br/>

> **"Transforming arithmetic practice from a chore into a daily adventure — one question at a time."**

<br/>

Atelier Arithmetic is a **production-quality, feature-rich Java Swing desktop application** built for children aged 8–14. It transforms basic arithmetic practice into an engaging, self-directed learning experience through adaptive question generation, a persistent progress system, a real-time per-question timer, a comprehensive answer review screen, and a fully interactive guided onboarding tour narrated by **Archie the Owl** — the app's hand-drawn Java2D mascot.

This project demonstrates strong product thinking, clean software engineering, and genuine educational value — far beyond a typical quiz application.

</div>

---

## 📋 Table of Contents

1. [Overview & Vision](#overview--vision)
2. [Key Features](#key-features)
3. [Screenshots & Tour](#screenshots--tour)
4. [Project Architecture](#project-architecture)
5. [File Structure](#file-structure)
6. [Installation & Running](#installation--running)
7. [How to Use the Application](#how-to-use-the-application)
8. [Question Categories & Difficulty Levels](#question-categories--difficulty-levels)
9. [Grading & Remarks System](#grading--remarks-system)
10. [Data Persistence](#data-persistence)
11. [Interactive Guided Tour](#interactive-guided-tour)
12. [Design Philosophy](#design-philosophy)
13. [Roadmap](#roadmap)
14. [Technical Highlights](#technical-highlights)
15. [Requirements](#requirements)
16. [License](#license)

---

## Overview & Vision

### The Problem

Most arithmetic quiz applications are **disposable** — you answer questions, see a score, close the app, and remember nothing. They offer no insight into what went wrong, no reason to return tomorrow, and no sense of progress over time. For children in particular, punitive grading language ("BAD", "VERY VERY BAD") actively discourages continued use.

### The Solution

Atelier Arithmetic is positioned not as a quiz app, but as a **personal math training companion**. It:

- Tells children **exactly** which questions they got wrong and what the correct answers were
- Tracks **how fast** they answered each question, encouraging computational fluency
- **Saves every session** to disk so future analytics can show long-term improvement
- Uses **encouraging, growth-mindset language** in every piece of feedback
- Onboards every new user with a **friendly interactive tour** so they immediately feel confident using the full feature set

The result is an application children *want* to come back to — rather than one that makes them feel judged and want to leave.

---

## Key Features

### ✅ Phase 1 — Delivered

| Feature | Description |
|---|---|
| **🦉 Interactive Guided Tour** | First-launch 12-step spotlight tour narrated by Archie the Owl (drawn in Java2D). Walks children through every screen element with child-friendly language |
| **❓ Persistent Help Panel** | 8-section in-app guide accessible any time via the `❓` button on every screen. Covers all features in plain, encouraging language |
| **⏱ Per-Question Timer** | Live count-up timer (`⏱ 0s`, `⏱ 1s`…) on every question. Records exact response time per question, stored in session history |
| **📋 Answer Review Screen** | Post-quiz scrollable colour-coded table showing every question, the child's answer, the correct answer, result, and time taken |
| **💾 Session Persistence** | Every completed session is automatically saved to `~/.atelier-arithmetic/history.json` with full per-question breakdown |
| **🌱 Growth-Mindset Grading** | All grade remarks replaced with encouraging, age-appropriate language (e.g. "Room to Grow — Try an Easier Level!" instead of "VERY VERY BAD") |
| **🎨 Modular MVC Architecture** | Refactored from a 633-line monolithic frame into 19 focused, loosely-coupled classes across clearly separated packages |
| **6 Question Categories** | Addition, Difference (Subtraction), Multiplication, Division, Mixed, and Special (compound bracket expressions) |
| **3 Difficulty Levels** | Easy, Medium, and Hard — each generating progressively complex arithmetic across all categories |
| **✅ Instant Feedback** | Correct/wrong feedback with the right answer shown immediately after submission |
| **⌨️ Keyboard-First Input** | Press `Enter` to submit answers — no mouse required during active gameplay |
| **🎭 Emoji Grade Badges** | Visual emoji badges (🏆, 🌟, 💪, 👍, 📈, 🔄, 🌱, 🚀) shown on the results screen alongside the letter grade |

### 🔜 Planned (Phases 2–4)

- 📊 **Analytics Dashboard** — custom Java2D line charts and radar charts showing accuracy trends across sessions
- 🧠 **Adaptive Difficulty Engine** — real-time difficulty adjustment based on rolling accuracy window
- 🎯 **Smart Practice Mode** — auto-configured sessions targeting the child's weakest category/difficulty
- 🏆 **Achievement & Badge System** — 10+ unlockable milestones
- 📅 **Daily Challenge Mode** — deterministic seed-based daily quiz
- 🔥 **Streak Tracking** — consecutive daily practice calendar
- 🌙 **Dark Mode** — full theme toggle with persisted preference
- 🔊 **Sound Effects** — subtle audio reinforcement for correct/wrong answers
- 💡 **Practice Mode with Hints** — step-by-step expression decomposition for learning
- 📄 **PDF Export** — branded session report generation

---

## Screenshots & Tour

> *The application runs as a native desktop window. Below is a description of each screen.*

### 🏠 Welcome Screen
The entry point of the application. Children set the **number of questions** (any positive integer) and **difficulty level** (Easy / Medium / Hard). Two utility buttons are always visible: `❓ Guide` opens the Help panel, and `🦉 Tour` replays the full guided tour.

### 🗂️ Category Selection Screen
Six discipline cards displayed in a 2×3 grid, each with an icon, name, short description, and a `START` button. Cards highlight with a gold border on hover. Categories:
- ➕ **Addition** — Multi-operand summation
- ➖ **Difference** — Multi-operand subtraction (always positive result)
- ✖️ **Multiplication** — Product of 2–3 factors
- ➗ **Division** — Perfect integer division (no remainders)
- 🔀 **Mixed** — Random selection from all four core types
- ⭐ **Special** — Compound bracket expressions (BODMAS required)

### 🎮 Game Screen
The active quiz interface. Shows:
- Question counter and progress bar (top)
- Live per-question timer (top right)
- The arithmetic expression in large serif font (centre)
- Answer input field with Enter-key support
- Instant ✅/❌ feedback after submission
- `NEXT QUESTION →` button to advance

### 📊 Results Screen
Post-session performance report:
- Correct answers count and success percentage
- Letter grade with emoji badge
- Encouraging remarks (growth-mindset language)
- Custom SmilePanel — an animated face drawn in Java2D that smiles, frowns, or stays neutral based on the grade
- `📋 Review Answers` and `🔄 PLAY AGAIN` buttons

### 📋 Answer Review Screen
A scrollable table with one row per question:
- 🟩 **Green rows** = correctly answered
- 🟥 **Red rows** = incorrectly answered
- Columns: `#` · `Expression` · `Your Answer` · `Correct Answer` · `Result` · `Time`

### ❓ Help Panel
A fully scrollable guide with 8 sections:
1. How to Start a Quiz
2. Understanding the Categories
3. What Do the Grades Mean?
4. About the Timer
5. Reviewing Your Answers
6. Your Progress is Saved
7. Tips to Score Higher
8. About Archie the Owl

---

## Project Architecture

The application follows a strict **Model-View-Controller (MVC)** pattern, enhanced with a **Service layer** and a **Config/Utility** layer. All inter-panel communication is mediated through the `QuizNavigator` interface, ensuring panels are loosely coupled and independently testable.

```
┌─────────────────────────────────────────────────────────────────┐
│                        QuizApp.java                             │
│                    (Entry Point + EDT Launch)                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       QuizFrame.java                            │
│              (Orchestrator — implements QuizNavigator)           │
│  CardLayout: welcome | categories | game | results | review | help │
│  Glass Pane: TourOverlay (transparent spotlight panel)           │
└──┬───────────┬──────────┬──────────┬──────────┬────────────────┘
   │           │          │          │          │
   ▼           ▼          ▼          ▼          ▼
Welcome  Category    Game       Results   Review / Help
Panel    Panel       Panel      Panel     Panels
   │                  │           │
   │              GamePanel   ResultsPanel
   │              - Timer          - SmilePanel
   │              - QuizSession    - Grade badges
   │
   ▼
AppConfig ←→ TourManager ←→ TourOverlay
SessionRepository              MascotPainter
JsonHelper                     TourStep
```

### Layer Responsibilities

| Layer | Package | Responsibility |
|---|---|---|
| **Entry Point** | `com.mathquiz` | EDT launch, L&F setup, anti-aliasing hints |
| **Config** | `com.mathquiz.config` | User preferences (Properties file), app data directory |
| **Model** | `com.mathquiz.model` | Immutable data classes: Question, QuizSession, QuestionResult |
| **Service** | `com.mathquiz.service` | Business logic: QuestionGenerator, SessionRepository, TourManager |
| **View** | `com.mathquiz.view` | All Swing UI: 6 screen panels + QuizNavigator interface |
| **Tour** | `com.mathquiz.view.tour` | Glass-pane overlay: TourOverlay, TourStep, MascotPainter |
| **Util** | `com.mathquiz.util` | Zero-dependency JSON serialization/deserialization helper |

---

## File Structure

```
MathQuizApp/
│
├── README.md                          # This file — project documentation
├── run.bat                            # Windows double-click launcher (compiles + runs)
│
├── bin/                               # Compiled .class files (auto-generated)
│
└── src/
    └── com/
        └── mathquiz/
            │
            ├── QuizApp.java           # 🚀 Application entry point
            │
            ├── config/
            │   └── AppConfig.java     # User preferences (tour seen, sound toggle)
            │                          #   → Stored at: ~/.atelier-arithmetic/config.properties
            │
            ├── model/
            │   ├── Question.java      # Single question: expression + correct answer
            │   ├── QuizSession.java   # Active session: score, timing, results list, grade
            │   └── QuestionResult.java # Per-question outcome: answers, correctness, time
            │
            ├── service/
            │   ├── QuestionGenerator.java  # Arithmetic generator (6 categories × 3 levels)
            │   ├── SessionRepository.java  # JSON read/write of session history
            │   └── TourManager.java        # 12-step tour lifecycle and step control
            │
            ├── util/
            │   └── JsonHelper.java    # Zero-dependency JSON builder + targeted parser
            │
            └── view/
                ├── QuizNavigator.java # Interface: panel→frame navigation contract
                ├── QuizFrame.java     # Lightweight orchestrator (CardLayout + glass pane)
                ├── WelcomePanel.java  # Screen 1: question count + difficulty setup
                ├── CategoryPanel.java # Screen 2: 6 discipline cards (hover effects)
                ├── GamePanel.java     # Screen 3: question display + timer + answer input
                ├── ResultsPanel.java  # Screen 4: grade, score, remarks, SmilePanel
                ├── ReviewPanel.java   # Screen 5: colour-coded per-question table
                ├── HelpPanel.java     # Screen 6: 8-section persistent guide
                ├── SmilePanel.java    # Custom Java2D face graphic (happy/neutral/sad)
                │
                └── tour/
                    ├── TourStep.java      # Data class: target component, message, position
                    ├── MascotPainter.java # Archie the Owl — 100% Java2D, no image files
                    └── TourOverlay.java   # Glass pane: dark mask + spotlight + bubble
```

---

## Installation & Running

### Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| **JDK** | 8 or higher | Download from [adoptium.net](https://adoptium.net) if needed |
| **OS** | Windows / macOS / Linux | `run.bat` is Windows-only; manual commands work everywhere |

### ▶ Windows — Quick Launch (Recommended)

Simply **double-click `run.bat`** in the project root. It will:
1. Create the `bin/` directory if missing
2. Compile all source files automatically via `-sourcepath`
3. Launch the application immediately

```bat
run.bat
```

### ▶ Manual Build & Run (Any OS)

Open a terminal in the project root directory:

```bash
# Step 1 — Compile (from project root)
javac -sourcepath src src/com/mathquiz/QuizApp.java -d bin

# Step 2 — Run
java -cp bin com.mathquiz.QuizApp
```

### ▶ macOS / Linux One-liner

```bash
mkdir -p bin && javac -sourcepath src src/com/mathquiz/QuizApp.java -d bin && java -cp bin com.mathquiz.QuizApp
```

> **Note:** No external libraries or build tools (Maven, Gradle) are required. The entire project uses only the Java Standard Library.

---

## How to Use the Application

### Step-by-Step Walkthrough

**1. First Launch — Guided Tour**
On your very first run, Archie the Owl will automatically appear and walk you through every feature of the app. Each step highlights a specific UI element with a glowing spotlight. Click `NEXT →` to advance or `SKIP TOUR` to dismiss. You can replay the tour any time via the `🦉 Tour` button.

**2. Configure Your Quiz**
- Set the **number of questions** (e.g. `5`, `10`, `20`)
- Choose a **difficulty**: `Easy` · `Medium` · `Hard`
- Click **`CHOOSE CATEGORY →`**

**3. Select a Category**
Click any of the six discipline cards to immediately begin a quiz in that topic.

**4. Answer Questions**
- Read the arithmetic expression displayed in large text
- Type your integer answer in the input field
- Press **`Enter`** or click **`SUBMIT ANSWER`**
- Feedback is shown instantly: `✅ Correct! Great job!` or `❌ Wrong. The correct answer was: X`
- Click **`NEXT QUESTION →`** (or press Enter again) to continue

**5. View Your Results**
After all questions are answered, the Results screen shows your grade, score percentage, and an encouraging remark. The SmilePanel graphic updates its expression based on your grade.

**6. Review Your Answers**
Click **`📋 Review Answers`** to see a full colour-coded breakdown of every question — ideal for learning from mistakes.

**7. Play Again**
Click **`🔄 PLAY AGAIN`** to return to the Welcome screen and start a new session.

---

## Question Categories & Difficulty Levels

### Category Details

| Category | Easy Example | Medium Example | Hard Example |
|---|---|---|---|
| **Addition** | `47 + 83` | `234 + 567 + 891` | `4821 + 6304 + 743 + 512` |
| **Difference** | `72 - 31` | `856 - 204 - 117` | `7493 - 1820 - 953 - 441` |
| **Multiplication** | `34 × 7` | `45 × 6 × 3` | `382 × 7 × 4` |
| **Division** | `56 / 8` | `(480 / 6) / 5` | `3264 / 34` |
| **Mixed** | Random of above | Random of above | Random of above |
| **Special** | `(6 + 4) × (9 - 3)` | `(48 / 8) + (35 / 7)` | `(14 × 6) / (3 × 4)` |

### Difficulty Scaling

| Level | Number Range | Operands | Complexity |
|---|---|---|---|
| **Easy** | 2-digit numbers | 2 | Single operation |
| **Medium** | 3-digit numbers | 2–3 | Multi-operand or nested |
| **Hard** | 3–4 digit numbers | 3–4 | Multi-operand or large-scale |

### Special Category — Compound Expression Types

The Special category generates 5 types of bracket expressions, all with exact integer results:

1. `(A + B) × (C − D)`
2. `(A − B) / (C + D)` — perfect division guaranteed
3. `(A / B) + (C / D)` — both divisions exact
4. `(A × B) / (C × D)` — exact quotient
5. `(A / B) − (C × D)` — positive result guaranteed

---

## Grading & Remarks System

### Grade Table

| Score Range | Letter Grade | Emoji | Remark |
|---|---|---|---|
| 95 – 100% | **A++** | 🏆 | Outstanding — True Mastery! |
| 85 – 94% | **A+** | 🌟 | Excellent — Impressive Skills! |
| 78 – 84% | **A** | 💪 | Very Good — Strong Performance! |
| 65 – 77% | **B+** | 👍 | Good — Solid Foundation! |
| 53 – 64% | **B** | 📈 | Progressing — Keep Practicing! |
| 40 – 52% | **C+** | 🔄 | Developing — You're Learning! |
| 33 – 39% | **C** | 🌱 | Getting Started — Stay Curious! |
| 0 – 32% | **D** | 🚀 | Room to Grow — Try an Easier Level! |

### Design Rationale

The grading language follows **growth-mindset educational principles**:
- Every remark acknowledges effort and frames low scores as a starting point, not a failure
- No punitive or demoralizing labels
- The lowest grade ("Room to Grow") explicitly suggests a constructive next action
- Emoji badges provide an immediate, visually engaging signal appropriate for ages 8–14

---

## Data Persistence

### Session History

Every completed quiz session is automatically saved to:

```
~/.atelier-arithmetic/history.json
```

On Windows this resolves to:

```
C:\Users\<YourName>\.atelier-arithmetic\history.json
```

The file is human-readable JSON. Each entry contains full session metadata and a per-question breakdown:

```json
[
  {
    "timestamp": "2026-07-06T18:30:00",
    "category": "Special",
    "difficulty": "Hard",
    "totalQuestions": 10,
    "correctAnswers": 8,
    "percentage": 80.0,
    "grade": "A+",
    "durationMs": 143200,
    "questions": [
      {
        "expression": "(45 + 12) * (18 - 14)",
        "correctAnswer": 228,
        "userAnswer": 228,
        "correct": true,
        "timeMs": 11400
      },
      {
        "expression": "(480 / 6) + (35 / 7)",
        "correctAnswer": 85,
        "userAnswer": 90,
        "correct": false,
        "timeMs": 23700
      }
    ]
  }
]
```

### User Preferences

User preferences are stored in a separate Properties file:

```
~/.atelier-arithmetic/config.properties
```

Currently tracked keys:

| Key | Type | Default | Description |
|---|---|---|---|
| `tourSeen` | boolean | `false` | Whether the first-launch guided tour has been completed |
| `soundEnabled` | boolean | `true` | Sound effects toggle (used in Phase 3) |

---

## Interactive Guided Tour

### Overview

On the very first launch of the application, **Archie the Owl** automatically appears and guides the child through every screen and feature in a 12-step interactive tour.

### How It Works — Technical Detail

The tour is implemented as a **JFrame glass pane overlay** (`TourOverlay.java`). When active:

1. A dark semi-transparent mask (`rgba(0,0,0,0.69)`) is painted over the entire screen using Java2D `Area` subtraction
2. A rounded-rectangle **spotlight hole** is cut out around the target component, with a glowing gold border
3. A **speech bubble** appears with word-wrapped text in a warm off-white card
4. **Archie the Owl** is drawn beside the bubble in pure Java2D — no image files, no external assets
5. `NEXT →` and `SKIP TOUR` buttons inside the overlay handle advancement

The overlay intercepts all mouse events to prevent accidental interaction with the underlying UI, while the NEXT and SKIP buttons remain fully clickable as embedded Swing components.

### Archie the Owl — Java2D Rendering

Archie is rendered entirely by `MascotPainter.java` using `Graphics2D` primitives:

- **Body**: gradient-filled oval (amber to warm brown)
- **Ear tufts**: rotated triangles
- **Eyes**: white sclera, dark iris, pupil, highlight dot
- **Beak**: orange equilateral triangle
- **Wings**: semi-transparent ovals
- **Feet**: Java2D line segments

Four facial expressions adapt to context:

| Expression | When Used |
|---|---|
| `WAVING` | Introduction / farewell steps |
| `HAPPY` | Positive feedback, confirmation |
| `EXCITED` | Achievement / completion steps |
| `THINKING` | Concept explanation steps |
| `POINTING` | Directing attention to a UI element |

### The 12 Tour Steps

| Step | Screen | Element Highlighted | Message Summary |
|---|---|---|---|
| 1 | Welcome | *(whole screen)* | Archie introduces himself |
| 2 | Welcome | Question count field | Explains how to set question quantity |
| 3 | Welcome | Difficulty selector | Explains Easy / Medium / Hard |
| 4 | Welcome | Start button | Prompts to choose a category |
| 5 | Categories | Category grid | Explains all 6 disciplines |
| 6 | Game | Expression label | Explains how to read the puzzle |
| 7 | Game | Answer field | Tells child to type their answer |
| 8 | Game | Submit button | Explains Enter key shortcut |
| 9 | Game | Progress bar | Explains the progress indicator |
| 10 | Game | Feedback label | Explains correct/wrong feedback |
| 11 | Results | Grade label | Explains the grading system |
| 12 | Results | Restart button | Farewell + Help button reminder |

### Replaying the Tour

The tour can be replayed at any time by clicking the **`🦉 Tour`** button on the Welcome screen. The completion state is stored in `config.properties` and can be reset by deleting or editing that file.

---

## Design Philosophy

### Visual Identity — "Atelier Arithmetic"

The application uses a **luxury editorial aesthetic** translated into a desktop UI:

| Token | Value | Usage |
|---|---|---|
| `BG_PRIMARY` | `#FAF9F6` | Off-white parchment background |
| `BG_CARD` | `#FFFFFF` | Pure white cards and panels |
| `ACCENT_GOLD` | `#B8966E` | Gold/bronze — all highlights, borders, progress |
| `TEXT_DARK` | `#1C1917` | Primary text — near-black warm tone |
| `TEXT_MUTED` | `#78716C` | Secondary text — warm grey |
| `SUCCESS_GREEN` | `#22C55E` | Correct answer feedback |
| `ERROR_RED` | `#EF4444` | Wrong answer feedback |

### Child-Centered UX Principles Applied

1. **Immediate feedback** — children need to know the result before moving on
2. **No time pressure** — the timer is informational, never punitive
3. **Error tolerance** — invalid input shows friendly messages, not technical errors
4. **Discovery-first** — the guided tour ensures no child is left confused
5. **Growth mindset** — every grade remark focuses on what comes next, not what went wrong
6. **Minimal navigation steps** — Welcome → Category → Play → Results is always ≤ 4 taps

---

## Roadmap

### Phase 1 — Core Foundation ✅ Complete

- [x] Modular MVC architecture (19 classes across 7 packages)
- [x] 6 question categories × 3 difficulty levels
- [x] Per-question live timer
- [x] Full session persistence (JSON)
- [x] Post-quiz answer review screen
- [x] Growth-mindset grading language
- [x] Interactive guided tour (Archie the Owl)
- [x] Persistent Help panel (8 sections)

### Phase 2 — Intelligence Layer 🔜 Next

- [ ] Analytics Dashboard (custom Java2D charts — accuracy trend, category radar)
- [ ] Adaptive Difficulty Engine (real-time adjustment based on rolling accuracy)
- [ ] Weakness-Targeted Smart Practice Mode (auto-configured from history data)

### Phase 3 — Engagement & Gamification 🔜 Planned

- [ ] Achievement & Badge System (10+ unlockable milestones)
- [ ] Daily Challenge Mode (deterministic date-seeded quiz)
- [ ] Streak & Consistency Tracking (daily practice calendar)
- [ ] Dark Mode Toggle (persisted theme preference)
- [ ] Sound Effects & Audio Reinforcement

### Phase 4 — Advanced Features 🔜 Planned

- [ ] Full Keyboard Navigation (WCAG 2.1 Level A compliance)
- [ ] Practice Mode with Step-by-Step Hints (scaffolded learning)
- [ ] Scalable Font Size / High-DPI Support
- [ ] PDF Session Report Export
- [ ] Multi-User Profile Support
- [ ] Custom Quiz Builder (teacher/parent-authored question sets)

---

## Technical Highlights

### Zero External Dependencies

The entire application uses **only the Java Standard Library**. No Maven, no Gradle, no third-party JARs. This means:
- Single `javac` command to compile
- No classpath configuration
- No version conflicts
- Works on any JDK 8+ installation

### JSON Without a Library

`JsonHelper.java` implements a purpose-built, schema-specific JSON serializer/deserializer:
- **Write path**: `StringBuilder`-based recursive object/array construction → guaranteed valid JSON
- **Read path**: Targeted `indexOf`/`substring` extraction of known keys → no full parse tree needed
- Produces human-readable, indented JSON that users can inspect in any text editor

### Glass Pane Tour Overlay

`TourOverlay.java` uses a sophisticated rendering technique for the spotlight effect:
```java
// Create full-screen area, subtract the spotlight rectangle
Area mask = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
mask.subtract(new Area(new RoundRectangle2D.Double(...)));
g2.setColor(new Color(0, 0, 0, 175));
g2.fill(mask);  // Draws dark mask with transparent hole
```
This approach is artifact-free and renders correctly across all JDK versions — no `AlphaComposite` compositing modes needed.

### Archie the Owl — No Image Assets

The mascot is rendered at any resolution by `MascotPainter.java` using only Java2D primitives:
- `GradientPaint` for the body shading
- `Arc2D` for mouth expressions
- `AffineTransform` (via Graphics2D rotate) for ear tufts
- `BasicStroke` with `CAP_ROUND` for the wing and foot details

This means Archie looks perfectly sharp at any size or screen DPI — no pixelation ever.

---

## Requirements

| Requirement | Minimum | Recommended |
|---|---|---|
| **Java Development Kit** | JDK 8 | JDK 17 LTS |
| **Operating System** | Windows 7 / macOS 10.12 / Ubuntu 16 | Windows 10+ / macOS 12+ / Ubuntu 22 |
| **RAM** | 128 MB available | 256 MB+ |
| **Display** | 1024 × 768 | 1280 × 800 or higher |
| **Disk Space** | < 1 MB (source + binaries) | — |

---

## License

```
MIT License

Copyright (c) 2026 Abdurrehman Narmawala

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

**Built with ❤️ and Java2D — no frameworks, no shortcuts, just clean engineering.**

*Atelier Arithmetic — Phase 1 Complete · Phase 2 Coming Soon*

</div>
