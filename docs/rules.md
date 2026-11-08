# Atelier Arithmetic — Project Rules and Conventions

| Field | Value |
|---|---|
| Document version | 1.0 |
| Date | 2026-11-08 |
| Applies to | Everyone (human or AI) who changes code, data, build scripts or docs in this repository |
| Related | [PRD.md](PRD.md) · [architecture.md](architecture.md) · [design.md](design.md) · [task.md](task.md) · [role_skills.md](role_skills.md) |

---

## 0. How to use these rules

* **MUST / MUST NOT** — mandatory. A violation blocks the change unless a documented exception exists.
* **SHOULD / SHOULD NOT** — default; deviate only with a stated reason in the commit/PR.
* **MAY** — optional.
* Every rule has an ID (`R-XXX-nn`). Cite the ID in reviews and commit bodies.
* The **Now** column states whether *today's* code complies: ✔ complies · ⚠ violated (linked to a task in [task.md](task.md)) · — prospective rule (no code affected yet).
* **Exceptions:** write the reason in the PR/commit, add a line to the "Exceptions" list in [task.md](task.md), and obtain the maintainer's approval for any MUST.
* Rules are derived from patterns that already exist in the code, plus guardrails stated in the PRD. They do not invent new product behaviour.

---

## 1. Documentation (R-DOC)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-DOC-01 | The running code is the source of truth. If code and docs disagree, either fix the docs or open a `BUG-` task — never silently change behaviour to match a doc, or vice-versa. | Prevents the README-drift found in the audit (PRD §14). | ✔ README/Help corrected 2026-11-08 |
| R-DOC-02 | A change to user-visible behaviour, grading/reward numbers, question ranges, config keys, DB schema, or build steps MUST update the matching PRD/architecture section **in the same commit**. | Keeps the spec trustworthy. | — |
| R-DOC-03 | Numeric specifications live in the PRD; the README stays a marketing-level overview. | One place to maintain. | ✔ README corrected and cross-linked to the PRD |
| R-DOC-04 | Docs MUST NOT claim legal certification (COPPA, GDPR-K) or "military-grade"/"secure" encryption. Use "privacy-by-design" and the limits in architecture §8.2. | Accuracy; legal risk. | ✔ |
| R-DOC-05 | New work items get a unique ID in [task.md](task.md) (`BUG-`, `DEBT-`, `FEAT-`, `DOC-`, `REL-`). | Traceability. | ✔ |
| R-DOC-06 | Every public class/non-trivial public method SHOULD have a Javadoc summary (existing style: purpose + constraints). | Onboarding. | ✔ mostly |
| R-DOC-07 | In-app help/tour text MUST be updated when the feature it describes changes. | Children rely on it. | ⚠ Help ✔; tour text — DOC-06 |

---

## 2. Product guardrails (R-PRD)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-PRD-01 | The app MUST NOT use the network at runtime (no sockets, HTTP, DNS, update checks, telemetry, remote fonts/images). Build tooling may. | Core privacy promise (PRD NFR-SEC-01). | ✔ |
| R-PRD-02 | MUST NOT collect or ask for personal data beyond what the educational function needs. Profile creation MUST keep the "do not use your real name" notice. | Child safety. | ✔ |
| R-PRD-03 | Child-facing text MUST use growth-mindset language at every score tier — no "bad", "fail", "stupid", shaming or red-alarm UI. The grade/remark table (PRD BR-01) MUST NOT change without Product Owner approval. | Product principle P1. | ✔ |
| R-PRD-04 | No time pressure: the timer is informational; MUST NOT add countdowns, time limits, speed penalties or leaderboards. | Principle P4. | ✔ |
| R-PRD-05 | Rewards MUST be earned in-app only and purely cosmetic: no real-money purchases, random paid rewards ("loot boxes"), expiring offers, or loss of owned items for inactivity. | Principle P5. | ✔ |
| R-PRD-06 | MUST NOT add ads, third-party SDKs, social/chat features or external links that open from child-facing screens. | Child safety. | ✔ |
| R-PRD-07 | Any new adult-only or destructive feature MUST be behind the parental gate and, if destructive, a confirmation dialog that states **everything** that will be deleted. | Principle P3. | ⚠ BUG-13 |
| R-PRD-08 | Copy SHOULD be readable by 8–14-year-olds: short sentences, concrete verbs, supportive emoji. | Audience. | ✔ |
| R-PRD-09 | Gameplay features MUST work without any adult gate and without the guided tour. | Usability. | ✔ |

