# Atelier Arithmetic — Product Requirements Document (PRD)

| Field | Value |
|---|---|
| Document version | 1.0 |
| Date | 2026-11-08 |
| Product / version | Atelier Arithmetic (a.k.a. "Royal Math Quiz") — **1.0.0** |
| Baseline | Git `main` @ `aaed02c` + one uncommitted text edit in `TourManager.java` |
| Basis of this document | Line-by-line audit of all 36 Java source files, `run.bat`, `package.ps1`, `sign.ps1`, `README.md`, `DEPLOYMENT.md` |
| Companion documents | [architecture.md](architecture.md) · [rules.md](rules.md) · [design.md](design.md) · [task.md](task.md) · [role_skills.md](role_skills.md) |

---

## 0. How to read this document

**Precedence of truth:** running code → this PRD and its companion docs → `README.md`. The README and the in-app Help were corrected against the code on 2026-11-08 (DOC-01, DOC-02); the mismatches that were found, and the code-level gaps that remain, are recorded in [§14](#14-spec-vs-implementation-gaps) and tracked in [task.md](task.md).

**Keywords.** *shall* = mandatory requirement. *should* = recommended. *may* = optional.

**Status legend** used in every requirement table:

| Code | Meaning |
|:---:|---|
| **I** | Implemented and behaves as specified |
| **P** | Partially implemented, or implemented with a known defect (see the linked task ID) |
| **N** | Not implemented (specified or implied by documentation, but absent in code) |

**IDs.** `US-nn` user story · `FR-xxx-nn` functional requirement · `BR-nn` business rule · `NFR-xxx-nn` non-functional requirement · `BUG-/DOC-/DEBT-/FEAT-/REL-nn` task IDs in [task.md](task.md).

---

## 1. Product overview

### 1.1 One-line definition
Atelier Arithmetic is an **offline, single-machine Windows desktop application** (Java Swing) in which children practise arithmetic across ten topic categories with adaptive hints and difficulty, earn stars and cosmetic rewards, and in which parents/teachers review analytics and author custom quizzes behind a "parental gate".

### 1.2 Problem
Typical quiz apps are disposable: the child answers, sees a score, and learns nothing about *what* went wrong, *how fast* they were, or *how they are trending*. Punitive grading language discourages children. Cloud-based products raise privacy concerns (children's data) and need accounts and connectivity.

### 1.3 Solution
A self-contained app that:
1. generates unlimited randomised questions with guaranteed integer answers;
2. shows every answer back (review table) and gives scaffolded hints on demand;
3. records every session locally (SQLite) per child profile and turns it into analytics, streaks and badges;
4. uses growth-mindset language at every grade;
5. motivates return visits through a star economy, a daily challenge and a mascot (Archie the Owl) that can be accessorised;
6. keeps adults in control through a math-puzzle "parental gate" and a quiz builder;
7. makes **zero network connections** at runtime.

### 1.4 Product principles
| # | Principle | Practical consequence |
|---|---|---|
| P1 | Growth mindset first | No grade, including the lowest, uses negative language (see [BR-01](#81-grading-br-01)). |
| P2 | Privacy by design | No network, no accounts, no real names requested, data encrypted at rest (see [NFR-SEC](#102-security-and-privacy)). |
| P3 | Adults control settings and data | Analytics, quiz builder, sound settings and history reset are behind the parental gate. |
| P4 | Practice, not pressure | Timer counts **up**, has **no limit**, and is informational only. |
| P5 | Motivation without dark patterns | Stars/streaks reward effort; rewards are cosmetic only; no ads, purchases, or social features. |

---

## 2. Goals and non-goals

### 2.1 Goals
| ID | Goal |
|---|---|
| G1 | Provide frequent, varied, age-appropriate arithmetic practice (10 categories × 3 difficulty levels). |
| G2 | Give immediate, specific feedback (correct answer, hint, review table, response time). |
| G3 | Surface progress and weaknesses to the child (badges, streak) and to adults (analytics dashboard, Smart Practice). |
| G4 | Encourage daily return with streaks, a seeded daily challenge, and stars. |
| G5 | Let parents/teachers author and run their own question sets. |
| G6 | Be installable without technical knowledge (MSI / portable ZIP, bundled JRE) and silently deployable in classrooms. |
| G7 | Protect children's data: local-only storage, minimal data, no identifying input encouraged. |

### 2.2 Non-goals (v1.0.0)
| ID | Non-goal |
|---|---|
| NG1 | Any network feature: cloud sync, accounts, leaderboards, telemetry, auto-update, ads. |
| NG2 | Real-money purchases. (Stars are earned only; shop items are cosmetic.) |
| NG3 | Multi-device or multi-user-on-network use. Data is bound to one OS user on one machine. |
| NG4 | Non-integer answers (decimals, fractions, negatives as designed answers). Every question has an `int` answer. |
| NG5 | Time-pressure mechanics (countdowns, speed scoring). |
| NG6 | Localisation (English only; strings are hard-coded). |
| NG7 | Mobile/web clients. |
| NG8 | Legal certification (COPPA / GDPR-K). The design follows the *principles*; no compliance claim should be made without legal review. |

---

## 3. Users and personas

| ID | Persona | Description | Key needs | Primary screens |
|---|---|---|---|---|
| **P1** | **Learner** (primary) | Child, target ages **8–14** (copy and code comments are written for this range; README positions the product for 8–17 — see [§16](#16-open-questions)). Reads at primary-school level, uses keyboard/mouse. | Fun, quick feedback, never feeling "bad", visible progress and rewards. | Home, Category, Game, Results, Review, Badges, Shop |
| **P2** | **Parent / guardian** | Installs the app, creates child profiles, monitors progress. | See accuracy/speed/streak and weak topics; stop the child changing settings or wiping data. | Analytics, Smart Practice, Quiz Builder, Sound menu |
| **P3** | **Teacher / tutor** | Runs the app for one or more children; needs targeted homework. | Author custom quizzes, print/export a session report. | Quiz Builder, Load Quiz, Export Report |
| **P4** | **IT administrator** | Deploys to classroom machines. | Silent MSI install, no Java prerequisite, signed binaries (no SmartScreen warning). | Installer (`msiexec /i … /qn`) |
| **P5** | **Maintainer** (stakeholder) | Evolves the codebase. | Clear architecture and rules; testability. | `docs/` |

---

## 4. Scope

### 4.1 In scope (v1.0.0 — all present in code)
Profiles · session setup · 10-category question engine · gameplay with timer, hints, adaptive-difficulty prompts · grading and results dashboard · answer review · HTML report export (+ print) · star rewards and 12-item mascot shop · 10 achievements · daily challenge and streaks · analytics dashboard (line chart, radar chart, heatmap, KPIs) · Smart Practice · custom quiz builder/loader · guided tour (24 steps) and help guide · dark mode, font scaling (100/125/150 %), programmatic sound with mute/volume · parental gate · encrypted local persistence · global crash handler/log · Windows packaging and code-signing scripts.

### 4.2 Out of scope
See [non-goals](#22-non-goals-v100). Additionally *not present today* (gaps, not non-goals): profile rename/delete, data backup/restore, hint-usage tracking, achievement-unlock notifications — see [task.md](task.md).

---

## 5. User stories

| ID | As a… | I want to… | So that… | Acceptance (summary) | Requirements |
|---|---|---|---|---|---|
| US-01 | Learner | pick how many questions and how hard | practice at my level | Count field (default 10) and Easy/Medium/Hard combo on Home | FR-SET-* |
| US-02 | Learner | choose a topic from pictured cards | practise what I want | 10 cards; click, Enter or Space starts | FR-CAT-* |
| US-03 | Learner | type an answer and see right/wrong instantly | learn at once | Feedback text + sound; correct answer shown if wrong | FR-GAM-* |
| US-04 | Learner | ask for a hint | get unstuck without giving up | 💡 button / F1 / Ctrl+H shows a step-by-step box | FR-HNT-* |
| US-05 | Learner | be offered easier/harder questions at the right moment | stay challenged but not frustrated | Prompt after 5 correct in a row / 3 wrong in a row | FR-ADP-* |
| US-06 | Learner | see my grade and what to do next | know how I did | Results dashboard with grade, time, speed, advice | FR-RES-* |
| US-07 | Learner | review every question I answered | learn from mistakes | Table with colour-coded rows | FR-REV-* |
| US-08 | Learner | earn stars and spend them on Archie's accessories | feel rewarded | Stars after each quiz; shop with 12 items | FR-RWD-*, FR-SHP-* |
| US-09 | Learner | collect badges | have goals | 10 badges; locked/unlocked view | FR-ACH-* |
| US-10 | Learner | play a daily challenge and keep a streak | build a habit | Date-seeded 10-question quiz; streak flame; 7-day strip | FR-DLY-* |
| US-11 | Learner | take a guided tour | know how the app works | 24-step spotlight tour, skippable, replayable | FR-TUR-* |
| US-12 | Parent | create a separate profile per child without real names | keep histories apart and private | Create-profile dialog with privacy notice and mascot-name generator | FR-PRF-* |
| US-13 | Parent | see accuracy, speed, streak, weak topics, history | know where to help | Analytics dashboard behind the gate | FR-ANL-* |
| US-14 | Parent | practise the weakest topic automatically | efficient remediation | Smart Practice launches 10 targeted questions | FR-SMP-* |
| US-15 | Parent | stop children altering settings or deleting history | protect data | Parental gate on 4 actions | FR-GTE-* |
| US-16 | Teacher | write my own questions and answers | align with the lesson | Quiz Builder saves JSON; Load Quiz runs it | FR-CQZ-* |
| US-17 | Teacher | save/print a session report | share with parents | HTML export with embedded logo; system print dialog | FR-EXP-* |
| US-18 | Parent / child | switch dark mode, text size and sound | comfort and accessibility | Persisted toggles | FR-SET2-* |
| US-19 | IT admin | install silently without Java | classroom rollout | MSI `/qn`; bundled JRE | NFR-DST-* |
| US-20 | Parent | reset a child's history | start fresh | Gate + confirmation; wipes sessions (and stars/shop — see BUG-13) | FR-DAT-* |

---

## 6. Functional requirements

### 6.1 Application shell (`FR-APP`)
| ID | Requirement | St |
|---|---|:---:|
| FR-APP-01 | The app shall run as one `JFrame` titled **"Atelier Arithmetic · Royal Math Quiz"**, initial size **820×620**, minimum **720×550**, centred, closing the window exits the process. | I |
| FR-APP-02 | The app shall apply the system look-and-feel, silently falling back to the default if unavailable, and enable text anti-aliasing (`awt.useSystemAAFontSettings=on`, `swing.aatext=true`). | I |
| FR-APP-03 | The window icon shall be `/com/mathquiz/resources/logo.png`. | I |
| FR-APP-04 | A launch chime shall play when the frame is constructed (if sound is enabled). | I |
| FR-APP-05 | A global uncaught-exception handler shall append thread name, timestamp and stack trace to `~/.atelier-arithmetic/logs/error.log` and show the dialog "Archie's Code Hiccup 🦉" with the exception message; the app shall continue running. | I |
| FR-APP-06 | All 11 screens shall be created once and switched with a `CardLayout` (cards: `welcome, categories, game, results, review, help, analytics, smart_practice, achievements, quiz_builder, mascot_shop`). | I |

### 6.2 Profiles (`FR-PRF`)
| ID | Requirement | St |
|---|---|:---:|
| FR-PRF-01 | A default profile named **Guest** shall exist; the profile list and active profile shall persist (`profiles`, `currentProfile`). | I |
| FR-PRF-02 | The top bar shall show `👤 <profile>`; clicking opens a menu listing profiles (active one marked ✓) and "➕ Create Profile…". Selecting a profile switches immediately and re-themes the UI. | I |
| FR-PRF-03 | The create dialog shall show the privacy notice *"To protect child safety, please do not use your real first or last name!…"*. | I |
| FR-PRF-04 | A profile name shall be accepted only if, after trimming, it is non-empty, matches `[a-zA-Z0-9_ ]+` (letters, digits, underscore, **space**) and is ≤ **15** characters; otherwise an error dialog is shown. An existing name is not duplicated (it is simply switched to). | I |
| FR-PRF-05 | A "🎭 Spark Mascot Name" button shall fill the field with `<Adjective> <Animal>` chosen from 12 adjectives × 12 animals. | **P** (5 of 144 combinations exceed 15 chars and are rejected — BUG-08) |
| FR-PRF-06 | **Per-profile data:** quiz sessions (DB), unlocked shop items, equipped item. **Global (shared by all profiles):** star balance, daily-challenge completion date, sound/theme/scale settings, tour-seen flag, custom quizzes. | **P** (star balance global — BUG-05) |
| FR-PRF-07 | Profile rename/delete. | **N** (FEAT-01) |

### 6.3 Session setup — Home screen (`FR-SET`)
| ID | Requirement | St |
|---|---|:---:|
| FR-SET-01 | Home shall provide a question-count text field (default `10`) and a difficulty combo (`Easy`, `Medium`, `Hard`; default Easy). | I |
| FR-SET-02 | "CHOOSE CATEGORY →" shall require the count to parse as an integer **> 0**; otherwise warning dialog ("Please enter a number…" / "Please enter at least 1 question!") and no navigation. There is **no upper bound**. | **P** (BUG-15) |
| FR-SET-03 | Home shall be a two-column dashboard: left = Archie header (equipped-accessory aware greeting), config card (count, difficulty, Quiz Builder, Load Quiz, Choose Category); right = four cards: **Daily Quest** (Daily Challenge button, streak, 7-day strip), **Performance & Badges** (sessions, badges `n / 10`, Analytics, Badges buttons), **Archie's Shop** (star balance, equipped item, shop button), **Smart Practice & Guide** (Practice, Tour, Guide buttons). | I |
| FR-SET-04 | Returning to Home shall refresh star balance, equipped item, greeting, session/badge counts, streak label and calendar strip. | I |

### 6.4 Category selection (`FR-CAT`)
| ID | Requirement | St |
|---|---|:---:|
| FR-CAT-01 | The screen shall show 10 cards in this order: Addition, Difference, Multiplication, Division, Mixed, Special, Fractions, Patterns, Algebra, Measurement — each with icon, name, one-line description and a START button; layout = 3 columns, vertically scrollable. | I |
| FR-CAT-02 | Clicking START, or pressing **Enter/Space** on a focused card, shall start a quiz with the Home count and difficulty. | I |
| FR-CAT-03 | Arrow keys shall move focus (Left/Right ±1 with wrap; Up/Down ±3 modulo 10); **Esc** returns Home; hover and focus show a gold 2-px border. | I |
| FR-CAT-04 | Footer buttons: "❓ Guide" and "← Back". | I |

### 6.5 Question engine (`FR-QGN`)
| ID | Requirement | St |
|---|---|:---:|
| FR-QGN-01 | A `Question` shall consist of an expression `String` and an `int` correct answer. | I |
| FR-QGN-02 | Every generated answer shall be an integer; every division/ratio/percentage/fraction question shall be constructed so the result is exact; Difference results shall be ≥ 1. Full per-category spec: [§9](#9-question-generation-specification). | I |
| FR-QGN-03 | **Mixed** shall pick uniformly among Addition, Difference, Multiplication, Division for *each question* (Special and the four advanced categories are excluded). | I |
| FR-QGN-04 | An unknown category name shall fall back to Addition. | I |
| FR-QGN-05 | Questions are drawn independently at random; **no de-duplication** within a session. | I |
| FR-QGN-06 | Seeded generation (`generateSeeded`) shall use `new Random(dateSeed*100 + questionIndex)` and be reproducible for the same inputs. | I |

### 6.6 Gameplay (`FR-GAM`)
| ID | Requirement | St |
|---|---|:---:|
| FR-GAM-01 | Header shall show "Question *i* of *N*", a progress bar (value = number answered) and a count-up timer `⏱ Ns` (whole seconds, updated every 1 s, **no limit**). | I |
| FR-GAM-02 | The expression shall be shown as `<expression> = ?` in a 32-pt serif font. | I |
| FR-GAM-03 | Answers shall be entered in a text field and submitted with **Enter** or "SUBMIT ANSWER". Accepted input: any string parseable by `Integer.parseInt` (so a leading `-` is accepted). Empty → "Type your answer first!"; non-integer → "Only whole numbers, please!"; in both cases the question is **not** consumed and the timer keeps running. | I |
| FR-GAM-04 | On a valid submit the timer stops, the field is disabled, and a `QuestionResult(expression, correct, user, isCorrect, elapsedMs)` is recorded. Elapsed time is measured from question display to submit (hint time included). | I |
| FR-GAM-05 | Feedback: correct → "✅ Correct! Great job!" (green) + success arpeggio; wrong → "❌ Wrong. The correct answer was: *X*" (red) + descending buzz. | I |
| FR-GAM-06 | After feedback the button reads "NEXT QUESTION →"; pressing it loads the next question or, after the last, finishes the session. | I |
| FR-GAM-07 | A tick sound shall play each second (very quiet). | I |
| FR-GAM-08 | **Esc** shall ask "Do you want to exit the current quiz and return to categories?"; **Yes** navigates to Categories and the session is discarded (not saved). | **P** (timer keeps running — BUG-03) |
| FR-GAM-09 | "❓ Guide" shall open Help; Back from Help during a game returns to **Home**, abandoning the session. | **P** (BUG-03) |

### 6.7 Hints (`FR-HNT`)
| ID | Requirement | St |
|---|---|:---:|
| FR-HNT-01 | A "💡 Hint" button (also **F1** and **Ctrl+H**) shall show an HTML hint in a gold-bordered box under the answer; the box hides when the next question loads. Hints are free and not tracked. | I |
| FR-HNT-02 | Hint content (first match wins, see `HintService.generateHint`): (a) starts with `(` and contains `)` and an operator → BODMAS hint (left bracket, right bracket, combine with middle operator when the form `) op (` is found, else generic BODMAS text); (b) contains substring `of` → fraction/percentage hint (1/2, 3/4, else "divide by 100 then multiply"); (c) starts `Pattern:` → generic "look at the change between steps"; (d) starts `Solve for x:` → generic "isolate x"; (e) starts `Convert` with meters→cm, hours→minutes, litres→mL → unit-factor hint; (f) operator `+`, `-`, `*`/`×`, `/` → partition/step/reverse-multiplication hint (worked example only for 2-digit operands, or exact division); (g) fallback "Break the numbers into parts…". | **P** (rule (b) misroutes measurement/ratio/numerator word problems — BUG-07) |

### 6.8 Adaptive difficulty (`FR-ADP`)
| ID | Requirement | St |
|---|---|:---:|
| FR-ADP-01 | After every submitted answer the engine shall evaluate the session. No recommendation is made until **≥ 4** answers exist. After a suggestion, a cool-down applies: the next suggestion can appear no earlier than **5 answers later** (the counter also advances if the child declines). | I |
| FR-ADP-02 | **UPGRADE** when the last **5** answers are all correct and current difficulty is not Hard. **DOWNGRADE** when the last **3** answers are all wrong and current difficulty is not Easy. Upgrade is checked first. | I |
| FR-ADP-03 | A recommendation shall be presented as a **modal Yes/No dialog** ("Level Up?" / "Adjust Level?"). Yes changes the *active session's* difficulty (affecting subsequent questions only) and shows a feedback line; No keeps it. The cool-down starts either way. | I |
| FR-ADP-04 | The difficulty stored with the session is the **final** value at completion. | I |

### 6.9 Results and grading (`FR-RES`)
| ID | Requirement | St |
|---|---|:---:|
| FR-RES-01 | Completing the last question shall: play the fanfare; if the category is "Daily Challenge" store today's date (`yyyyMMdd`) as `lastDailyChallengeDate`; **save** the session; **then** compute and award stars; populate and show Results. | I |
| FR-RES-02 | Results shall show 4 cards. **Key Metrics:** score *x / N*, success rate (`%.1f%%`), grade with emoji, total time (`Xm Ys` or `Ys`), average speed (sum of per-question time ÷ N, `%.1fs / question`), and "★ +*n* Stars Earned!" with a tooltip breakdown. **Session Mastery:** fastest *correct* question and a "challenge area" (slowest *incorrect* question; if none incorrect, slowest correct). **Archie's Insights:** advice by accuracy (≥95 / ≥80 / ≥50 / else). **Suggested Path:** one action button ([BR-08](#88-results-next-step-rules-br-08)). | I |
| FR-RES-03 | Footer: "❓ Guide", "📋 Review Answers", "📄 Export Report", "🔄 PLAY AGAIN" (→ Home). | I |
| FR-RES-04 | The "Suggested Path" button shall act as specified in BR-08. | **P** (listener accumulation and no-op "step up" — BUG-02) |
| FR-RES-05 | Grade/remark/emoji shall follow [BR-01](#81-grading-br-01). The remark text is shown only in the exported report (the dashboard shows grade + emoji). | I |

### 6.10 Review (`FR-REV`)
| ID | Requirement | St |
|---|---|:---:|
| FR-REV-01 | A read-only table shall list every answered question: `#`, `Expression = ?`, `Your Answer`, `Correct Answer`, `Result` (✅ Correct / ❌ Wrong), `Time` (`x.xs`). Rows are tinted green/red with two alternating shades per colour, with dark-mode variants. | I |
| FR-REV-02 | Buttons: "← Back to Results", "🏠 Play Again" (→ Home). | I |

### 6.11 Export and print (`FR-EXP`)
| ID | Requirement | St |
|---|---|:---:|
| FR-EXP-01 | "📄 Export Report" shall open a save dialog (default `~/.atelier-arithmetic/math_report.html`) and write a self-contained HTML file: embedded Base64 logo, grade box, category, difficulty, totals, duration (whole seconds), and a per-question table (time in whole seconds). | I |
| FR-EXP-02 | After saving, the system print dialog shall open automatically; the printed page is a plain-text summary of **at most the first 20 questions**. There is no way to export without the print prompt. | **P** (BUG-12) |
| FR-EXP-03 | Dynamic text in the HTML shall be HTML-escaped. | **N** (BUG-11) |

### 6.12 Stars (`FR-RWD`)
Rules in [BR-02](#82-star-rewards-br-02). 
| ID | Requirement | St |
|---|---|:---:|
| FR-RWD-01 | After each completed quiz (any category incl. Daily, Smart Practice, Custom) stars shall be computed per BR-02 and added to the balance; balance never negative. | I |
| FR-RWD-02 | Balance shall be visible on Home (top bar and shop card) and in the Shop header. | I |
| FR-RWD-03 | Balance shall be per profile. | **N** (global — BUG-05) |
| FR-RWD-04 | Unlocking an achievement shall award +15 stars. | **N** (method `awardAchievementBonus` exists but is never called — BUG-06) |

### 6.13 Mascot Shop (`FR-SHP`)
| ID | Requirement | St |
|---|---|:---:|
| FR-SHP-01 | The shop shall list the 12 items in [BR-03](#83-mascot-shop-catalogue-br-03) in a 3-column scrollable grid, each with emoji, category, name, description, price and one action button. | I |
| FR-SHP-02 | Button states: **Buy *n*★** (not owned) → if balance < price show "Not Enough Stars" info dialog; else confirm dialog → deduct → unlock **and auto-equip** → success dialog. **Equip** (owned, not equipped). **✅ Equipped** (clicking unequips). Only **one** item can be equipped at a time. | I |
| FR-SHP-03 | Unlocked and equipped items are stored per profile. The equipped item's emoji appears after 🦉 in Archie's Home greeting and as "<emoji> Equipped" on the shop card. Items are purely cosmetic. | I |

### 6.14 Achievements (`FR-ACH`)
| ID | Requirement | St |
|---|---|:---:|
| FR-ACH-01 | The 10 achievements in [BR-04](#84-achievements-br-04) shall be **computed on demand** from the active profile's saved history (no stored unlock state). Clearing history re-locks them. | I |
| FR-ACH-02 | The Badges screen shall show a 2-column grid; unlocked cards have the badge emoji and a gold outline, locked cards show 🔒 and muted text. | I |
| FR-ACH-03 | Newly unlocked achievements shall be announced (sound/toast) and rewarded. | **N** (BUG-06) |

### 6.15 Daily challenge and streaks (`FR-DLY`)
| ID | Requirement | St |
|---|---|:---:|
| FR-DLY-01 | The Daily Challenge shall be a **10-question, Medium, category "Daily Challenge"** session whose questions come from `generateSeeded(difficulty, "Mixed", seed = yyyyMMdd as long, index)`; hence all users on the same date get identical questions (unless the adaptive prompt changes difficulty mid-run). | I |
| FR-DLY-02 | Completing it shall record today's date globally and award +8 bonus stars (BR-02). The Home button then shows "✅ Completed" (disabled) for the rest of that calendar day. | I |
| FR-DLY-03 | The challenge shall be playable once per day. | **P** (Analytics and Results screens can start it again and re-award +8 — BUG-04) |
| FR-DLY-04 | Streak rules: [BR-05](#85-streaks-br-05). Home shows `🔥 n Day Streak` and a 7-day strip (today and the previous 6 days; ● played / ○ not). | I |

### 6.16 Analytics dashboard (`FR-ANL`)
| ID | Requirement | St |
|---|---|:---:|
| FR-ANL-01 | Entry from Home shall require the parental gate. | I |
| FR-ANL-02 | **Stat cards (4):** *Overall Accuracy* = arithmetic mean of session percentages (unweighted by question count), subtitle "Best score"; *Questions Solved* = Σ totalQuestions, subtitle total practice time (`Ns` or `N.N min`); *Average Speed* = mean per-question time, subtitle fastest correct answer (answers ≤ 100 ms ignored); *Practice Streak* = current streak in days, subtitle longest streak. | I |
| FR-ANL-03 | **Accuracy Trend:** smooth Bézier line chart of the **last 20** sessions, oldest → newest; needs ≥ 2 sessions, otherwise an empty-state message. | **P** (shows the *oldest* 20 when > 20 sessions exist — BUG-01) |
| FR-ANL-04 | **Category Radar:** 10-spoke radar with rings at 25/50/75/100 %; per-category value = mean percentage of that category's sessions (0 if none). Daily Challenge and Custom sessions are not plotted. | I |
| FR-ANL-05 | **Heatmap:** 28 cells (4 rows × 7), oldest first; gold = at least one session that day, grey = none (binary, no intensity). | I |
| FR-ANL-06 | **Recommendation** text from weakest category and weakest difficulty. **Strengths:** up to 2 categories with mean ≥ 70 %. **Weaknesses:** up to 2 categories with mean < 70 % (no overlap). Categories with no sessions are ignored. | I |
| FR-ANL-07 | **Recent history:** the 4 most recent sessions: category (difficulty), score %, seconds/question, `MM/DD`. | I |
| FR-ANL-08 | **Goals:** two read-only checkboxes ("Complete Daily Quest today", "Keep practice streak alive") and buttons "🎯 Smart Practice", "📅 Daily Challenge". | I |
| FR-ANL-09 | **Reset History** (gate + confirmation) shall clear the active profile's data per FR-DAT-01. | **P** (dialog does not disclose star/shop wipe — BUG-13) |

### 6.17 Smart Practice (`FR-SMP`)
| ID | Requirement | St |
|---|---|:---:|
| FR-SMP-01 | If the profile has ≥ 1 session: recommend *(weakest category, weakest difficulty)* — weakest category = lowest mean accuracy among the 10 canonical categories that have ≥ 1 session; weakest difficulty = lowest mean accuracy among Easy/Medium/Hard across all sessions. "START SMART PRACTICE →" starts **10** questions at that pair. | I |
| FR-SMP-02 | If no history: friendly empty state; the button reads "PLAY A REGULAR GAME →" and opens Category selection (defaults would be Mixed/Easy). | I |

### 6.18 Custom quizzes (`FR-CQZ`)
| ID | Requirement | St |
|---|---|:---:|
| FR-CQZ-01 | The Quiz Builder (gate-protected) shall let an adult enter a quiz name (default "My Custom Quiz"), an expression (free text) and an **integer** answer, add rows to a table, remove the selected row (rows re-numbered), and save. Validation: expression and answer required; answer must be an integer; name required; ≥ 1 question. | I |
| FR-CQZ-02 | Saved quizzes shall be stored as `~/.atelier-arithmetic/custom_quizzes/<sanitised-name>.json` where characters outside `[A-Za-z0-9_-]` become `_`. Saving an existing name **overwrites silently**; names differing only in special characters collide. | **P** (BUG-10) |
| FR-CQZ-03 | "📂 Load Quiz" (not gated) shall list saved quizzes alphabetically in a popup; selecting one starts a session with `total = number of questions`, difficulty `Medium`, category `"Custom: <name>"`, questions in stored order. Empty list → info dialog. | I |
| FR-CQZ-04 | The builder correctness of expression-vs-answer is the author's responsibility; the app does not evaluate expressions. Editing an existing quiz is not supported. | I |

### 6.19 Guided tour and help (`FR-TUR`, `FR-HLP`)
| ID | Requirement | St |
|---|---|:---:|
| FR-TUR-01 | On first launch (`tourSeen=false`) the tour shall auto-start 400 ms after the frame appears; it can be replayed from Home ("🦉 Tour") and from Help ("🦉 Replay Tour"). | I |
| FR-TUR-02 | The tour has **24 steps**: `welcome` 13 (1 intro + 12 element spotlights: profile, font scale, theme, calendar strip, Quiz Builder, Load Quiz, question count, difficulty, Analytics, Badges, Smart Practice, Choose Category), `categories` 1, `game` 5 (expression, answer field, hint, timer, submit), `results` 3 (grade, Export Report, Review Answers), `review` 1 (table), plus 1 closing `welcome` step. Each step dims the screen, cuts a rounded spotlight around the target, shows a speech bubble with text, Archie in one of 5 expressions, "Step *i* of *N*", **NEXT →/FINISH ✓** and **SKIP TOUR**. The tour auto-switches screens (using a mock session if none exists). | I |
| FR-TUR-03 | Finishing the final step shall persist `tourSeen=true`. | I |
| FR-TUR-04 | Skipping shall prevent the tour from auto-launching again. | **N** (skip does not persist — BUG-09) |
| FR-HLP-01 | Help shall show 13 sections (How to start, Categories (all 10), Hints/level prompts/shortcuts, Grades, Timer, Reviewing, Stars/badges/shop, Progress saved, Tips, Settings, Streaks & Daily Challenge, Analytics/Smart Practice/Quiz Builder, About). It is opened from the ❓ Guide buttons on Home, Categories, Game and Results; its Back button returns to `categories` or `results` when opened from there, otherwise to Home (leaving Help from a running quiz ends that quiz). Text was verified against the code on 2026-11-08 (DOC-02) and is hard-wrapped so the page fits the 720 px minimum window. | I |

### 6.20 Settings (`FR-SET2`)
| ID | Requirement | St |
|---|---|:---:|
| FR-SET2-01 | **Dark mode** toggle (🌙 Dark / ☀️ Light), persisted (`darkMode`), applied to all screens. | I |
| FR-SET2-02 | **Font scale** button cycles **100 % → 125 % → 150 % → 100 %**, persisted (`fontSizeScale`); the **default is 125 %**. | I |
| FR-SET2-03 | **Sound menu** (gate-protected): Mute/Unmute and four volume presets **100 / 75 / 50 / 25 %** (default stored volume 70 %, which matches no preset). Selecting a preset plays a click and persists. | I (menu of presets; there is no slider) |
| FR-SET2-04 | Every `JButton` that exists when `updateAllThemes()` runs shall produce hover and click sounds. | **P** (dynamic buttons — BUG-14) |

### 6.21 Parental gate (`FR-GTE`)
| ID | Requirement | St |
|---|---|:---:|
| FR-GTE-01 | The gate shall show: *"This screen is for parents or teachers only. Please solve this math puzzle to proceed: ★ What is **<word>** times **<word>**? ★"* with two factors uniformly random in **[3, 12]** spelled out in English; the correct integer product must be typed. | I |
| FR-GTE-02 | Cancel → denied silently. Non-numeric or wrong → warning dialog "Incorrect answer. Verification failed! Access restricted." and denied. | I |
| FR-GTE-03 | The gate protects exactly: **opening Analytics**, **opening Quiz Builder**, **Reset History**, **opening the Sound menu**. (Load Quiz, Shop, Smart Practice, Badges are *not* gated.) | I |

### 6.22 Data management (`FR-DAT`)
| ID | Requirement | St |
|---|---|:---:|
| FR-DAT-01 | "Reset History" shall: delete the active profile's rows from `sessions` (cascade to `session_questions`); set the (global) star balance to 0; clear that profile's unlocked and equipped items; refresh Home, Analytics, Badges, Smart Practice and Shop. Other profiles' sessions are untouched. | I |
| FR-DAT-02 | Backup/restore or export of history. | **N** (FEAT-04) |

---

## 7. Screen inventory

| Screen (card) | Class | Entry points | Gate |
|---|---|---|:---:|
| Home (`welcome`) | `WelcomePanel` | start-up; Back/Play Again from any screen | — |
| Categories (`categories`) | `CategoryPanel` | Home → Choose Category; Esc from Game; Help back | — |
| Game (`game`) | `GamePanel` | Category START; Daily; Smart Practice; Load Quiz | — |
| Results (`results`) | `ResultsPanel` | automatic after last question; Review back | — |
| Review (`review`) | `ReviewPanel` | Results → Review Answers | — |
| Help (`help`) | `HelpPanel` | "❓ Guide" buttons | — |
| Analytics (`analytics`) | `AnalyticsPanel` | Home → Analytics | **Yes** |
| Smart Practice (`smart_practice`) | `SmartPracticePanel` | Home; Results; Analytics | — |
| Badges (`achievements`) | `AchievementsPanel` | Home → Badges | — |
| Quiz Builder (`quiz_builder`) | `QuizBuilderPanel` | Home → Quiz Builder | **Yes** |
| Mascot Shop (`mascot_shop`) | `MascotShopPanel` | Home → Open Mascot Shop | — |
| Tour overlay | `TourOverlay` (glass pane) | first launch; Tour buttons | — |

Visual specs: [design.md](design.md). Navigation internals: [architecture.md](architecture.md#6-runtime-behaviour).

---

## 8. Business rules

### 8.1 Grading (BR-01)
`percentage = correctAnswers / totalQuestions × 100`. Boundaries are inclusive lower bounds.

| Percentage | Grade | Remark | Emoji |
|---|:---:|---|:---:|
| ≥ 95 | A++ | Outstanding — True Mastery! | 🏆 |
| ≥ 85 | A+ | Excellent — Impressive Skills! | 🌟 |
| ≥ 78 | A | Very Good — Strong Performance! | 💪 |
| ≥ 65 | B+ | Good — Solid Foundation! | 👍 |
| ≥ 53 | B | Progressing — Keep Practicing! | 📈 |
| ≥ 40 | C+ | Developing — You're Learning! | 🔄 |
| ≥ 33.33 | C | Getting Started — Stay Curious! | 🌱 |
| < 33.33 | D | Room to Grow — Try an Easier Level! | 🚀 |

The percentage stored in the database is rounded to one decimal place; the grade is stored too.

### 8.2 Star rewards (BR-02)
Computed **after** the session is saved (so the streak includes today).

| Component | Stars | Condition |
|---|:---:|---|
| Base | +5 | every completed quiz |
| Correct answers | +1 each | per correct answer |
| Perfect bonus | +10 | percentage = 100 (mutually exclusive with grade bonus) |
| Grade bonus | +5 | 85 ≤ pct < 100 |
| Grade bonus | +3 | 78 ≤ pct < 85 |
| Daily Challenge | +8 | category equals "Daily Challenge" (case-insensitive) |
| Streak | `min(2 × streakDays, 10)` | current streak after saving (≥ 1 today ⇒ at least +2) |
| Achievement unlock | +15 | **not implemented** (BUG-06) |

Worked example: 10/10 Medium quiz, 1-day streak → 5 + 10 + 10 + 0 + 0 + 2 = **27** stars. 6/10 with 3-day streak → 5 + 6 + 0 + 0 + 0 + 6 = **17**.

### 8.3 Mascot Shop catalogue (BR-03)
Total catalogue price = **470 ★**.

| ID | Item | Emoji | Category | Price | Description |
|---|---|:---:|---|:---:|---|
| `hat_top` | Top Hat | 🎩 | Hats | 30 | A dapper classic topper! |
| `hat_crown` | Royal Crown | 👑 | Hats | 50 | Fit for a math champion! |
| `hat_wizard` | Wizard Hat | 🔮 | Hats | 40 | Channel your inner wizard! |
| `hat_party` | Party Hat | 🎉 | Hats | 20 | Celebrate every quiz win! |
| `glasses_nerd` | Nerd Glasses | 👓 | Glasses | 20 | Show off your smarts! |
| `glasses_sun` | Cool Shades | 😎 | Glasses | 25 | Too cool for school! |
| `glasses_star` | Star Frames | ⭐ | Glasses | 35 | See the world in stars! |
| `color_gold` | Golden Archie | ✨ | Colors | 60 | Turn Archie into solid gold! |
| `color_rainbow` | Rainbow Glow | 🌈 | Colors | 80 | Shine in every color! |
| `badge_scholar` | Scholar Badge | 🎓 | Badges | 35 | Earn the scholar's emblem! |
| `badge_star` | Star Champion | 🌟 | Badges | 45 | The top achiever's mark! |
| `badge_heart` | Kind Heart | 💖 | Badges | 30 | Show your love of learning! |

### 8.4 Achievements (BR-04)
Evaluated over the active profile's sessions (`percentage` as stored, category/difficulty case-insensitive).

| ID | Badge | Emoji | Unlock condition |
|---|---|:---:|---|
| `first_quiz` | First Steps | 🌱 | ≥ 1 session |
| `perfect_score` | Arithmetic Genius | 👑 | any session with 100 % |
| `speed_demon` | Speed Demon | ⚡ | any *correct* question with 0 < time < 2000 ms |
| `math_maven` | Math Maven | 📚 | ≥ 10 sessions |
| `addition_ace` | Addition Ace | ➕ | Addition session ≥ 90 % |
| `sub_champ` | Difference Champion | ➖ | Difference session ≥ 90 % |
| `multi_master` | Multiplication Master | ✖️ | Multiplication session ≥ 90 % |
| `div_wizard` | Division Wizard | ➗ | Division session ≥ 90 % |
| `specialist` | Special Specialist | 🔬 | **Hard** Special session ≥ 80 % |
| `consistency` | Consistency Champion | 🔥 | **longest** streak ≥ 3 days |

### 8.5 Streaks (BR-05)
* A "practice day" is any calendar date (from the stored session timestamp, local time) with ≥ 1 saved session for the active profile.
* **Current streak:** if the latest practice day is **more than 1 day** before today → 0; otherwise count back consecutive days from the latest practice day. (Playing yesterday but not yet today keeps the streak alive.)
* **Longest streak:** longest run of consecutive practice days in history.
* Calendar strip: today and the 6 previous days.

### 8.6 Daily challenge (BR-06)
Seed = today's date as `yyyyMMdd` (long). Question *i* uses `new Random(seed*100 + i)` with category *Mixed*. Difficulty starts Medium, count is 10. Completion date is stored in `lastDailyChallengeDate` (global).

### 8.7 Adaptive difficulty constants (BR-07)
`MIN_QUESTIONS=4`, `UPGRADE_WINDOW=5` (ratio 1.0), `DOWNGRADE_WINDOW=3` (ratio ≤ 0.0), `COOLDOWN_QUESTIONS=5`, order Easy < Medium < Hard.

### 8.8 Results next-step rules (BR-08)
| Condition | Prompt | Button | Intended action |
|---|---|---|---|
| pct ≥ 85, difficulty Easy | "You are ready for the next challenge!" | STEP UP TO MEDIUM DIFFICULTY | go Home (does **not** change the difficulty combo) |
| pct ≥ 85, Medium | "Incredible skills! Level up to master." | STEP UP TO HARD DIFFICULTY | go Home (same limitation) |
| pct ≥ 85, Hard | "Master class quizzer! Try the seeded daily run." | PLAY TODAY'S DAILY CHALLENGE | start Daily Challenge |
| pct < 85 | "Review weaknesses to improve mastery!" | START SMART PRACTICE MODE | open Smart Practice |

---

## 9. Question generation specification

`Random` ranges below are **inclusive**. "Fixed" means a random choice among the listed templates with equal probability.

### 9.1 Core categories
| Category | Easy | Medium | Hard |
|---|---|---|---|
| **Addition** | `a + b`; a,b ∈ [10, 99] | `a + b + c`; each ∈ [100, 999] | `a + b + c + d`; a,b ∈ [1000, 9999], c,d ∈ [100, 999] |
| **Difference** | `a − b`; a ∈ [10, 99], b ∈ [5, a−1] | `a − b − c`; a ∈ [100, 999], b ∈ [10, a/2+9], c ∈ [5, a−b−6] | `a − b − c − d`; a ∈ [1000, 9999], b ∈ [100, a/3+99], c ∈ [50, a/3+49], d ∈ [10, …] — result always ≥ 1 |
| **Multiplication** | `a * b`; a ∈ [10, 98], b ∈ [2, 10] | `a * b * c`; a ∈ [10, 98], b ∈ [2, 9], c ∈ [2, 5] | `a * b * c`; a ∈ [100, 998], b,c ∈ [2, 9] |
| **Division** | `D / d`; d ∈ [2, 13], quotient ∈ [2, 16], D = d × q | `(D / d) / e`; e ∈ [2, 9], q ∈ [2, 11], d ∈ [2, 9]; answer q | `D / d`; d ∈ [3, 50], quotient ∈ [10, 99] |
| **Mixed** | per question: uniform choice of Addition / Difference / Multiplication / Division at the selected difficulty | | |

> Operators in expressions use ASCII `+ - * /` (not × ÷).

### 9.2 Special (five fixed templates; difficulty only sets `scale` = 10 / 50 / 150)
| # | Template | Parameters | Answer |
|---|---|---|---|
| 0 | `(A + B) * (C - D)` | A,B ∈ [3, scale+2]; C ∈ [10, scale+9]; D ∈ [1, C−2] | (A+B)(C−D) — *scale-dependent* |
| 1 | `(A - B) / (C + D)` | denom ∈ [2, 10], q ∈ [2, 13]; A−B = denom×q; C+D = denom (C = ⌊denom/2⌋+1, D may be 0 when denom = 2) | q — *scale-dependent* |
| 2 | `(A / B) + (C / D)` | B,D ∈ [2, 9]; q1,q2 ∈ [2, 11] | q1 + q2 |
| 3 | `(A * B) / (C * D)` | C,D ∈ [2, 6]; q ∈ [2, 9]; A = C×q, B = D | q |
| 4 | `(A / B) - (C * D)` | B ∈ [2, 9], q1 ∈ [10, 24]; D ∈ [1, 4]; C ∈ [1, ⌊q1/D⌋−1] | q1 − C×D |

Templates 2–4 are difficulty-independent.

### 9.3 Fractions
| Level | Sub-types (equal probability) |
|---|---|
| Easy | `What is a/b of c?` — b ∈ [2, 10]; a ∈ [1, b−1] coprime to b; c = b × m, m ∈ [2, 15]; answer a×m |
| Medium | (0) `What is the numerator of A B/C as an improper fraction?` — C ∈ [3, 10], B ∈ [1, C−1] coprime, A ∈ [1, 9]; answer A·C+B · (1) `If you simplify (a·d)/(b·d) to simplest form, what is the denominator?` — b ∈ [2, 10], a coprime, d ∈ [2, 8]; answer b · (2) `Calculate a/b of (b·m)` — b ∈ [5, 15], m ∈ [5, 20]; answer a·m |
| Hard | (0) `What is p% of x?` — p ∈ {5,10,15,20,25,30,40,50,60,70,75,80,90,110,120,150}; x chosen so p·x/100 is an integer · (1) `The ratio of red to blue marbles is a:b. If there are R red marbles, how many blue marbles are there?` — a,b ∈ [2, 9] coprime and distinct; factor ∈ [2, 15] · (2) `What is (a/b) * (c/d) of E?` — b,d ∈ [2, 5]; E = b·d·m, m ∈ [2, 10]; answer a·c·m |

### 9.4 Patterns (`Pattern: s1, s2, …, _`)
| Level | Sub-types |
|---|---|
| Easy | (0) arithmetic sequence, 4 terms shown, start ∈ [10, 39], step ±[2, 10] (shifted to avoid negatives) · (1) alternating `+a, −b` (a ∈ [3, 9], b ∈ [1, a−2]), 5 terms shown |
| Medium | (0) increasing step (initial d ∈ [1, 5], increment k ∈ [1, 3]), 5 terms · (1) geometric ×r, r ∈ [2, 4], start ∈ [1, 5], 4 terms · (2) alternating `×2, +k` (k ∈ [2, 6]), 5 terms |
| Hard | (0) Fibonacci-like from s1,s2 ∈ [1, 9], 6 terms · (1) `n² ± c` (c ∈ [1, 6]), 5 terms · (2) two interleaved sequences `A, B, A+d, B+d, A+2d, B+2d` · (3) `n(n+1)` or `n² − n`, 5 terms |

### 9.5 Algebra (`Solve for x: …`; x is always a positive integer ≥ 2)
| Level | Sub-types |
|---|---|
| Easy | `x + A = B` · `x − A = B` (B may be negative) · `A − x = B` · `A x = B` (A ∈ [2, 10], x ∈ [2, 12]) |
| Medium | `Ax + B = C` · `Ax − B = C` · `B − Ax = C` · `(x + B) / A = C` |
| Hard | `Ax + B = Cx + D` · `Ax − B = Cx + D` · `If x + y = S and x − y = T, what is x?` · `If x + y = S and 2x + y = T, what is x?` |

### 9.6 Measurement
| Level | Sub-types (equal probability) |
|---|---|
| Easy | Convert m→cm (2–20) · cm→mm (2–50) · km→m (1–9) · kg→g (1–9) · L→mL (1–9) |
| Medium | Convert hours→minutes (2–8) · "How many minutes are in H hours and M minutes?" (H 1–4, M 5–55) · perimeter of rectangle (sides 3–20) · area of square (3–15) · area of rectangle (3–15) |
| Hard | "*m* m and *cm* cm to centimeters" (m 2–15, cm 5–95) · "*ml* mL + *L* L, total mL" (L 1–5, ml 100–900) · square garden perimeter → area (side 5–25) · rectangle area + length → perimeter · volume of box (l 2–8, w 2–6, h 2–5) |

---

## 10. Non-functional requirements

### 10.1 Performance *(targets — not measured)*
| ID | Requirement |
|---|---|
| NFR-PRF-01 | Question generation and answer handling shall complete well within one frame (< 16 ms). |
| NFR-PRF-02 | Opening Analytics with ≤ 500 stored sessions should take < 1 s. *Known risk:* each refresh performs ≈ 17 full-history loads with per-row decryption (DEBT-03). |
| NFR-PRF-03 | Sound playback must not block the EDT (each sound uses its own thread). |

### 10.2 Security and privacy
| ID | Requirement | St |
|---|---|:---:|
| NFR-SEC-01 | The running app shall open **no network sockets** (no analytics, update checks or cloud). *(Build-time `sign.ps1` contacts a timestamp server; that is tooling, not the app.)* | I |
| NFR-SEC-02 | `config.properties` shall be stored as a single Base64 AES-128 blob; sensitive DB columns (`profile_name, category, difficulty, grade, expression`) shall be AES-128 encrypted. | I |
| NFR-SEC-03 | The key shall be derived from `SHA-256(user.name + os.name + user.home)[0..16)`. **Implication:** data is bound to one OS user/machine; it is *obfuscation-grade* protection against casual file copying, **not** protection against an attacker with the same account (cipher mode is ECB, deterministic, no IV, key is reproducible). See DEBT-08. | I |
| NFR-SEC-04 | Only data needed for the educational function shall be stored; profile-creation UI shall discourage real names. | I |
| NFR-SEC-05 | Decryption failure shall not crash the app (raw value returned). | I |
| NFR-SEC-06 | Administrative actions shall be behind the parental gate (FR-GTE). The gate is a deterrent for children, not an authentication mechanism. | I |

### 10.3 Reliability
| ID | Requirement | St |
|---|---|:---:|
| NFR-REL-01 | Saving a session and its questions shall be a single DB transaction with rollback on failure. | I |
| NFR-REL-02 | Uncaught exceptions shall be logged and not terminate the app (FR-APP-05). | I |
| NFR-REL-03 | Config writes shall be atomic and tolerate corruption. | **N** — non-atomic `FileOutputStream` overwrite on every setter; an undecryptable file silently yields defaults (DEBT-07). |
| NFR-REL-04 | Foreign-key cascade shall remove question rows with their session. | I |

### 10.4 Accessibility and usability
| ID | Requirement | St |
|---|---|:---:|
| NFR-ACC-01 | Three text sizes (100/125/150 %). | I |
| NFR-ACC-02 | Category screen fully keyboard-operable; Game screen operable by keyboard (Enter, Esc, F1/Ctrl+H); tour is mouse-driven (buttons). | P |
| NFR-ACC-03 | Colour contrast ≥ WCAG AA for text. | **P** — gold accent text on light backgrounds is ≈ 2.7 : 1 (FEAT-05). |
| NFR-ACC-04 | Screen-reader accessible names. | **N** |
| NFR-ACC-05 | Emoji must render on Windows; HTML labels wrap emoji in `Segoe UI Emoji`. | I |

### 10.5 Compatibility
| ID | Requirement |
|---|---|
| NFR-CMP-01 | **Run-time:** Windows 10 or later, 64-bit, no installed Java (bundled custom JRE, Java 21.0.1 in the existing build; modules `java.base, java.desktop, java.sql, java.xml, java.naming, jdk.charsets` + transitive). |
| NFR-CMP-02 | **Build:** JDK 17 or 21 (JDK 21.0.1 verified; clean compile, no warnings). Source needs ≥ JDK 9 (`InputStream.readAllBytes`). Source files contain emoji literals → compile with `-encoding UTF-8` (not yet in scripts — DEBT-02). |
| NFR-CMP-03 | Source runs on any OS with a JDK, but the shipped packaging and emoji font handling target Windows. |

### 10.6 Distribution
| ID | Requirement | St |
|---|---|:---:|
| NFR-DST-01 | `package.ps1` shall produce a native app-image and, if WiX (`candle.exe`/`light.exe`) is on `PATH`, an MSI (`AtelierArithmetic-1.0.0.msi`, upgrade UUID `71a2e7c3-3882-4df4-94fa-7e9b422a59cc`, Start-menu group "Atelier Arithmetic", desktop shortcut, directory chooser); otherwise a portable ZIP. | I (existing `dist/` contains only the portable ZIP, 46,470,444 bytes) |
| NFR-DST-02 | Silent install: `msiexec /i AtelierArithmetic-1.0.0.msi /qn /norestart`. | I (documented; not tested here) |
| NFR-DST-03 | `sign.ps1` shall sign with SHA-256 + RFC 3161 timestamp (DigiCert) via PFX, EV token (smart-card CSP) or certificate store, and verify with `signtool verify /pa`. | I |

### 10.7 Maintainability
| ID | Requirement | St |
|---|---|:---:|
| NFR-MNT-01 | Layering and coding rules in [rules.md](rules.md) shall be followed. | P |
| NFR-MNT-02 | Automated tests shall cover generator invariants, grading, rewards, analytics maths. | **N** (no tests exist — DEBT-01) |
| NFR-MNT-03 | Language: English only; all user-facing text in code. | I |

---

## 11. Data requirements

| Datum | Store | Per-profile | Encrypted | Notes |
|---|---|:---:|:---:|---|
| Settings (`tourSeen, soundEnabled, soundVolume, darkMode, fontSizeScale, currentProfile, profiles, lastDailyChallengeDate`) | `config.properties` | No | Whole file | Defaults: tour false, sound true, volume 70, dark false, scale 1.25, profile `Guest`, profiles `Guest` |
| Stars | `config.properties` `starBalance` | **No (global)** | Whole file | integer ≥ 0 |
| Shop state | `unlockedItems.<profile>`, `equippedItem.<profile>` | Yes | Whole file | comma-separated IDs; `none` default |
| Sessions | SQLite `sessions` | Yes (encrypted `profile_name`) | `profile_name, category, difficulty, grade` | counts, percentage, timestamp, duration in clear |
| Question results | SQLite `session_questions` | via session | `expression` | answers/times in clear |
| Custom quizzes | `custom_quizzes/*.json` | No | **No** | plaintext JSON |
| Crash log | `logs/error.log` | No | No | append-only; no rotation |
| Reports | user-chosen `.html` | — | No | contains questions/answers |

All app data lives under `%USERPROFILE%\.atelier-arithmetic\`. Uninstalling the app does not remove it. Details: [architecture.md §7](architecture.md#7-data-architecture).

---

## 12. Constraints, assumptions, dependencies

* **Single dependency:** `lib/sqlite-jdbc-3.42.0.0.jar` (13.0 MB; bundles native libs for Windows/Mac/Linux). Everything else is JDK.
* **Assumption:** one OS user account per household/classroom machine; children share the OS account and are separated only by in-app profiles.
* **Assumption:** the parental gate's strength (a times-table puzzle) is adequate against young children only.
* **Constraint:** all state is local; moving to another PC/OS account requires re-creating data (encrypted data cannot be read elsewhere).
* **Constraint:** Swing UI scaled via font-size multiplication, not layout reflow; at 150 % some screens may need scrolling in the 720×550 minimum window *(unverified)*.

---

## 13. Success metrics *(proposed — no telemetry exists)*

Because the product is offline and collects nothing, success must be assessed locally or through pilots:

| Metric | How measured | Target (proposed) |
|---|---|---|
| Learner re-engagement | median distinct practice days in the first 14 days, from the local DB during a pilot | ≥ 5 |
| Completion | share of started sessions that reach Results | ≥ 90 % |
| Perceived friendliness | pilot survey of children/parents | ≥ 4/5 |
| Setup success | pilot: install → first quiz without help | ≥ 95 % |
| Stability | crash-log entries per 100 hours of use | < 1 |

---

## 14. Spec-vs-implementation gaps

Mismatches that were found between claims (README, Help text, code comments) and the code. **The README and the in-app Help were corrected on 2026-11-08 (DOC-01, DOC-02)**, so every row marked *fixed* now matches the code. Where the *code* itself falls short of the intended behaviour, the gap stays open under the listed task and is disclosed in README → *Known Limitations*.

| # | Claim (as originally written) | Reality | Documentation | Open code task |
|---|---|---|---|---|
| 1 | Tour has 19 steps | 24 steps | README fixed | — |
| 2 | Category grid "2×5" | 3 columns, scrollable | README fixed | — |
| 3 | Sound popup has a volume slider with live preview | Menu with mute + presets 100/75/50/25 | README fixed | — |
| 4 | Star balance is per-profile | Global `starBalance` key | README + Help now say "shared" | BUG-05 |
| 5 | Unlocking a badge awards +15 stars | Never wired | README fixed (disclosed) | BUG-06 |
| 6 | Sounds for category select, streak milestone, achievement | Methods exist but are never called | README fixed | BUG-06 / DEBT-10 |
| 7 | Adaptive difficulty shows a non-intrusive banner | Modal confirm dialog | README fixed | — |
| 8 | Questions are non-repeating | No de-duplication | README fixed | — |
| 9 | Multiplication Easy is 2–12 × 2–12; Special has nested/3-term brackets | See §9 | README fixed | — |
| 10 | Heatmap intensity-mapped; Analytics shows last 10 sessions | Binary heatmap; 4 sessions | README fixed | — |
| 11 | Profile names have no spaces | Spaces allowed | README fixed | — |
| 12 | Wrong gate answer is "silently denied" | Warning dialog shown | README fixed | — |
| 13 | Grade C/D remarks | Code: "Getting Started — Stay Curious!" / "Room to Grow — Try an Easier Level!" | README fixed (Help already matched) | — |
| 14 | Help has 8 sections | 11 sections; **13 after DOC-02** | README + Help fixed | — |
| 15 | Daily challenge once per day | Only the Home button enforces it | README + Help now say so | BUG-04 |
| 16 | Tour completion/skip persisted | Only completion | README fixed | BUG-09 |
| 17 | Accuracy Trend = last 20 sessions | Oldest 20 once > 20 exist | README discloses | BUG-01 |
| 18 | MIT licence | No `LICENSE` file in repo | **Not fixed** (owner decision Q8) | DOC-05 |
| 19 | README: Help "accessible from every screen", opens "without losing progress", Back "returns to the screen the user came from" | Guide buttons exist only on Home, Categories, Game, Results; Back from a quiz goes Home and ends it | README fixed | — |
| 20 | README/DEPLOYMENT: bundled runtime ~35 MB, ~80 MB installed | Measured 46 MB runtime, 59 MB portable build (Java 21.0.1) | README + DEPLOYMENT fixed | — |
| 21 | README: "COPPA-compliant", Quiz Builder section "fully" gated incl. Load, "AI-targeted" Smart Practice, "animated" hint drawer / tour highlight, "Quick actions: Refresh", ms review times | COPPA-aware (not a certification); Load Quiz not gated; rule-based; static box / ring; no Refresh button; seconds | README fixed | — |
| 22 | Help: only 6 categories, "future updates" history, "hexagonal radar", plain sound toggle, "prevents double-completion", no Stars/Badges/Shop, "Practising" spelling | See Help rewrite | Help fixed | — |
| 23 | Tour text: "visual mascot feedback" in the quiz, results "animate the owl" and show "remarks", "interactive" charts, timing "graphed" | No such behaviour | **Not fixed** | DOC-06 |

---

## 15. Risks

| ID | Risk | Likelihood | Impact | Mitigation |
|---|---|:---:|:---:|---|
| R1 | Data unreadable after Windows user rename / machine move (key derivation) | Med | High | Backup/export (FEAT-04); document limitation |
| R2 | Config corruption on crash wipes stars/settings | Low | Med | Atomic writes (DEBT-07) |
| R3 | JDK 17 build on Windows mis-decodes emoji (no `-encoding UTF-8`) | Med | Med | DEBT-02 |
| R4 | Performance degrades with large history (N+1, repeated full loads on EDT) | Med | Med | DEBT-03, DEBT-09 |
| R5 | Unsigned binaries trigger SmartScreen warnings | High (if unsigned) | Med | EV signing (REL-02) |
| R6 | Children bypass parental gate by guessing (9–144 products) | Med | Low–Med | Accept; or strengthen (FEAT) |
| R7 | Privacy/legal claims exceed what is certified | Low | High | NG8; keep wording "privacy-by-design" |
| R8 | No automated tests → regressions in generators/rewards | High | Med | DEBT-01 |

---

## 16. Open questions

| # | Question | Owner |
|---|---|---|
| Q1 | Official target age range: 8–14 (code comments) or 8–17 (README)? | Product Owner |
| Q2 | Should stars be per-profile (matching shop state) or deliberately shared (family wallet)? | Product Owner |
| Q3 | Should "Reset History" also wipe stars/shop (current behaviour) or only sessions? | Product Owner |
| Q4 | Should the Daily Challenge be strictly once per day, per profile? | Product Owner |
| Q5 | Do achievements need an unlock moment (toast/sound/+15 stars)? | Product Owner / UX |
| Q6 | Is a maximum question count (e.g. 100) desirable? | Product Owner |
| Q7 | Is macOS/Linux packaging required? | Product Owner |
| Q8 | Is the stated MIT licence correct, and who is the copyright holder? | Owner |

---

## 17. Glossary

| Term | Meaning |
|---|---|
| **Archie** | Brand mascot, an owl; drawn in Java2D (tour) and shown as 🦉 + accessory emoji (Home). |
| **Atelier Arithmetic** | Product name; window title adds "Royal Math Quiz". |
| **Card** | A `CardLayout` screen. |
| **Parental gate** | Times-table puzzle dialog (`ParentalGate.verifyParent`). |
| **Profile** | A named child identity (Guest by default) isolating history and shop state. |
| **Session** | One quiz run: N questions, category, difficulty, stored with results. |
| **Stars (★)** | Reward currency; spent in the Mascot Shop. |
| **Smart Practice** | Auto-targeted 10-question quiz on the weakest category/difficulty. |
| **Daily Challenge** | Date-seeded 10-question Medium quiz, +8 stars. |
| **Tour** | Spotlight walkthrough narrated by Archie (`TourOverlay`). |
| **BODMAS** | Order of operations (Brackets, Orders, Division/Multiplication, Addition/Subtraction). |

---

## 18. Traceability (requirements → code)

| Requirement group | Primary files |
|---|---|
| FR-APP | `QuizApp`, `QuizFrame`, `QuizNavigator` |
| FR-PRF, FR-SET, FR-SET2 | `WelcomePanel`, `AppConfig`, `AppTheme` |
| FR-CAT | `CategoryPanel` |
| FR-QGN | `QuestionGenerator`, `Question` |
| FR-GAM, FR-HNT, FR-ADP | `GamePanel`, `HintService`, `AdaptiveDifficultyEngine`, `QuizSession`, `QuestionResult` |
| FR-RES, FR-REV, FR-EXP | `ResultsPanel`, `ReviewPanel`, `QuizSession` |
| FR-RWD, FR-SHP | `RewardService`, `MascotShopPanel`, `AppConfig` |
| FR-ACH | `AchievementService`, `AchievementsPanel` |
| FR-DLY | `WelcomePanel`, `GamePanel`, `QuestionGenerator`, `AnalyticsService` |
| FR-ANL, FR-SMP | `AnalyticsService`, `AnalyticsPanel`, `SmartPracticePanel` |
| FR-CQZ | `QuizBuilderPanel`, `CustomQuizService`, `JsonHelper` |
| FR-TUR, FR-HLP | `TourManager`, `TourOverlay`, `TourStep`, `MascotPainter`, `HelpPanel` |
| FR-GTE | `ParentalGate` |
| FR-DAT, NFR-SEC | `SessionRepository`, `CryptoHelper`, `AppConfig`, `QuizFrame.resetAllData` |
| NFR-DST | `run.bat`, `package.ps1`, `sign.ps1`, `IcoConverter`, `DEPLOYMENT.md` |
