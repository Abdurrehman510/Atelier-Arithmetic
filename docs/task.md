# Atelier Arithmetic — Task Register, Backlog and Test Plan

| Field | Value |
|---|---|
| Document version | 1.0 |
| Date | 2026-11-08 |
| Baseline | Git `main` @ `aaed02c`; working tree has one uncommitted change (`TourManager.java`) |
| Evidence standard | Every defect below was confirmed by reading the cited code; **none were reproduced at run-time** unless stated. File/line references are for the baseline and will drift. |
| Related | [PRD.md](PRD.md) · [architecture.md](architecture.md) · [rules.md](rules.md) · [design.md](design.md) · [role_skills.md](role_skills.md) |

---

## 1. How to use this register

**IDs:** `BUG-` defect · `DEBT-` technical debt · `DOC-` documentation · `REL-` release/ops · `FEAT-` proposed enhancement.
**Priority:** **P0** data loss / security / child-safety / release blocker · **P1** user-visible defect or high-value fix · **P2** quality improvement · **P3** nice to have.
**Size:** **S** ≤ 2 h · **M** ≤ 1 day · **L** multi-day.
**Status:** `Open` · `In progress` · `Blocked (decision)` · `Done`. All items below are **Open** at the baseline.
**Rules for work on a task:** cite the ID in the commit body ([rules.md](rules.md) R-GIT-03); update the PRD/architecture sections named in *Docs*; satisfy the Definition of Done ([rules.md §13](rules.md#13-testing-and-definition-of-done-r-tst)); when done, move the task to §3 with the commit hash.
**Decisions needed** (from PRD §16) are marked **Blocked (decision)** and name the question.

---

## 2. Project status snapshot

| Item | State |
|---|---|
| Product version | 1.0.0 (feature-complete for the 1.0 scope in PRD §4.1) |
| Last commit | `aaed02c` — 2026-07-13 — "docs: add About the Author section to README.md" |
| Uncommitted | `README.md` (DOC-01), `src/com/mathquiz/view/HelpPanel.java` (DOC-02), `DEPLOYMENT.md` (size claim), the new `docs/` folder, and `src/com/mathquiz/service/TourManager.java` (tour text "6 disciplines" → "10 arithmetic disciplines"). Needs committing — DOC-03 |
| Build | Clean compile on JDK 21.0.1, 0 warnings (verified 2026-11-08) |
| Automated tests | **None** |
| Existing artefacts | `dist/atelier-arithmetic.jar`, `dist/AtelierArithmetic/` (app-image), `dist/AtelierArithmetic-v1.0.0-Portable.zip` (46.5 MB). **No MSI** built yet. |
| Signing | Scripts exist; no evidence artefacts are signed |
| Licence | README says MIT; **no `LICENSE` file** in repo |
| Size | 36 Java files, ≈ 9,700 lines |

---

## 3. Completed work (from git history)

| Date | Commit(s) | Deliverable |
|---|---|---|
| 2026-07-06 | `3faa020` | Phase 1: MVC foundation, 6 categories, timer, progress bar, review table, child-friendly grading |
| 2026-07-09 | `b011eb6` | README; features of Phases 2–5 completed (analytics, adaptive difficulty, smart practice, achievements, sound, dark mode, profiles, hints, tour, quiz builder) |
| 2026-07-09 | `b22809f` | Fix theme NPE on image-only labels |
| 2026-07-09 | `a1e9690`, `07bc026`, `68a3cd5`, `0503a1c` | UI polish: 3-column category grid with scroll; 125 % overlap fixes; gold hint box; two-column Home dashboard |
| 2026-07-09 | `0f3e460`, `7d671d1` | Randomised templates for Fractions/Patterns/Algebra/Measurement; 10-axis radar |
| 2026-07-09 | `a9f6c1b` | Programmatic sound system with mute/volume |
| 2026-07-09 | `020decb`, `fe3d800`, `d78f425`, `3a73f2c`, `f6ac5bb`, `c7525a9`, `ddcc413` | Analytics dashboard redesign and layout/overlap fixes |
| 2026-07-11 | `f6be529` | **SQLite** persistence + global uncaught-exception handler |
| 2026-07-11 | `4ae4ae7` | **AES encryption**, parental gates, COPPA-aware mascot-name generator |
| 2026-07-11 | `9049fe5` | Brand logo redesign |
| 2026-07-11 | `e433611` | First rewards-economy/shop prototype ("coins") |
| 2026-07-12 | `0521ece`, `5cf780a`, `3713045` | **Stars economy + Mascot Shop**, extended gates, emoji-font and shop-UI fixes |
| 2026-07-13 | `68e85d5` | Question-generation redesign for remaining four categories; analytics reset consistency |
| 2026-07-13 | `974e8da` | Packaging (`package.ps1`), ICO converter, signing script |
| 2026-07-13 | `c17e418`, `aaed02c` | README rewrite; author section |
| 2026-11-08 | *(uncommitted)* | Documentation set `docs/` created from a full code audit (this file and 5 companions) |
| 2026-11-08 | *(uncommitted)* | **DOC-01 done** — README corrected against the code (85 edits: tour 24 steps, 3-column grid, sound menu, shared stars, no badge bonus, adaptive dialog, exact question ranges, heatmap/history counts, profile-name and gate rules, grade remarks, 13-section Help, runtime/installed sizes, COPPA wording) plus new *Known Limitations* and *Further documentation* sections. **DOC-02 done** — in-app Help rewritten (13 sections: all 10 categories, hints/level prompts/shortcuts, stars/badges/shop, accurate settings/sound/daily/analytics text; lines hard-wrapped and body width pinned so the page no longer overflows horizontally — the original page rendered 1,498 px wide, now 674 px). `DEPLOYMENT.md` runtime-size claim corrected (46 MB measured). |

---

## 4. Open tasks

### 4.1 Defects

#### BUG-01 — Accuracy Trend shows the *oldest* 20 sessions once more than 20 exist · `P1 · S`
* **Evidence:** `AnalyticsService.getAccuracyTrend` (lines 42–59): `loadRaw()` is newest-first; `from = max(0, size − max)` and `subList(from, size)` selects the *end* of the list, i.e. the **oldest** `max` sessions, then iterates in reverse.
* **Impact:** After 21+ sessions the chart stops reflecting recent progress while its title says "last 20 sessions".
* **Fix:** take `raw.subList(0, min(max, size))` (newest) and iterate in reverse for oldest→newest.
* **Accept:** with 25 seeded sessions with distinct percentages, the chart's 20 points equal sessions 6–25 in chronological order; ≤ 20 sessions unchanged.
* **Docs:** PRD FR-ANL-03.

#### BUG-02 — Results "Suggested Path" button accumulates listeners; "STEP UP" buttons don't change difficulty · `P1 · S`
* **Evidence:** `ResultsPanel.generateNextSteps` (lines 199–228) calls `actionButton.addActionListener(...)` on every `populate()` without removing earlier ones. The two "STEP UP TO …" variants only call `nav.goToWelcome()`.
* **Impact:** After N quizzes in one run the button fires N handlers, including stale ones from earlier branches (e.g. *goToWelcome* **and** *startSmartPractice* both run); duplicated navigation sounds/refreshes. "Step up" promises a change it does not make.
* **Fix:** keep a single listener that reads the current recommendation from a field (or remove existing listeners first); add `QuizNavigator` support to pre-select the next difficulty (e.g. `goToWelcome(String suggestedDifficulty)`), or relabel the button.
* **Accept:** after three consecutive quizzes, one click triggers exactly one navigation; after a ≥ 85 % Easy quiz the Home difficulty combo shows Medium (or the label is changed).
* **Docs:** PRD FR-RES-04, BR-08.

#### BUG-03 — Question timer (and its tick sound) keeps running after a quiz is abandoned · `P1 · S`
* **Evidence:** `GamePanel` starts the Swing `Timer` in `resetTimer()` for each question; it is stopped only in `handleSubmit()` and at session end (`GamePanel.java` 290–308, 312–316, 386). Esc → *Yes* (`setupKeyBindings`, 485–500) navigates away without stopping it; so do "❓ Guide" → Back (→ Home) and the tour's mock session (`QuizFrame.showCardForTour`).
* **Impact:** a tick sound every second and timer events on unrelated screens until a new session starts.
* **Fix:** stop the timer on abandon (add a `GamePanel.cancelSession()` called from the Esc handler and on card change), and when the tour switches away.
* **Accept:** after Esc→Yes there is no tick sound; no `Timer` events while on any other screen.
* **Docs:** PRD FR-GAM-08/09.

#### BUG-04 — Daily Challenge can be replayed (and re-rewarded) from Analytics and Results · `P1 · S` · *Blocked (decision Q4)*
* **Evidence:** once-per-day is enforced only by the Home button (`WelcomePanel.buildCalendarStrip`, 922–932). `AnalyticsPanel` (499–504) and `ResultsPanel` (215–219) call `nav.startDailyChallenge()` unconditionally; `RewardService` (113) grants +8 for every "Daily Challenge" session.
* **Impact:** unlimited +8 stars; contradicts Help ("Prevents double-completion daily").
* **Fix:** enforce in `QuizFrame.startDailyChallenge()` (central) — if `lastDailyChallengeDate == today`, show an info dialog and return; hide/disable the other buttons accordingly. Decide whether the date should be per profile (see BUG-05).
* **Accept:** after completing the challenge, no entry point starts it again until the next calendar day.
* **Docs:** PRD FR-DLY-03.

#### BUG-05 — Star balance is global, not per profile · `P1 · M` · *Blocked (decision Q2)*
* **Evidence:** `AppConfig.getStarBalance/setStarBalance` use the plain key `starBalance` (140–151) while unlocked/equipped items use `.<profile>` suffixes. README claims per-profile.
* **Impact:** siblings share/spend one wallet while history and shop items are separate; `resetAllData` zeros the shared balance (QuizFrame 431).
* **Fix (if per-profile chosen):** key `starBalance.<profile>` with one-time migration of the legacy key to the active profile; make `lastDailyChallengeDate` per profile as well. If shared is intended: document it and show "Family stars".
* **Accept:** star balance changes only for the active profile; switching profiles changes the displayed balance; legacy balance preserved after upgrade.
* **Docs:** PRD FR-PRF-06, FR-RWD-03, architecture §7.1.

#### BUG-06 — Achievement unlocks are silent and unrewarded · `P2 · M`
* **Evidence:** `RewardService.awardAchievementBonus` (128–130) is never called; `SoundService.playAchievement`, `playStreak`, `playCategorySelect`, `playNotification` are never called. Achievements are computed on demand with no stored "already unlocked" state.
* **Impact:** README/Help promise +15 ★ and an unlock sound; none occurs.
* **Fix:** persist the set of awarded achievement IDs per profile (config key `achievements.<profile>`); on `finishQuiz`, diff newly unlocked vs awarded, award +15 ★ once each, play `playAchievement`, show a toast/dialog on Results; wire or delete the other unused sounds.
* **Accept:** unlocking "First Steps" gives +15 ★ exactly once, with sound and a visible notice; clearing history does not re-award on re-unlock *(decide: re-award or not)*.
* **Docs:** PRD FR-RWD-04, FR-ACH-03, BR-02.

#### BUG-07 — Hints for measurement/ratio/numerator questions are the wrong hint · `P1 · S`
* **Evidence:** `HintService.generateHint` (25–27) routes any expression containing the substring `of` to the fraction/percentage hint. Affected generated texts include "Find the area **of** a square…", "Find the perimeter **of** a rectangle…", "…has a perimeter **of** … Meters", "…has an area **of**…", "Find the volume … **of** a box", "The ratio **of** red to blue…", "What is the numerator **of** …".
* **Impact:** a child asking for help on an area problem is told to "divide by 100 and multiply by the percent".
* **Fix:** route by explicit category markers (`What is a/b of`, `% of`, `Calculate a/b of`, `… of E?` for fractions) and add dedicated hints for area/perimeter/volume/ratio/numerator/time conversions; fall back to generic.
* **Accept:** for every generator template there is a unit test asserting the hint contains the expected key phrase; no template maps to the percentage hint except fraction/percent templates.
* **Docs:** PRD FR-HNT-02.

#### BUG-08 — "Spark Mascot Name" can generate names the dialog rejects (> 15 chars) · `P2 · S`
* **Evidence:** `WelcomePanel` 768–774 (12 adjectives × 12 animals) vs validation 809. Over-length combos: *Friendly* + {Squirrel(17), Dolphin(16), Penguin(16), Cheetah(16)}, *Sparkly* + Squirrel(16) → 5/144.
* **Fix:** rename the longest words (e.g. "Friendly"→"Kindly" or trim animals) or filter generated names to ≤ 15.
* **Accept:** 10,000 generations are all valid.
* **Docs:** PRD FR-PRF-05.

#### BUG-09 — Skipping the tour never marks it as seen · `P2 · S`
* **Evidence:** `TourManager.skipTour()` → `endTour(false)` → `setTourSeen(true)` only when `completed` (277–284).
* **Impact:** a user who skips is shown the tour at every launch.
* **Fix:** persist `tourSeen=true` on skip as well (replay remains available).
* **Accept:** Skip at step 2 → restart → no auto-tour.
* **Docs:** PRD FR-TUR-04.

#### BUG-10 — Custom quiz storage is fragile · `P2 · M`
* **Evidence:** `JsonHelper.extractString` (110–117) ends the value at the first `"` and never unescapes; `escapeString` writes `\"`/`\\`. `CustomQuizService.sanitizeFilename` maps all other chars to `_` (81–83), so "Quiz 1" and "Quiz_1" collide and `saveQuiz` silently overwrites; `JsonHelper.writeFile` swallows `IOException` yet `saveQuiz` returns `true` (27–45).
* **Fix:** proper JSON escape/unescape (or use a tiny parser), detect name collisions (confirm overwrite), return `false` on write failure.
* **Accept:** an expression containing `"` and `\` round-trips; collision prompts; failed write shows an error.
* **Docs:** PRD FR-CQZ-02.

#### BUG-11 — Exported HTML is not escaped · `P2 · S`
* **Evidence:** `ResultsPanel.handleExportReport` appends `getCategory()`, `getExpression()` raw (553, 568).
* **Impact:** a custom-quiz name/expression with `<` breaks or injects markup into the local report.
* **Fix:** HTML-escape all dynamic text.
* **Accept:** expression `<b>x</b>` renders literally.
* **Docs:** PRD FR-EXP-03.

#### BUG-12 — Export always opens the print dialog; print output truncated · `P3 · S`
* **Evidence:** `handleExportReport` ends with `triggerNativePrint()` (586); the printable draws at most 20 rows (617).
* **Fix:** separate "Export HTML" and "Print" actions; paginate printing.
* **Accept:** exporting does not open a print dialog; printing handles > 20 questions.
* **Docs:** PRD FR-EXP-02.

#### BUG-13 — "Reset History" also wipes stars and shop purchases without saying so · `P1 · S` · *Blocked (decision Q3)*
* **Evidence:** warning text (`AnalyticsPanel` 281–287) mentions only quiz history; `QuizFrame.resetAllData` (424–441) also sets `starBalance=0`, clears equipped/unlocked items.
* **Fix:** either limit the reset to sessions, or list everything deleted in the confirmation ("…your stars and Archie's accessories too").
* **Accept:** dialog text matches exactly what is removed.
* **Docs:** PRD FR-ANL-09, FR-DAT-01.

#### BUG-14 — Dynamically rebuilt content may not be scaled or sound-instrumented · `P2 · M` · *needs visual verification*
* **Evidence:** `AppTheme.scaleComponentFont` runs only from `QuizFrame.updateAllThemes()` (start-up, theme/scale/profile/sound toggles). Panels rebuilt in `refresh()` (Analytics cards, badge cards, shop grid, smart-practice labels) are created after that call.
* **Impact (expected):** at the default 125 % some screens show base-size fonts, and their buttons lack hover/click sounds until the next toggle.
* **Fix:** call `AppTheme.scaleComponentFont(panel, scale, sound)` at the end of each `refresh()/populate()` (or hook `QuizFrame` navigation).
* **Accept:** fonts and button sounds are identical before and after toggling the scale button, on every screen.
* **Docs:** design §13, architecture §6.7.

#### BUG-15 — No upper limit on the number of questions · `P2 · S`
* **Evidence:** `WelcomePanel.handleStart` (578–596) accepts any integer > 0.
* **Fix:** cap (e.g. 1–100, spinner or clamp with message) — decision Q6.
* **Accept:** values above the cap are refused with a friendly message.
* **Docs:** PRD FR-SET-02.

#### BUG-16 — Category grid Up/Down navigation wraps oddly · `P3 · S`
* **Evidence:** `CategoryPanel` 208–211 uses `(idx ± 3) mod 10` on a 3-column grid with 10 items (e.g. ↓ from card 8 goes to card 1).
* **Fix:** compute row/column; stop at the last row or wrap by column.
* **Accept:** ↑/↓ keep the same column and clamp/wrap predictably.

#### BUG-17 — Daily Challenge determinism breaks if the adaptive prompt changes difficulty · `P3 · S`
* **Evidence:** `GamePanel.loadNextQuestion` seeds with the session's *current* difficulty (322–329); `proposeUpgrade/Downgrade` can change it mid-run.
* **Impact:** two children can receive different questions on the same day after accepting a prompt.
* **Fix:** suppress adaptive prompts in Daily Challenge sessions (or freeze the seed difficulty).
* **Accept:** two runs with different prompt choices still produce identical question lists.

### 4.2 Documentation

| ID | Pri · Size | Task | Acceptance |
|---|---|---|---|
| **DOC-03** | P2 · S | Commit the pending changes in two commits: `docs: correct README against the code and add docs/ set` (README.md, DEPLOYMENT.md, docs/) and `fix(help): align in-app Help text with the app` (HelpPanel.java, plus the `TourManager.java` text fix as its own `fix(tour): …` commit) | `git status` clean |
| **DOC-04** | P2 · ongoing | Keep `docs/` in sync (R-DOC-02): every behaviour/schema/number change updates PRD/architecture in the same commit; review this register at each release | Release checklist item ticked |
| **DOC-05** | P1 · S · *Blocked (decision Q8)* | Add a `LICENSE` file (README says MIT) with the correct copyright holder; add `.gitattributes` (R-GIT-07) | File present; README link works |
| **DOC-06** | P2 · S | Correct the guided-tour copy in `TourManager.initialize` (it also needs a visual check): game submit step promises "visual mascot feedback" (none exists); results step says high scores "animate the owl mascot" and show "remarks" (the dashboard shows grade + emoji; remarks appear only in the exported report); analytics step calls the charts "interactive"; timer step says timing is "graphed in analytics" (only averages are shown); Quiz Builder step says "compile" | Every tour message describes behaviour that exists |

### 4.3 Technical debt

#### DEBT-01 — Introduce automated tests · `P1 · L`
* **Why:** zero tests; generators and reward/grade maths directly affect children and stored data.
* **Plan:** add `test/` with JUnit 5 (vendored console-standalone JAR in `lib-test/` to stay offline; needs approval per R-ARC-09). First targets: (1) **`QuestionGenerator` invariants** — for each of 10 categories × 3 difficulties × ≥ 10,000 samples assert integer answer, exact division, Difference ≥ 1, algebra x ≥ 2, format prefixes, and seeded reproducibility; (2) `QuizSession` grade/remark/emoji boundaries (95, 85, 78, 65, 53, 40, 33.33); (3) `AdaptiveDifficultyEngine` windows/cool-down; (4) `RewardService.calculateAndAwardQuizReward` table-driven (PRD BR-02 examples); (5) `AnalyticsService` streak/trend/weakest logic with an in-memory or temp-dir repository; (6) `HintService` per template (BUG-07); (7) `CryptoHelper` round-trip and fallback; (8) `SessionRepository` save/load/clear/cascade against a temp DB; (9) `JsonHelper`/`CustomQuizService` round-trip incl. escapes.
* **Enabler:** make the app directory overridable (`-Datelier.home=…`/constructor parameter) — part of DEBT-04.
* **Accept:** tests run with one command and fail on injected defects (e.g. change `pct >= 95` to `> 95`).

#### DEBT-02 — Build hygiene: encoding, line endings, version · `P1 · S`
* Add `-encoding UTF-8` to `run.bat` and `package.ps1` (and any future build); add `.gitattributes`; document the non-Windows classpath separator. **Risk addressed:** JDK 17 on a Windows ANSI code page can corrupt emoji literals. **Accept:** clean build on JDK 17 *and* 21, UI shows emoji correctly.

#### DEBT-03 — Reduce repeated full-history loads · `P2 · M`
* **Evidence:** `loadRaw()` (all sessions + N+1 question queries + decryption) is invoked ≈ 17× per Analytics open and ≈ 5× per Home refresh (architecture §7.5); several panels also `new SessionRepository()` repeatedly (re-running `CREATE TABLE IF NOT EXISTS`).
* **Fix:** inject one `SessionRepository`; add `loadSummaries()` (sessions only) and a per-refresh snapshot object; cache with explicit invalidation on `save/clear`; add `CREATE INDEX` on `session_questions(session_id)`.
* **Accept:** Analytics open performs ≤ 2 repository loads; measured open time with 500 seeded sessions < 1 s.

#### DEBT-04 — Dependency injection and layering clean-up · `P2 · L`
* Remove `AppConfig.getInstance()`/`new SessionRepository()` from views and `SessionRepository`; pass `AppConfig`/profile provider via constructors; introduce `AppPaths` (overridable root); move `TourManager`/`ParentalGate` out of `service` (or behind interfaces); remove `AppTheme → SoundService`; add `QuizNavigator.refreshTheme()` so `WelcomePanel` stops casting to `QuizFrame`. **Accept:** rules R-ARC-01/02/04 hold for all files (grep-checkable).

#### DEBT-05 — UI tokens, styles and "god panel" decomposition · `P2 · L`
* Remove per-panel colour constants; create `Styles` helpers (primary/secondary/chip button, card, label roles); replace `recolorTree` heuristics with explicit styling; split `WelcomePanel` (profile dialog, sound menu, calendar strip, cards), `AnalyticsPanel` (charts into own classes), `ResultsPanel` (export/print service). Single category list. **Accept:** no `new Color(` outside `AppTheme`/chart code; each class < 500 LOC.

#### DEBT-06 — De-duplicate streak/date logic and adopt `java.time` · `P2 · S`
* Streak is implemented in both `AnalyticsService` and `WelcomePanel` with different `dayDiff` code (one uses `Math.abs`). Keep one implementation in `AnalyticsService` (plus `LocalDate`). **Accept:** Home and Analytics always show the same streak (test with boundary dates, DST).

#### DEBT-07 — Atomic config writes and visible persistence failures · `P1 · M`
* **Evidence:** every setter rewrites the file with a truncating `FileOutputStream` (`AppConfig.save`, 216–228); an undecryptable/corrupt file silently loads as defaults (stars, shop, profiles lost); `SessionRepository.save` only prints to `System.err` on failure (the child still sees Results, the session is lost).
* **Fix:** write temp file + `Files.move(ATOMIC_MOVE)`, keep `config.properties.bak`, on load failure restore backup and log; surface a friendly non-blocking notice when saving a session fails. **Accept:** kill -9 during a write never corrupts; simulated DB failure shows a message.

#### DEBT-08 — Strengthen at-rest protection · `P2 · L` · *needs decision*
* Replace AES/ECB with AES-GCM (random IV), key from a random per-install secret protected by Windows DPAPI (or user-supplied password), HMAC-SHA-256 blind index for `profile_name`/`category` equality lookups, versioned ciphertext prefix, and a migration that re-encrypts old data. Update wording in README/PRD. **Accept:** old installs open and migrate; copied DB is unreadable without the protected key; tests cover migration.

#### DEBT-09 — Move blocking work off the EDT · `P3 · M`
* Run repository loads/saves and analytics computation in `SwingWorker` with lightweight "loading" states. **Accept:** no UI freeze with 2,000 seeded sessions.

#### DEBT-10 — Sound service hygiene · `P3 · S`
* Use a single-thread executor and reuse/size-limit audio lines; mark threads daemon; remove or wire the four unused sounds (BUG-06). **Accept:** rapid hovering never creates more than N concurrent lines.

#### DEBT-11 — Database schema versioning · `P2 · S`
* Add `PRAGMA user_version`, a `Migrations` list, indexes, and an upgrade test using a v1 fixture DB. **Accept:** opening a v1 DB upgrades silently and keeps data.

#### DEBT-12 — Remove deprecated `Date` constructor usage · `P3 · S`
* `AnalyticsService.parseDate` uses `new Date(y-1900, m-1, d)`; replace with `LocalDate` (folds into DEBT-06). **Accept:** build shows no deprecation notes.

#### DEBT-13 — Real PNG logo · `P3 · S`
* `logo.png` is JPEG data. Re-export as true PNG (consider transparency) at 1024² and 256² (+ ICO sizes 16–256). **Accept:** `file` reports PNG; ICO contains multiple sizes.

#### DEBT-14 — Externalise UI strings for localisation · `P3 · L` (prerequisite for FEAT-07)

### 4.4 Release and operations

| ID | Pri · Size | Task | Acceptance |
|---|---|---|---|
| **REL-01** | P2 · S | Single version source (e.g. `version.properties` read by `package.ps1`, `HelpPanel`, artefact names) | Changing one value updates all |
| **REL-02** | P1 · S | Release pipeline signs the inner `AtelierArithmetic.exe` *before* MSI creation, then signs the MSI; document in DEPLOYMENT.md | `signtool verify /pa` passes for both |
| **REL-03** | P1 · M | Install WiX 3.11, build the MSI, test install / uninstall / upgrade / silent install; confirm user data folder survives upgrade | MSI in `dist/`; test log recorded |
| **REL-04** | **P0 before any release** · M | Smoke-test on a clean Windows 10 and 11 VM **without Java** (run §7 cases, esp. sound, SQLite native load, emoji rendering, print dialog) | All §7 release-blocking cases pass |
| **REL-05** | P1 · S | Stop passing PINs/passwords as plain CLI args (read from env or secure prompt); `.gitignore` `*.pfx *.p12 *.key`; remove unused `-CryptoProvider` parameter | No secret appears in shell history/process list |
| **REL-06** | P3 · S | `CHANGELOG.md` + release notes template; record JDK version and artefact hashes | Present for 1.0.1 |

### 4.5 Proposed enhancements (not committed scope)

| ID | Pri | Proposal | Notes |
|---|---|---|---|
| FEAT-01 | P2 | Profile management: rename, delete, per-profile stars/daily date/settings | Depends on BUG-05 decision |
| FEAT-02 | P3 | Track hint usage per question (store, show in Review/Analytics) | Schema change → DEBT-11 first |
| FEAT-03 | P3 | De-duplicate questions within a session; mastery-based difficulty progression | Needs generator tests |
| FEAT-04 | P2 | Backup/restore (export/import) of history & settings; makes machine moves possible | Mitigates PRD risk R1; interacts with DEBT-08 |
| FEAT-05 | P2 | Accessibility pass: AA-compliant text colours, visible focus, keyboard-operable tour, accessible names, high-contrast theme | See design §3.5, §14 |
| FEAT-06 | P3 | macOS/Linux packaging with `jpackage` (sqlite-jdbc already bundles natives) | Emoji font handling differs |
| FEAT-07 | P3 | Localisation (needs DEBT-14) | |
| FEAT-08 | P3 | Teacher reports: multi-session PDF/HTML for a profile | |
| FEAT-09 | P3 | Heatmap intensity + tooltips; trend chart tooltips | |
| FEAT-10 | P3 | Optional parent PIN instead of / in addition to the math puzzle | Decision on gate strength |

---

## 5. Suggested roadmap

| Milestone | Theme | Contents | Exit criteria |
|---|---|---|---|
| **M1 — Trust and correctness** | Fix what is visibly wrong | BUG-01, 02, 03, 04*, 07, 13*; DOC-03, 05*, 06 (DOC-01/02 done) | Defects verified fixed by §7 cases; README accurate |
| **M2 — Safety nets** | Make change safe | DEBT-01 (generators, grading, rewards first), DEBT-02, DEBT-07, BUG-09, 10, 11, 15 | Tests run in one command; no silent data loss paths |
| **M3 — Data consistency** | Rewards and profiles | BUG-05*, BUG-06, DEBT-11, FEAT-01 | Per-profile economy; achievements rewarded once |
| **M4 — Maintainability** | Pay down structure | DEBT-03, 04, 05, 06, 09, 10, 12, BUG-14, BUG-16, 17, REL-01 | Rules R-ARC-* hold; Analytics < 1 s with 500 sessions |
| **M5 — Release hardening** | Ship 1.0.x | REL-02, 03, 04, 05, DEBT-13 | Signed MSI + ZIP verified on clean VMs |
| **M6 — Enhancements** | Grow | DEBT-08, FEAT-04, FEAT-05, FEAT-0x | Per-feature PRD updates |

\* requires a decision from PRD §16 first (Q2, Q3, Q4, Q8).

---

## 6. Exceptions register (rules waived)
| Rule | Where | Reason | Approved by | Date |
|---|---|---|---|---|
| *(none)* | | | | |

---

## 7. Manual regression checklist

Run on a **throw-away Windows user profile** or with `-Duser.home=<temp>` so real data is not touched (R-TST-07). "Now" marks cases whose current result differs from the expected result because of an open task. Cases marked **RB** are release-blocking.

| ID | Area | Steps | Expected |
|---|---|---|---|
| TC-01 **RB** | First launch | Delete `~/.atelier-arithmetic`; start app | Window 820×620 titled "Atelier Arithmetic · Royal Math Quiz"; launch chime; tour auto-starts ~0.4 s later; Home shows `👤 Guest`, `★ 0`, "Scale: 125%" |
| TC-02 | Tour complete | Click NEXT through all steps | Counter "Step i of 24"; screens switch automatically; last button reads FINISH ✓; restart → no tour |
| TC-03 | Tour skip | SKIP at step 3; restart | Expected: no tour. **Now:** tour returns (BUG-09) |
| TC-04 | Corrupt config | Replace `config.properties` with random text; start | App starts with defaults; no crash dialog |
| TC-05 | Profile create | Create "Maya 7" | Profile active; history empty; name appears in menu with ✓ |
| TC-06 | Profile validation | Try empty, `a/b`, 16-char names | Error dialogs; nothing created |
| TC-07 | Mascot names | Click 🎭 50× and create each | All accepted. **Now:** *Friendly Squirrel/Dolphin/Penguin/Cheetah*, *Sparkly Squirrel* rejected (BUG-08) |
| TC-08 | Dark mode | Toggle; visit all 11 screens; restart | Readable everywhere; persists |
| TC-09 | Font scale | Cycle 125→150→100→125 on every screen at minimum window size | No clipped controls (scroll bars where needed); persists. Check BUG-14 consistency |
| TC-10 **RB** | Sound menu | Open (gate); mute; unmute; each preset | Gate required; mute silences all effects; presets audibly differ; persists |
| TC-11 | Gate | Open Analytics/Quiz Builder/Sound/Reset with right, wrong, cancelled answers | Right → opens; wrong → "Lock Active" dialog; cancel → silent; each time a new puzzle with factors 3–12 |
| TC-12 | Ungated | Open Shop, Badges, Smart Practice, Load Quiz | No gate |
| TC-13 **RB** | Generator sampling | Play 5 questions for every category × difficulty (30 runs) or run harness | Integer answers; exact division; Difference positive; formats per PRD §9 |
| TC-14 | Mixed | 30 Mixed questions | Only +, −, ×, ÷ single-type questions (no brackets/word problems) |
| TC-15 | Count validation | Enter `abc`, `0`, `-3`, `1`, `100000` | Warnings for first three; `1` works; huge number accepted (BUG-15) |
| TC-16 **RB** | Play flow | Answer right then wrong | Feedback strings exactly as design §2.2; sounds; button text toggles; timer stops on submit, resets on next |
| TC-17 | Input errors | Submit empty, `abc`, `-5` | Empty/abc → red messages, question not consumed; `-5` accepted as an answer |
| TC-18 | Hints | Hint via button, F1, Ctrl+H on Addition(2-digit), Division, bracket, Pattern, Algebra, Measurement | Appropriate hint box; hides on next question. **Now:** area/perimeter/volume/ratio/numerator show the percentage hint (BUG-07) |
| TC-19 | Esc | Esc → No; Esc → Yes | No → stays; Yes → Categories; session not in history. **Now:** tick sound/timer continue (BUG-03) |
| TC-20 | Adaptive up | Easy, 10 questions, answer 5 correct in a row | "Level Up?" dialog after 5th (needs ≥ 4 answered); Yes → later questions Medium; results show Medium; Hard session never prompts to upgrade |
| TC-21 | Adaptive down | Medium: Q1 correct, Q2–Q4 wrong | "Adjust Level?" after Q4; Easy never prompts to downgrade |
| TC-22 | Cool-down | After a prompt, continue streak | Next prompt not before 5 answers later |
| TC-23 **RB** | Finish & save | Complete a quiz | Fanfare; Results populated; session visible in Analytics/Recent History |
| TC-24 | Grades | N = 20: score 19, 17, 16, 13, 11, 8, 7, 6; N = 3: score 1 | A++, A+, A, B+, B, C+, C, D; and 33.33 % → C |
| TC-25 | Stars | 10/10 Medium on a fresh profile | +27 ★ (5 + 10 + 10 + 2); tooltip lists Base, Correct answers, Perfect score bonus, Streak bonus; balance on Home matches |
| TC-26 | Review | Open Review | Row count = questions; colours; times `x.xs`; Back buttons work |
| TC-27 | Export | Export Report → open file | HTML opens with logo and table; print dialog appears (cancel OK); with expression `<b>` renders literally (BUG-11) |
| TC-28 | Next-step button | Finish 3 quizzes in one run (mix of ≥ 85 % and < 85 %), click the Suggested Path button | One navigation only. **Now:** multiple (BUG-02) |
| TC-29 | Shop buy | Try item above balance; then affordable item | Info dialog; confirm → deducted, unlocked, equipped, success dialog; Home greeting shows emoji |
| TC-30 | Shop equip | Equip/unequip another owned item | Only one equipped; card border gold; Home reflects |
| TC-31 | Profiles & shop | Buy item on A; switch to B | B sees nothing owned; star balance shown is the global one (BUG-05 — see decision) |
| TC-32 | Achievements | Trigger each badge (see PRD BR-04); simulate 3-day streak by changing system date or DB timestamps | Badge unlocks; 🔒 replaced by emoji; no +15 ★ (BUG-06) |
| TC-33 | Daily challenge | Start from Home twice (restart between) before finishing | Same first question both times; finishing gives +8 ★; button shows "✅ Completed" |
| TC-34 | Daily re-entry | After completing, use Analytics "📅 Daily Challenge" and Results hard-path button | Expected: refused. **Now:** starts again (+8 ★ each time) (BUG-04) |
| TC-35 | Streak | Play on consecutive days; skip a day | 🔥 count increments; resets after a missed day (> 1 day gap); yesterday-only still counts |
| TC-36 | Analytics empty | New profile → Analytics | Empty-state messages; no exceptions |
| TC-37 | Trend | Seed 25 sessions with ascending % | Chart shows latest 20. **Now:** oldest 20 (BUG-01) |
| TC-38 | Radar & lists | Play two categories | Radar polygon matches category means; strengths ≥ 70 %, weaknesses < 70 %, no overlap; Daily/Custom not plotted |
| TC-39 | Smart Practice | With history: start; without: open | Recommends lowest-accuracy category + weakest difficulty; 10 questions; empty state shows 🌱 and "PLAY A REGULAR GAME →" |
| TC-40 | Reset | Reset History as profile A | Gate → confirm → A's sessions gone, badges re-lock, stars 0, A's shop cleared; B's history intact. Dialog should disclose stars/shop (BUG-13) |
| TC-41 | Quiz Builder | Build 3-question quiz; save; Load Quiz; play | Listed alphabetically; questions in order; category "Custom: name"; difficulty Medium |
| TC-42 | Builder validation | Missing expression/answer; answer `1.5`; empty name; zero rows | Specific error dialogs |
| TC-43 | Builder edge | Expression with `"` or `\`; names "Quiz 1" vs "Quiz_1" | Round-trips; collision warned (BUG-10) |
| TC-44 **RB** | Persistence | Change profile, theme, scale, volume; earn/buy; close and reopen | Everything restored |
| TC-45 | Encryption | Open `config.properties` and the DB in an editor/SQLite viewer | Config is one Base64 blob; `profile_name, category, difficulty, grade, expression` are Base64; numbers/timestamps readable |
| TC-46 | Network | Run with a network monitor during a full session | No connections |
| TC-47 **RB** | Portable ZIP | On a clean Windows 10/11 without Java: unzip, run `AtelierArithmetic.exe` | App runs; SQLite works; sound works; emoji render; print dialog opens |
| TC-48 **RB** | MSI | Install (dir chooser, shortcuts), launch, uninstall; silent `msiexec /i … /qn /norestart` | Shortcuts present; uninstall removes program but leaves `~/.atelier-arithmetic` |
| TC-49 | Upgrade | Install previous MSI, create data, install new MSI | Replaces in place (same upgrade UUID); data intact |
| TC-50 **RB** | Signature | `signtool verify /pa` on exe and MSI | Verified; no SmartScreen block on signed EV build |
| TC-51 | Crash log | In a dev build force an exception on the EDT | Entry in `logs/error.log`; friendly dialog; app continues |
| TC-52 | No audio device | Disable audio output / run on VM without audio | App works silently |
| TC-53 | Help | Open Help from Home, Categories, Game and Results; scroll all 13 sections at 100 %, 125 % and 150 % scale, light and dark, at the 720×550 minimum window | No horizontal scrollbar; no stranded one-word lines; Back returns to Categories/Results (from there) or Home; facts match PRD (10 categories, shared stars, 25/50/75/100 % sound menu, 13 sections) |

---

## 8. Release checklist
- [ ] All P0/P1 items for the release are Done or explicitly waived (§6)
- [ ] `git status` clean; version updated in one place (REL-01); `CHANGELOG` entry (REL-06)
- [ ] Build on JDK 21 (and 17) with `-encoding UTF-8`; zero warnings
- [ ] Automated tests green (once DEBT-01 lands)
- [ ] `package.ps1` on a machine with WiX → MSI + ZIP; artefact hashes recorded
- [ ] Inner exe signed, MSI signed, `signtool verify /pa` OK (REL-02)
- [ ] §7 release-blocking cases (**RB**) pass on clean Windows 10 and 11 VMs without Java (REL-04)
- [ ] README/Help/PRD/architecture updated for any behaviour change
- [ ] Tag `vX.Y.Z`; publish artefacts; keep the upgrade UUID unchanged

## 9. Task template
```
#### <PREFIX>-<nn> — <title>  `P? · S|M|L`  [Blocked (decision Qn)]
- Evidence: <file:line / reproduction>
- Impact:
- Fix:
- Accept: <observable, testable>
- Docs: <PRD/architecture sections to update>
```

## 10. Verification log for this audit
| Check | Result |
|---|---|
| Read all 36 Java files, `run.bat`, `package.ps1`, `sign.ps1`, `README.md`, `DEPLOYMENT.md`, `.gitignore` | Done |
| `javac -encoding UTF-8 -cp "lib/*" -sourcepath src src/com/mathquiz/QuizApp.java -d <scratch>` (JDK 21.0.1) | Exit 0, no warnings |
| `dist/AtelierArithmetic/runtime/release` | Java 21.0.1; MODULES: java.base java.datatransfer java.xml java.prefs java.desktop java.logging java.security.sasl java.naming java.transaction.xa java.sql jdk.charsets |
| `git ls-files` for LICENSE | None found |
| `logo.png` type (`file`) | JPEG, 1024×1024 |
| Unused methods (grep call-sites) | `awardAchievementBonus`, `playAchievement`, `playStreak`, `playCategorySelect`, `playNotification`, `AnalyticsService.getMasteredCategories`, `getDailyChallengesCompleted` |
| Application was **not** executed during the audit | Run-time behaviour is inferred from code; TC-xx cases confirm it |
| README + Help fixes (2026-11-08) | 85 README edits and the Help rewrite applied by all-or-nothing scripts (every anchor matched exactly once; CRLF preserved); `javac -encoding UTF-8` exit 0; `HelpPanel` rendered **off-screen** (headless): 674 px wide, widest label 572 px → fits the 720 px minimum window. **Not yet seen in a live window** — run TC-53 |