---

## 3. Architecture and layering (R-ARC)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-ARC-01 | Dependency direction is `view → service → model/config/util`. New code MUST NOT add `service → view` or `config → service` dependencies. | Testability; layering ([architecture §4](architecture.md#4-layered-architecture)). | ⚠ DEBT-04 (6 legacy exceptions) |
| R-ARC-02 | Screens MUST navigate only through `QuizNavigator`. No panel may hold or call another panel. New navigation = new interface method + `QuizFrame` implementation. | Loose coupling. | ⚠ `WelcomePanel` calls `((QuizFrame) nav).updateAllThemes()` — add a navigator method (DEBT-05) |
| R-ARC-03 | Business rules (scoring, grading, rewards, streaks, achievements, recommendations, question generation, hint text) MUST live in `model`/`service`, never in a panel. | Single responsibility; unit-testable. | ⚠ streak maths duplicated in `WelcomePanel`; next-step rules in `ResultsPanel` (DEBT-06) |
| R-ARC-04 | Collaborators MUST be passed in through constructors. New code MUST NOT call `new SessionRepository()`, `AppConfig.getInstance()` or `new AnalyticsService(...)` inside a view or service when `QuizFrame` already owns one. | Avoids hidden state, redundant DB init, and enables tests. | ⚠ DEBT-04 |
| R-ARC-05 | Each screen is one `JPanel`, created once, registered as a `CARD_*` constant in `QuizFrame`, and exposes `applyTheme()` that `updateAllThemes()` calls. | Consistent theming/navigation. | ✔ |
| R-ARC-06 | A class SHOULD stay under ~500 lines; split when it exceeds. | Maintainability. | ⚠ `WelcomePanel` 1,142, `AnalyticsPanel` 844, `ResultsPanel` 636, `GamePanel` 563, `QuestionGenerator` 576 |
| R-ARC-07 | A constant (category list, difficulty names, badge count, app version) MUST have one definition. | Prevents drift. | ⚠ categories in 3 places; "10" badges hard-coded; version in 2 places (DEBT-05, REL-01) |
| R-ARC-08 | Persisted data goes through `AppConfig` (settings) or `SessionRepository` (history). Files outside `AppConfig.getAppDir()` are written only on an explicit user action (save dialog). | Predictable data footprint. | ✔ |
| R-ARC-09 | Do not introduce a dependency (JAR, JDK module) without maintainer approval, licence check, pinned version, and updates to `package.ps1` (`build/input`, `--add-modules`). | Offline, small, reproducible. | ✔ |

---

