<div align="center">

<img src="src/com/mathquiz/resources/logo.png" width="120" height="120" style="border-radius: 12px; margin-bottom: 10px;" />

# 🦉 Atelier Arithmetic
### *A Premium Adaptive Math Mastery Desktop Application for Children*

<br/>

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![UI](https://img.shields.io/badge/UI-Java%20Swing-5C6BC0?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Architecture-MVC-2E7D32?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-0277BD?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-37474F?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-43A047?style=for-the-badge)

<br/>

> **"Transforming arithmetic practice from a chore into a daily adventure — one question at a time."**

<br/>

Atelier Arithmetic is a **production-quality, feature-rich Java Swing desktop application** built for children aged 8–17. It transforms standard arithmetic practice into an engaging, self-directed learning experience through adaptive question generation, a persistent progress system, a real-time per-question timer, a comprehensive answer review screen, and a fully interactive guided onboarding tour narrated by **Archie the Owl** — the app's brand mascot.

This project demonstrates strong product thinking, clean software engineering, and genuine educational value — far beyond a typical quiz application.

</div>

---

## 📋 Table of Contents

1. [Overview & Vision](#overview--vision)
2. [Key Features by Phase](#key-features-by-phase)
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

- Tells children **exactly** which questions they got wrong and what the correct answers were.
- Tracks **how fast** they answered each question, encouraging computational fluency.
- **Saves every session** to disk under user profiles to show long-term analytics and streak highlights.
- Uses **encouraging, growth-mindset language** in every piece of feedback.
- Onboards every new user with a **friendly interactive tour** so they immediately feel confident using the full feature set.

The result is an application children *want* to come back to — rather than one that makes them feel judged and want to leave.

---

## Key Features by Phase

Atelier Arithmetic has been fully realized through a rigorous 5-phase roadmap:

| Phase | Title | Key Additions |
|---|---|---|
| **Phase 1** | **Core Foundation** | MVC architecture, live per-question timer, scrollable review tables, JSON file storage, and growth-mindset grading. |
| **Phase 2** | **Intelligence Layer** | Custom Java2D bezier line graphs and radar analytics charts, adaptive difficulty prompts, and Smart Practice recommendations. |
| **Phase 3** | **Engagement & Audio** | Persistence streak tracking with flame indicators, 10 unlockable achievement badges, sound chimes, and Dark Mode theme toggle. |
| **Phase 4** | **Bespoke Features** | Multi-user profiles, step-by-step scaffolded hints, PDF/HTML report exports, and high-DPI scaling (125%/150%). |
| **Phase 5** | **Customizer & Tour** | Interactive 10-category quiz engine, custom parent/teacher Quiz Builder, and a comprehensive 19-step guided tour overlay. |

---

## Screenshots & Tour

> *The application runs as a native desktop window. Below is a description of each screen.*

### 🏠 Welcome Screen
The entry point of the application. Children set the **number of questions** and **difficulty level** (Easy / Medium / Hard). Four utility buttons are always visible: `❓ Guide` opens the Help panel, `⚙️ Profile` switches profiles, `🌙 Dark Mode` toggles themes, and `🦉 Tour` replays the guided tour. 

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
└─────────────────────────────────────────────────────────────────┘
```

---

## File Structure

```
d:/MathQuizApp/
├── README.md               # Complete project documentation
├── run.bat                 # Windows launch script
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

### Execution

Double-click `run.bat` or run the following command in PowerShell/CMD:

```cmd
run.bat
```

The script automatically compile source files, copy resources, and launch the application.

---

## Technical Highlights

### Resource Synchronization
To avoid classpath asset loading issues in Swing, the application's resources folder is synchronized programmatically during compilation to build targets (`bin/`). 

### Base64 Embedded Report Export
The report exporter reads the local raw brand icon `logo.png` dynamically, converts it to an offline-compatible Base64 data URI, and writes it directly inside the generated `math_report.html` header, ensuring zero broken image icons when shared across machines.

---

## License

This project is licensed under the MIT License. Archie the Owl logo artwork copyright Atelier Arithmetic Studio. All rights reserved.