## 4. Java coding conventions (R-COD)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-COD-01 | Packages: `com.mathquiz.{config,model,service,util,view,view.tour}`. One public top-level class per file named like the class. | Existing layout. | ✔ |
| R-COD-02 | Source files are UTF-8 and MUST be compiled with `-encoding UTF-8`. For BMP symbols that must render on every font (e.g. ★) prefer `★` escapes. | Emoji literals exist in sources. | ⚠ DEBT-02 |
| R-COD-03 | Use only APIs available in **JDK 17** (supported baseline). Raising the baseline needs a documented decision. | Packaging uses 17/21. | ✔ |
| R-COD-04 | Style: 4-space indent, K&R braces, `camelCase` members, `PascalCase` types, `UPPER_SNAKE_CASE` constants, no wildcard imports in new files, no unused imports. | Matches existing code. | ✔ mostly |
| R-COD-05 | Model classes (`Question`, `QuestionResult`) are immutable; only `QuizSession` has documented mutable state (`difficulty`, results list). | Safe sharing. | ✔ |
| R-COD-06 | No empty `catch` where data could be lost. Minimum: report to `System.err`/`error.log`; for save/IO failures also tell the user. Ignoring is acceptable only for optional UX (look-and-feel, sound, icon). | Silent data loss found in audit. | ⚠ DEBT-07 |
| R-COD-07 | SQL MUST use `PreparedStatement` parameters; never concatenate input into SQL. | Injection safety. | ✔ |
| R-COD-08 | Name domain thresholds as constants with a comment (pattern: `AdaptiveDifficultyEngine`). | Readability; spec mapping. | ⚠ magic numbers in `RewardService`, `QuizSession`, `AchievementService` |
| R-COD-09 | Parse user input defensively (`NumberFormatException`) and answer with a child-friendly message. | UX. | ✔ |
| R-COD-10 | New date/time code SHOULD use `java.time`, not `Date`/`SimpleDateFormat`/`Calendar`. | Correctness; deprecated APIs. | ⚠ legacy in `AnalyticsService`, `WelcomePanel` (DEBT-06, DEBT-12) |
| R-COD-11 | MUST NOT call `System.exit` (frame close already exits) or `Thread.sleep` on the EDT. | Responsiveness. | ✔ |
| R-COD-12 | New asynchronous sound/IO SHOULD use a shared executor or `SwingWorker`, not ad-hoc `new Thread`. | Resource control. | ⚠ `SoundService` (DEBT-10) |
| R-COD-13 | Avoid code in `catch (Exception e) {}` blocks of the form "do nothing" in new files without a comment explaining why. | Debuggability. | ⚠ several legacy |

---

## 5. Swing and UI (R-UI)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-UI-01 | All Swing component creation/mutation MUST happen on the EDT. | Thread safety. | ✔ |
| R-UI-02 | Colours MUST come from `AppTheme` semantic getters (`getBgPrimary/Card/TextDark/TextMuted/BorderClr/AccentGold`) — no new hard-coded colours except semantic ones in [design.md §3.3](design.md#33-semantic-and-feature-colours). | Dark mode correctness. | ⚠ legacy private palettes (DEBT-05) |
| R-UI-03 | Every change MUST be checked in **both light and dark mode**. | Dark mode is persisted and common. | — |
| R-UI-04 | Every new panel implements `applyTheme()` and is added to `QuizFrame.updateAllThemes()`. | Consistent theme switching. | ✔ |
| R-UI-05 | Fonts: `Serif` for display titles, `SansSerif` for UI text, `Segoe UI Emoji` (inside HTML `<font face>`) for emoji; set a base size and let `AppTheme.scaleComponentFont` scale it. | Cross-screen consistency; accessibility. | ✔ |
| R-UI-06 | After building components dynamically (in `refresh()`/`populate…`), the new subtree MUST receive scaling and button-sound instrumentation. | Else content looks unscaled/silent. | ⚠ BUG-14 (verify) |
| R-UI-07 | Layouts MUST work at the minimum window size 720×550 at the default 125 % scale, and scroll (not clip) at 150 %. | Accessibility. | — verify |
| R-UI-08 | Every interactive control MUST be reachable and operable by keyboard; Enter triggers the primary action where natural; Esc backs out; no keyboard traps. | Accessibility. | ⚠ tour is mouse-only; analytics keyboard untested |
| R-UI-09 | Use `★` (U+2605) for star counts in text; use emoji only in emoji-capable fonts. | Rendering (fixed in commit `5cf780a`). | ✔ |
| R-UI-10 | MUST NOT run long DB/IO work on the EDT in new code; use `SwingWorker`. | Responsiveness. | ⚠ legacy (DEBT-09) |
| R-UI-11 | Every data-driven view MUST have an empty state with friendly wording. | UX. | ✔ |
| R-UI-12 | Dialogs: `JOptionPane`/`JDialog` with a parent component, child-friendly title (🦉 pattern), no stack traces shown (except the global crash dialog message). | UX consistency. | ✔ |
| R-UI-13 | When re-populating a panel, remove old listeners before adding new ones (or add the listener once). | Prevents duplicate handlers. | ⚠ BUG-02 |
| R-UI-14 | Text contrast SHOULD meet WCAG AA (4.5 : 1 normal, 3 : 1 large/bold). Gold accent text on light backgrounds fails for small text — use it only for large/bold text or add a darker variant. | Accessibility. | ⚠ FEAT-05 |
| R-UI-15 | Do not hard-code pixel sizes that depend on 100 % scale (e.g. fixed label heights) — prefer layouts and insets. | Scale support. | ✔ mostly |
| R-UI-16 | Text shown in `HelpPanel`: hard-wrap every line to ≤ ~66 characters (emoji-bearing lines shorter), indent continuation lines with `&nbsp;&nbsp;`, never use raw `<`/`>`, keep the pinned body width, and re-render off-screen to confirm there is no horizontal scrollbar at the 720 px minimum window. | Swing HTML labels do not auto-wrap; long lines make the whole page scroll sideways. | ✔ |

---

## 6. Domain invariants (R-DOM)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-DOM-01 | Every generated `Question` MUST have an `int` answer and no `int` overflow along the way. | Answer field is integer-only. | ✔ |
| R-DOM-02 | Division/fraction/percentage/ratio/algebra questions MUST be constructed so the answer is an exact integer; Difference results MUST be ≥ 1; algebra solutions are positive integers. | Child-appropriate; no remainders. | ✔ |
| R-DOM-03 | Generated expressions use ASCII operators `+ - * /`; `HintService` parsing depends on it. | Hint correctness. | ✔ |
| R-DOM-04 | `generateSeeded` MUST be a pure function of `(difficulty, category, seed, index)`. Do not introduce time/global-state dependence. | Daily Challenge fairness. | ✔ |
| R-DOM-05 | Values in PRD §8 (grading, star formula, achievement thresholds, adaptive constants, streak rule) MUST NOT change without a PRD edit and tests. | Spec integrity. | ✔ |
| R-DOM-06 | Rewards are awarded exactly once per **completed** session, **after** it is saved. Abandoned sessions are neither saved nor rewarded. | Streak maths and fairness. | ✔ |
| R-DOM-07 | Stored category strings are exactly: `Addition, Difference, Multiplication, Division, Mixed, Special, Fractions, Patterns, Algebra, Measurement, Daily Challenge, Custom: <name>`. They are persisted — never rename without a data migration. | History/achievement lookup is string-based. | ✔ |
| R-DOM-08 | Shop item IDs (`hat_top`, …) and profile names are persisted keys — never change or reuse them. | Saved purchases. | ✔ |
| R-DOM-09 | The daily challenge MUST be playable at most once per calendar day (per decision Q4 in the PRD). | Reward integrity. | ⚠ BUG-04 |
| R-DOM-10 | Mixed excludes Special and the advanced categories (PRD FR-QGN-03). | Spec. | ✔ |
| R-DOM-11 | A new generator sub-type MUST be added together with an invariant test (≥ 10,000 samples, all difficulties). | Prevents broken questions reaching children. | ⚠ no tests (DEBT-01) |

---

## 7. Data and persistence (R-DAT)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-DAT-01 | All data lives under `AppConfig.getAppDir()` (`~/.atelier-arithmetic`). | Single, documented footprint. | ✔ |
| R-DAT-02 | Multi-row DB writes MUST be one transaction with rollback. | ACID. | ✔ |
| R-DAT-03 | A schema change MUST ship with a migration strategy (introduce `PRAGMA user_version` first), be backward-compatible, and never drop user data. | Existing installs hold real data. | ⚠ DEBT-11 |
| R-DAT-04 | A new config key MUST have a safe default and tolerate missing/invalid values (never throw). Decide per-profile vs global and record it in architecture §7.1. | Stability. | ✔ |
| R-DAT-05 | Per-profile settings use the key suffix `.<profile>`. | Existing convention. | ✔ |
| R-DAT-06 | Config and quiz files SHOULD be written atomically (temp file + move) and backed up before overwrite. | Crash safety. | ⚠ DEBT-07 |
| R-DAT-07 | Reset/delete flows MUST disclose what is deleted and MUST affect only the active profile's data (plus any shared value explicitly named). | Trust. | ⚠ BUG-13, BUG-05 |
| R-DAT-08 | Logs MUST NOT contain answers, profile names or other personal data. | Privacy. | ✔ |
| R-DAT-09 | Custom-quiz writer and reader MUST remain symmetrical; characters needing escaping (`"`, `\`) MUST round-trip. | Data integrity. | ⚠ BUG-10 |

---

## 8. Security and crypto (R-SEC)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-SEC-01 | New sensitive string columns MUST be encrypted through `CryptoHelper`; profile names and expressions are never stored in clear. | Privacy. | ✔ |
| R-SEC-02 | Do not describe current encryption as strong security. It is machine-bound obfuscation (ECB, derived key). | Honesty ([architecture §8.2](architecture.md#82-honest-limits)). | ✔ |
| R-SEC-03 | Secrets (PINs, PFX files/passwords, keys) MUST NOT be committed. `*.pfx`, `*.p12`, `*.snk`, `*.key` SHOULD be added to `.gitignore`. Prefer environment variables or secure prompts over command-line secrets. | Supply-chain safety. | ⚠ REL-05 |
| R-SEC-04 | Any HTML written by the app MUST escape dynamic values. | Injection safety. | ⚠ BUG-11 |
| R-SEC-05 | Custom file names MUST be sanitised to `[A-Za-z0-9_-]`. | Path traversal. | ✔ |
| R-SEC-06 | When changing the crypto scheme, ship a migration that can still read old data (decrypt fallback) and test with real old files. | No data loss. | — |

---

## 9. Parental gate (R-GATE)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-GATE-01 | Call `ParentalGate.verifyParent(...)` **before** doing the protected action; treat cancel, wrong answer and exceptions as *denied*. | Safe default. | ✔ |
| R-GATE-02 | No "remember me" or session-long unlock. | Children share the PC. | ✔ |
| R-GATE-03 | Currently gated, and MUST stay gated: open Analytics, open Quiz Builder, Reset History, open Sound menu. | PRD FR-GTE-03. | ✔ |
| R-GATE-04 | Do not gate gameplay or child-facing rewards. | Usability. | ✔ |

---

## 10. Audio (R-AUD)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-AUD-01 | Every sound MUST pass through `SoundService` and respect mute/volume (`volumeFactor`). | Parent control. | ✔ |
| R-AUD-02 | Synthesise tones; do not ship audio files. | Size; zero dependencies. | ✔ |
| R-AUD-03 | Keep amplitudes child-safe: ≤ 0.6 × volume for effects, ≤ 0.12 for ticks/hover. | Hearing comfort. | ✔ |
| R-AUD-04 | Failure to open an audio line MUST be silent and non-fatal. | Machines without audio. | ✔ |
| R-AUD-05 | Do not leave unused sounds: wire them or delete them. | Dead code (4 unused). | ⚠ DEBT-10 |

---

## 11. Build, packaging and release (R-BLD)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-BLD-01 | Compile with `-encoding UTF-8` in `run.bat`, `package.ps1` and any CI. | Emoji in sources. | ⚠ DEBT-02 |
| R-BLD-02 | Release builds use JDK 21 (or 17), `JAVA_HOME` set; record the JDK version in release notes. | Reproducibility. | — |
| R-BLD-03 | The product version has **one** source and is applied to `package.ps1`, `HelpPanel`, installer names and docs. | Consistency. | ⚠ REL-01 |
| R-BLD-04 | Generated outputs (`bin/`, `build/`, `dist/`, `logo.ico`) are never committed; `lib/*.jar` is the only committed binary. | Repo hygiene. | ✔ |
| R-BLD-05 | The MSI upgrade UUID `71a2e7c3-3882-4df4-94fa-7e9b422a59cc` MUST never change. | Upgrades replace, not duplicate. | ✔ |
| R-BLD-06 | Release artefacts MUST be signed (inner `AtelierArithmetic.exe`, then MSI), time-stamped (RFC 3161), and verified with `signtool verify /pa`. | SmartScreen reputation. | ⚠ REL-02 |
| R-BLD-07 | Run the manual regression checklist ([task.md §7](task.md#7-manual-regression-checklist)) on a clean Windows machine **without Java** before publishing. | Catches missing JRE modules. | ⚠ REL-04 |
| R-BLD-08 | `package.ps1` deletes `build/` and `dist/` first — copy anything you need to keep. | Destructive script. | ✔ documented |
| R-BLD-09 | When adding a JDK API/module, re-verify the `--add-modules` list and the produced `runtime/release` MODULES line. | jlink strips unused modules. | — |

---

## 12. Git and collaboration (R-GIT)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-GIT-01 | Non-trivial work MUST be done on a branch and merged via review; `main` stays releasable. | Quality gate. | ⚠ history is direct-to-main |
| R-GIT-02 | Commit subjects follow Conventional Commits as in history: `feat`, `fix`, `style`, `docs`, `refactor`, `test`, `chore` + optional `(scope)` + imperative text, e.g. `fix(analytics): show latest 20 sessions in trend`. | Readable history. | ✔ |
| R-GIT-03 | Commit bodies reference task IDs (`BUG-01`, `DEBT-03`). | Traceability. | — |
| R-GIT-04 | AI-assisted commits MUST keep the tool-mandated `Co-Authored-By` trailer. | Attribution policy. | — |
| R-GIT-05 | MUST NOT force-push `main`, rewrite shared history, skip hooks, or commit secrets/user data (anything from `~/.atelier-arithmetic`). | Safety. | ✔ |
| R-GIT-06 | Keep commits atomic; docs-only changes in their own commit unless they describe the same change. | Review clarity. | ✔ |
| R-GIT-07 | A `.gitattributes` with `* text=auto` and `*.ps1 text eol=crlf`, `*.bat text eol=crlf` SHOULD exist (Git currently warns about LF→CRLF). | Stable diffs. | ⚠ DEBT-02 |
| R-GIT-08 | The working tree currently holds an uncommitted text fix in `TourManager.java`; commit it separately (`fix(tour): …`). | Hygiene. | ⚠ DOC-03 |

---

## 13. Testing and Definition of Done (R-TST)

| ID | Rule | Why | Now |
|---|---|---|:---:|
| R-TST-01 | Code MUST compile with **zero warnings**: `javac -encoding UTF-8 -cp "lib/*" -sourcepath src src/com/mathquiz/QuizApp.java -d <tmpdir>`. | Baseline verified clean. | ✔ |
| R-TST-02 | Once the test harness exists (DEBT-01), changes to generators, grading, rewards, streaks, achievements or the adaptive engine MUST include/modify unit tests. | Regression safety. | ⚠ no tests |
| R-TST-03 | A generator change MUST be validated with ≥ 10,000 samples per (category × difficulty) asserting: integer answer, exact division, positivity, expected format. | R-DOM-01/02. | ⚠ |
| R-TST-04 | UI changes MUST be exercised in light + dark, at 100 %, 125 % (default) and 150 % scale, with sound on and off. | Theme/scale/sound regressions. | — |
| R-TST-05 | Test three data states: **first launch** (no `.atelier-arithmetic`), **existing data** (upgrade), **empty profile**. | Migrations, empty states. | — |
| R-TST-06 | UI changes SHOULD attach before/after screenshots to the PR. | Review. | — |
| R-TST-07 | Never test against a developer's real data folder — use a throw-away user profile or a temporary `user.home` (`-Duser.home=…`). | Data safety. | — |

### Definition of Done (checklist)
- [ ] Builds with `-encoding UTF-8`, zero warnings
- [ ] Behaviour matches the PRD (or the PRD is updated in the same change)
- [ ] Layering rules (R-ARC) respected; no new duplicate constants
- [ ] Light + dark, 100/125/150 % checked; empty states present
- [ ] No new EDT-blocking work; no new network use; no new dependencies without approval
- [ ] Data changes: defaults, migration, per-profile vs global decided and documented
- [ ] Destructive/admin actions gated and disclosed
- [ ] Relevant manual regression cases run; tests added when a harness exists
- [ ] Help/tour text and docs updated
- [ ] Commit message follows R-GIT-02 and cites task IDs

---

## 14. Prohibited actions (summary)
1. Adding any network call, telemetry, ad or third-party SDK to the app.
2. Collecting names, emails, locations or any identifier of a child.
3. Introducing punitive wording, time pressure, or purchasable/random rewards.
4. Putting business logic in `view` classes or adding `service → view` dependencies.
5. Renaming persisted strings (categories, shop IDs, profile names, config keys) without a migration.
6. Changing grading/reward/achievement/adaptive numbers without a PRD update and tests.
7. Concatenating SQL, writing unsanitised file names, or emitting unescaped HTML.
8. Committing secrets, certificates, user data, or generated build outputs.
9. Running `package.ps1` when existing `dist/` contents must be kept.
10. Force-pushing `main` or bypassing hooks.

## 15. Reviewer checklist (quick)
| Question | Rule |
|---|---|
| Does it add a dependency, network access, or personal data? | R-PRD-01/02, R-ARC-09 |
| Is logic in a panel? Are services injected? | R-ARC-02/03/04 |
| Colours from `AppTheme`? Dark + scaled checked? | R-UI-02/03/06/07 |
| New data → default, migration, scope? Reset disclosed? | R-DAT-03/04/07 |
| Gate on adult features? Confirmation text complete? | R-PRD-07, R-GATE-* |
| Numbers/ranges changed → PRD + tests? | R-DOM-05/11, R-TST-02/03 |
| Messages child-friendly and growth-oriented? | R-PRD-03/08 |
| Commit message and task IDs correct? | R-GIT-02/03 |

## 16. Known non-compliance register
Existing violations are accepted as **debt** until their task is done; they do not license new violations.

| Rule | Where | Task |
|---|---|---|
| R-ARC-01/04 | `AppTheme`→`SoundService`, `TourManager`→view, `ParentalGate`→Swing, `SessionRepository`→`AppConfig` singleton, panels creating services | DEBT-04 |
| R-ARC-06 | Five classes > 500 LOC | DEBT-05 |
| R-ARC-07 | Duplicate category/token/version definitions | DEBT-05, REL-01 |
| R-COD-02 / R-BLD-01 | No `-encoding` flag | DEBT-02 |
| R-COD-06 | Silent persistence failures | DEBT-07 |
| R-UI-06 | Unscaled dynamic content | BUG-14 |
| R-UI-13 | Listener accumulation in `ResultsPanel` | BUG-02 |
| R-DOM-09 | Daily Challenge re-entry | BUG-04 |
| R-DAT-07 | Reset wipes stars/shop without saying so | BUG-13 |
| R-SEC-04 | Unescaped HTML report | BUG-11 |
| R-TST-02/03 | No tests | DEBT-01 |
