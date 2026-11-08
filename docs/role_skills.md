# Atelier Arithmetic — Roles, Skills and Working Agreements

| Field | Value |
|---|---|
| Document version | 1.0 |
| Date | 2026-11-08 |
| Purpose | Define **who** is needed to build, run and evolve this product, **what skills** each role needs (mapped to actual files and technologies in this repo), who decides what, and how to brief an **AI coding assistant** for each kind of task. |
| Context | The repository currently has a single maintainer, so one person usually wears every hat. The roles are separated here so work can be delegated, reviewed or handed to an assistant without ambiguity. |
| Related | [PRD.md](PRD.md) · [architecture.md](architecture.md) · [rules.md](rules.md) · [design.md](design.md) · [task.md](task.md) |

---

## 1. Role overview

| # | Role | One-line mission | Primary documents | Primary code/paths |
|--:|---|---|---|---|
| R1 | **Product Owner (PO)** | Decide *what* the product does for children, parents and teachers | PRD, task (priorities, open questions) | — |
| R2 | **Lead Engineer / Maintainer** | Keep the architecture healthy and the main branch releasable | architecture, rules | whole repo; `QuizFrame`, `QuizNavigator`, `config/`, `util/` |
| R3 | **Java/Swing Feature Engineer** | Implement features and fixes within the layering rules | rules, architecture | `view/`, `service/`, `model/` |
| R4 | **UX/UI Designer** | Keep the experience warm, clear, accessible and consistent | design | `view/*`, `AppTheme`, `tour/`, `resources/` |
| R5 | **Math Content & Pedagogy Designer** | Make questions, hints and feedback correct and age-appropriate | PRD §9, §8 | `QuestionGenerator`, `HintService`, `QuizSession` (grading text), Help/Tour text |
| R6 | **QA / Test Engineer** | Prove it works: automated invariants + manual regression | task §7 | `test/` (to be created), TC-xx |
| R7 | **Security & Privacy Reviewer** | Guard the "offline, child-safe" promise | rules §2, §8, architecture §8 | `CryptoHelper`, `SessionRepository`, `AppConfig`, `ParentalGate` |
| R8 | **Release / DevOps Engineer** | Produce signed, installable Windows builds | architecture §11, DEPLOYMENT | `run.bat`, `package.ps1`, `sign.ps1`, `IcoConverter`, `lib/` |
| R9 | **Technical Writer / Doc Owner** | Keep docs, in-app help and README truthful | rules §1 | `docs/`, `README.md`, `HelpPanel`, `TourManager` text |

> Any role may be an AI assistant working under a human owner — see §6.

---

## 2. Role profiles

### R1 — Product Owner
| Aspect | Detail |
|---|---|
| **Responsibilities** | Own the PRD; set priorities in `task.md`; answer the open questions (PRD §16: age range, per-profile stars, reset scope, daily-challenge policy, achievement rewards, question cap, platforms, licence); approve changes to grading, reward, achievement and adaptive numbers; accept features against the acceptance criteria. |
| **Decides** | Scope, rewards economy numbers (BR-02/03), grade table (BR-01), what is gated (FR-GTE-03), pricing of shop items, target audience, release go/no-go. |
| **Must-have skills** | Product definition for children's education; writing testable acceptance criteria; understanding of motivation design without dark patterns; basic child-privacy awareness (COPPA/GDPR-K principles). |
| **Should-have** | Teaching or parenting experience with 8–14-year-olds; analytics interpretation. |
| **Must know in the codebase** | PRD in full; `RewardService`, `QuizSession` grading, `AchievementService` rules at table level (no coding needed). |
| **Deliverables** | PRD updates, prioritised backlog, decisions recorded in PRD §16 / task `Blocked (decision)` items. |
| **Success measures** | Open questions resolved; every shipped feature traceable to an FR-ID. |

### R2 — Lead Engineer / Maintainer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Guard layering (R-ARC-*); review all merges; own `QuizFrame`/`QuizNavigator` contract, `AppConfig` keys and the SQLite schema; schedule debt work (DEBT-*); keep `main` releasable; approve dependency/JDK-module changes and rule exceptions. |
| **Decides** | Architecture changes, schema/migration strategy, exceptions to MUST rules, JDK baseline. |
| **Must-have skills** | Expert Java 17/21; Swing architecture (CardLayout, EDT, glass pane); JDBC/SQLite; Git (Conventional Commits); code review; refactoring legacy code safely without tests (characterisation tests). |
| **Should-have** | Build tooling (Maven/Gradle) to replace scripts; DI patterns; performance profiling. |
| **Must know in the codebase** | architecture.md entirely; `QuizFrame` wiring, tour registry, theming/scaling pipeline, `SessionRepository` internals. |
| **Deliverables** | Merged PRs, ADRs (architecture §13), updated exceptions register. |
| **Success measures** | Layering exceptions shrinking (rules §16); tests green; no regression escapes. |

### R3 — Java/Swing Feature Engineer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Implement tasks from `task.md` following rules.md; add/modify panels and services; write tests with each change; update docs in the same commit. |
| **Must-have skills** | Java 17; Swing layouts (`GridBagLayout`, `GridLayout`, `BorderLayout`, `CardLayout`), `Timer`, key bindings, `JTable`, `JOptionPane`; Java2D (`Graphics2D`, `GeneralPath`, `Area`); text/HTML in `JLabel`; EDT discipline; `PreparedStatement`; `javax.sound.sampled`; reading/writing `Properties`. |
| **Should-have** | `SwingWorker`; JUnit 5; `java.time`; accessibility API (`AccessibleContext`). |
| **Must know in the codebase** | Navigation contract; `applyTheme()` + `updateAllThemes()`; tour registry; how to add a category/achievement/shop item/setting (architecture §12). |
| **Typical tasks** | BUG-01…17, DEBT-03/06/10/12, FEAT-01/02. |
| **Success measures** | Definition of Done met (rules §13); zero compile warnings; light/dark and 100/125/150 % verified. |

### R4 — UX/UI Designer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Own design.md; define component styles; run accessibility reviews; design empty states, copy tone and Archie expressions; propose improvements within Swing's constraints. |
| **Must-have skills** | Interface design for children (8–14) and for adults (dashboards); typography and colour systems (light/dark tokens); WCAG 2.x contrast and keyboard accessibility; information hierarchy; microcopy in a supportive voice; asset prep (PNG/ICO, vector illustration for Archie). |
| **Should-have** | Swing/Java2D awareness (what is cheap vs expensive), emoji/font rendering behaviour on Windows, motion design. |
| **Must know in the codebase** | `AppTheme` tokens, panel layouts (design §7), tour overlay geometry, `MascotPainter`. |
| **Typical tasks** | FEAT-05, FEAT-09, DEBT-05 (style system), DEBT-13 (logo), contrast fixes in design §3.5. |
| **Success measures** | AA contrast for all text; consistent spacing scale; every screen has a designed empty state. |

### R5 — Math Content & Pedagogy Designer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Own PRD §9 (question specs) and the hint/feedback wording; ensure difficulty ladders are sensible; design new templates; review that every question has one integer answer and a suitable hint; review grade remarks and advice texts for growth-mindset tone. |
| **Must-have skills** | Primary/middle-school mathematics (arithmetic, fractions, percentages, ratios, patterns, linear algebra, measurement/geometry); misconception knowledge; scaffolding/hint design; assessment design; writing for ages 8–14. |
| **Should-have** | Basic programming (reading `QuestionGenerator`), probability to reason about randomised coverage. |
| **Must know in the codebase** | `QuestionGenerator` per-category methods, `HintService` routing (and BUG-07), `QuizSession.getRemarks`, Results advice, Help text. |
| **Typical tasks** | BUG-07 hint rewrite, FEAT-03, new templates, DOC-06 tour/Help wording. |
| **Success measures** | Zero invalid questions in 10,000-sample tests; hints exist for every template; teachers rate difficulty progression as appropriate. |

### R6 — QA / Test Engineer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Execute and extend the manual checklist (task §7); build the automated suite (DEBT-01); verify fixes against acceptance criteria; maintain test data (seeded DBs, fixture quizzes); run clean-machine installs. |
| **Must-have skills** | Test design (boundary values, state transitions, equivalence classes); JUnit 5 and property-style testing; Windows VM/sandbox management; SQLite inspection; reproducing UI defects in Swing; reading logs (`logs/error.log`). |
| **Should-have** | UI automation for Swing (AssertJ-Swing / Robot), accessibility testing (screen reader), performance measurement. |
| **Must know in the codebase** | PRD business rules (BR-01…08), data locations (architecture §7), how to isolate data with `-Duser.home`. |
| **Typical tasks** | TC-xx execution, DEBT-01, REL-04. |
| **Success measures** | All **RB** cases pass before each release; generators proven by tests; defects reproduced with steps. |

### R7 — Security & Privacy Reviewer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Review changes touching data, crypto, gate, file I/O, build secrets; keep claims honest (R-DOC-04, R-SEC-02); own the threat model (architecture §8); lead DEBT-08 (crypto upgrade) and FEAT-04 (backup) design reviews; confirm no network/telemetry creeps in. |
| **Must-have skills** | Applied cryptography (AES modes, GCM vs ECB, IVs, key management, KDFs, HMAC blind indexes); Windows DPAPI; secure coding in Java; SQL injection / path traversal / HTML injection; privacy-by-design and children's-data principles (COPPA, GDPR-K) — advisory, not legal advice; code-signing and supply-chain hygiene. |
| **Should-have** | Static analysis tools, dependency vulnerability checks (sqlite-jdbc), threat modelling (STRIDE). |
| **Must know in the codebase** | `CryptoHelper`, `AppConfig.load/save`, `SessionRepository` column encryption, `ParentalGate`, `sign.ps1` parameters. |
| **Typical tasks** | DEBT-08, REL-05, BUG-11, review of DEBT-07. |
| **Success measures** | No unreviewed crypto change; zero secrets in repo; documented limits match reality. |

### R8 — Release / DevOps Engineer
| Aspect | Detail |
|---|---|
| **Responsibilities** | Own `run.bat`, `package.ps1`, `sign.ps1`, DEPLOYMENT.md; produce MSI + ZIP; sign artefacts; maintain the upgrade UUID; record JDK, hashes, release notes; clean-machine smoke tests with QA; (future) CI. |
| **Must-have skills** | JDK tooling (`javac`, `jar`, `jlink`, `jpackage`, module selection via `--add-modules`); PowerShell scripting; WiX Toolset 3.11 / MSI behaviour (upgrade codes, silent install); Windows code signing (`signtool`, EV hardware tokens, PFX, RFC 3161 timestamps, SmartScreen reputation); Windows 10/11 deployment. |
| **Should-have** | CI (GitHub Actions on `windows-latest`), build reproducibility, SBOM generation. |
| **Must know in the codebase** | Architecture §11; `IcoConverter`; resource loading from the JAR; why `jdk.charsets`/`java.sql` are included. |
| **Typical tasks** | REL-01…06, DEBT-02, FEAT-06. |
| **Success measures** | A tagged release produces signed artefacts reproducibly; SmartScreen clean; upgrade preserves data. |

### R9 — Technical Writer / Documentation Owner
| Aspect | Detail |
|---|---|
| **Responsibilities** | Keep `docs/`, README, in-app Help and tour text consistent with the code (R-DOC-*); maintain the spec-vs-implementation table (PRD §14); write release notes. |
| **Must-have skills** | Precise technical writing; Markdown + Mermaid; ability to read Java well enough to verify claims; plain-language writing for children (Help). |
| **Should-have** | Screenshot capture and annotation; information architecture. |
| **Typical tasks** | DOC-03…06, REL-06 (DOC-01/02 done 2026-11-08). |
| **Success measures** | No statement in README/Help contradicts the code (checked each release). |

---

## 3. Skills catalogue and matrix

Legend: **●** primary (must be strong) · **○** supporting (working knowledge) · blank = not needed.

| Skill | Where it is used in this repo | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 | R9 |
|---|---|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| Java language & collections (17/21) | everywhere | | ● | ● | | ○ | ○ | ○ | ○ | ○ |
| Swing layouts, components, EDT, Timer, key bindings | `view/*` | | ● | ● | ○ | | ○ | | | |
| Java2D custom painting (charts, mascot, overlay) | `AnalyticsPanel`, `MascotPainter`, `TourOverlay` | | ○ | ● | ● | | | | | |
| `javax.sound.sampled` synthesis | `SoundService` | | ○ | ● | ○ | | | | | |
| JDBC / SQLite / SQL | `SessionRepository` | | ● | ● | | | ○ | ○ | | |
| Cryptography & key management | `CryptoHelper` | | ○ | ○ | | | | ● | | |
| Properties / file I/O, JSON by hand | `AppConfig`, `JsonHelper`, `CustomQuizService` | | ● | ● | | | ○ | ○ | | |
| Randomised generation & invariants | `QuestionGenerator` | | ○ | ● | | ● | ● | | | |
| Math content (arithmetic → algebra, measurement) | PRD §9 | ○ | | | | ● | ○ | | | |
| Hint & feedback pedagogy | `HintService`, remarks, advice | ○ | | | ○ | ● | | | | ○ |
| Gamification without dark patterns | `RewardService`, shop, streaks | ● | | ○ | ● | ○ | | ○ | | |
| Visual/typographic design systems; dark mode | `AppTheme`, design.md | ○ | | ○ | ● | | | | | |
| Accessibility (WCAG, keyboard, SR) | design §14 | ○ | | ○ | ● | | ● | | | |
| Microcopy for children | all UI strings | ● | | | ● | ● | | | | ● |
| Unit/property/UI testing; manual test design | `test/`, task §7 | | ○ | ● | | ○ | ● | | ○ | |
| Windows install testing (clean VMs) | REL-04 | | | | | | ● | | ● | |
| `jlink`/`jpackage`/WiX/MSI | `package.ps1` | | ○ | | | | | | ● | |
| PowerShell | `package.ps1`, `sign.ps1` | | ○ | | | | | | ● | |
| Code signing (EV/PFX/timestamp) | `sign.ps1` | | ○ | | | | | ○ | ● | |
| Git, Conventional Commits, review | rules §12 | | ● | ● | ○ | ○ | ○ | ○ | ● | ● |
| Privacy-by-design (COPPA/GDPR-K principles) | rules §2 | ● | ○ | ○ | ○ | | | ● | | ○ |
| Technical writing, Markdown/Mermaid | `docs/` | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ● |
| Performance analysis (EDT, JDBC) | DEBT-03/09 | | ● | ● | | | ○ | | | |

### 3.1 Proficiency scale
| Level | Meaning |
|--:|---|
| 1 | Aware — can follow explanations |
| 2 | Working — completes guided tasks |
| 3 | Proficient — works independently, follows rules |
| 4 | Expert — designs solutions, reviews others |

Minimum bar: R2 = 4 in Java/Swing/JDBC; R3 = 3 in Java/Swing; R7 = 4 in cryptography; R8 = 4 in jpackage/signing; R5 = 4 in math content; R4 = 4 in accessibility.

---

## 4. Responsibility matrix (RACI)

**R** = does the work · **A** = accountable (one per row) · **C** = consulted · **I** = informed.

| Activity | R1 PO | R2 Lead | R3 Eng | R4 UX | R5 Math | R6 QA | R7 Sec | R8 Rel | R9 Docs |
|---|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| Define/approve requirements (PRD) | **A** | C | C | C | C | C | C | I | R |
| Change grading, reward, achievement or adaptive numbers | **A** | C | R | C | C | R (tests) | I | I | R |
| Add/change question templates or hints | A | C | R | I | **R** | R (invariants) | I | I | C |
| UI/visual change | C | C | R | **A/R** | C | R | I | I | C |
| Accessibility audit and fixes | C | C | R | **A** | | R | | | I |
| Architecture / layering decisions | C | **A/R** | C | I | | I | C | C | R |
| Data schema or config-key change | C | **A** | R | | | R | C | I | R |
| Crypto, gate, file-I/O security change | C | R | R | | | C | **A/R** | I | C |
| Privacy/legal wording | A | C | | C | | | **R** | | R |
| Build scripts, JDK baseline | I | A | C | | | C | C | **R** | C |
| Release build, signing, publishing | A | C | | | | R | C | **R** | R |
| Clean-machine regression (TC RB cases) | I | I | C | | | **A/R** | | R | I |
| Bug triage and prioritisation | **A** | R | C | C | C | R | C | C | I |
| Keep README/Help/docs truthful | C | C | C | C | C | C | C | C | **A/R** |
| Rule exceptions | C | **A** | R | | | | C | | R (log) |

---

## 5. Ownership map (paths → role)

| Path / artefact | Primary | Reviewer(s) |
|---|---|---|
| `docs/PRD.md` | R1 | R9, R2 |
| `docs/architecture.md` | R2 | R7, R9 |
| `docs/rules.md` | R2 | R1, R7 |
| `docs/design.md` | R4 | R1, R6 |
| `docs/task.md` | R1 (priorities) / R2 (technical) | R6 |
| `docs/role_skills.md` | R1 | R2 |
| `src/.../QuizApp.java`, `view/QuizFrame.java`, `view/QuizNavigator.java` | R2 | R3 |
| `src/.../view/*Panel.java`, `view/tour/*` | R3 | R4, R2 |
| `src/.../config/AppTheme.java` | R4 | R2 |
| `src/.../config/AppConfig.java`, `util/*` (except `IcoConverter`) | R2 | R7 |
| `src/.../service/QuestionGenerator.java`, `HintService.java` | R5 | R3, R6 |
| `src/.../service/RewardService.java`, `AchievementService.java`, `AdaptiveDifficultyEngine.java` | R1 (numbers) / R3 (code) | R6 |
| `src/.../service/SessionRepository.java`, `AnalyticsService.java` | R2 | R7, R6 |
| `src/.../service/ParentalGate.java`, `util/CryptoHelper.java` | R7 | R2 |
| `src/.../service/SoundService.java` | R3 | R4 |
| `src/.../model/*` | R3 | R5 (grading text) |
| `src/.../resources/logo.png`, `logo.ico` pipeline | R4 | R8 |
| `run.bat`, `package.ps1`, `sign.ps1`, `util/IcoConverter.java`, `lib/` | R8 | R2, R7 |
| `README.md`, `DEPLOYMENT.md`, in-app Help/Tour text | R9 | R1, R5 |
| Future `test/` | R6 | R3 |

---

## 6. Working with an AI coding assistant

This repository is developed with AI assistance. Treat the assistant as a member of **R3 (and, when asked, R5–R9)** working under a human owner. Give it the context below; it must follow the same [rules](rules.md).

### 6.1 Standard context pack (read in this order)
1. `docs/rules.md` (all MUST rules) · 2. `docs/architecture.md` §4–§7 · 3. the PRD sections for the feature (IDs `FR-…`, `BR-…`) · 4. the task entry in `docs/task.md` (evidence + acceptance) · 5. the source files named in the task.

### 6.2 Playbooks
| Job | Read first | Typically touches | Verify with | Escalate to the human owner when… |
|---|---|---|---|---|
| **Bug fix** (`BUG-nn`) | task entry, PRD FR-ID, architecture section of the class | the cited file(s) + a test | compile (§6.3); reproduce before/after using the TC-xx case; light + dark | the fix changes a number, a stored value, or user-visible wording not covered by the task; a **Blocked (decision)** tag exists |
| **Feature** (`FEAT-nn`) | PRD scope, rules §3/§5, architecture §12 (extension guide) | service logic first, then panel, `QuizNavigator`, `applyTheme()`, tour/help text, PRD | compile; manual flow; update PRD IDs | new dependency, schema change, new admin function, new persisted key |
| **Question-content change** | PRD §9, rules R-DOM-*, `QuestionGenerator` | one generator method, `HintService`, PRD §9, tests | invariant sampling ≥ 10,000 per category×difficulty; hint exists | change of ranges that alters difficulty ladders |
| **Refactor / debt** (`DEBT-nn`) | rules §3, architecture §4.2 | multiple files; behaviour-preserving | tests (once present) or characterisation checks; grep checks (§6.3) | behaviour would change |
| **Docs** | rules §1, PRD §14 | `docs/*`, README, Help | cross-check each claim against code; links/anchors | a claim cannot be verified |
| **Release** (`REL-nn`) | architecture §11, DEPLOYMENT.md, task §8 | scripts only | run on clean machine; `signtool verify /pa` | any secret/PIN/certificate is needed — **never handle secrets in chat/logs** |

### 6.2.1 Prompt skeleton
```
Task: <ID and title from docs/task.md>
Read: docs/rules.md, docs/architecture.md (<sections>), docs/PRD.md (<FR/BR IDs>), docs/task.md (<ID>)
Constraints: follow R-ARC/R-UI/R-DOM rules; no new dependencies; no network; UTF-8.
Change only: <files>. Update: <docs sections>.
Verify: compile command from role_skills §6.3; list the TC-xx cases you checked.
Do not commit unless asked. If a decision is needed (Qn), stop and ask.
```

### 6.3 Standard commands (Windows; Git Bash/PowerShell)
| Purpose | Command |
|---|---|
| Compile check (no output files in repo) | `javac -encoding UTF-8 -cp "lib/*" -sourcepath src src/com/mathquiz/QuizApp.java -d <temp-dir>` |
| Run from sources | `run.bat` (compiles to `bin/`, launches) |
| Run with throw-away data | `java -Duser.home=<temp-dir> -cp "<out>;lib/*" com.mathquiz.QuizApp` (use `:` instead of `;` on macOS/Linux) |
| Layering check | `grep -rn "import com.mathquiz.view" src/com/mathquiz/service src/com/mathquiz/config` (expected today: `TourManager`; `AppTheme` imports `service`) |
| Hard-coded colour check | `grep -rn "new Color(" src/com/mathquiz/view` (should shrink — DEBT-05) |
| Unused sound/achievement hooks | `grep -rn "awardAchievementBonus\|playAchievement" src` |
| Package (**deletes `build/` and `dist/`**) | `powershell -File package.ps1` |
| Verify signature | `signtool verify /pa dist\AtelierArithmetic\AtelierArithmetic.exe` |

### 6.4 Hard "do not" list for assistants
* Do not run `package.ps1` unless a release build is requested (it wipes `dist/`).
* Do not read, modify or commit anything under the real `~/.atelier-arithmetic` folder; use a temp `user.home`.
* Do not add dependencies, network code, telemetry, or third-party SDKs.
* Do not change grading/reward/achievement/adaptive numbers, stored category strings, shop IDs or config key names without an explicit instruction **and** a PRD update.
* Do not weaken the parental gate or remove confirmations; do not describe encryption as "secure".
* Do not commit, push, or tag without being asked; when asked, keep the required `Co-Authored-By` trailer and cite task IDs.
* Do not paste or store signing PINs, passwords, certificates or keys.
* Do not "fix" documentation by guessing — verify against code (R-DOC-01).

---

## 7. Decision rights and open questions

| Decision | Owner | Pending question (PRD §16) | Blocks |
|---|---|---|---|
| Target age range | R1 | Q1 (8–14 vs 8–17) | Copy tone for future Help/tour revisions (DOC-06) |
| Shared vs per-profile stars | R1 | Q2 | BUG-05, FEAT-01 |
| Reset History scope | R1 | Q3 | BUG-13 |
| Daily challenge policy (once/day, per profile) | R1 | Q4 | BUG-04 |
| Achievement unlock reward/notification | R1 + R4 | Q5 | BUG-06 |
| Question-count cap | R1 | Q6 | BUG-15 |
| Additional platforms | R1 + R8 | Q7 | FEAT-06 |
| Licence & copyright holder | R1 | Q8 | DOC-05 |
| Crypto upgrade scheme | R7 + R2 | — (design review) | DEBT-08, FEAT-04 |
| JDK baseline (17 vs 21) | R2 + R8 | — | DEBT-02, REL-* |

**Escalation path:** engineer/assistant → Lead Engineer (technical or rule questions) → Product Owner (product, numbers, wording policy) → Security Reviewer (anything touching data, crypto, gate, secrets).

---

## 8. Onboarding path (first week)

| Step | Activity | Output |
|---|---|---|
| 1 | Read PRD §1–§6 and §8; skim §9 | Can explain the product and its rules |
| 2 | Read rules.md §2–§5 and §13 | Knows the guardrails and Definition of Done |
| 3 | Read architecture §4–§7 | Can draw the layers, the navigation and the data model |
| 4 | Build and run (`run.bat`); play every screen in light/dark and 3 scales; take the tour | Hands-on mental model; list of surprises |
| 5 | Run task §7 cases TC-01…TC-25 on a temp profile | Confirms known defects (BUG-01/02/03/07…) |
| 6 | Pick a starter task: **BUG-08** (S), **BUG-09** (S), **BUG-01** (S), **DOC-03** (S) | First merged change with docs updated |
| 7 | Read design.md before touching UI; read the tests you will extend | Ready for feature work |

**Starter-task pairing:** R3 + R2 for BUG-01/02/03; R3 + R5 for BUG-07; R8 + R6 for REL-03/04; R9 + R1 for DOC-06.

---

## 9. Collaboration agreements

| Topic | Agreement |
|---|---|
| **Definition of Ready** (task) | Has ID, evidence, acceptance criteria, linked PRD IDs; blocking decisions resolved |
| **Definition of Done** | rules.md §13 checklist |
| **Branching** | Feature branch per task, `main` always releasable (R-GIT-01) |
| **Commits** | Conventional Commits + task ID (R-GIT-02/03) |
| **Review** | One reviewer minimum; security-touching changes need R7; content changes need R5; UI changes need R4 |
| **Docs** | Updated in the same PR (R-DOC-02) |
| **Releases** | Cut after M-level exit criteria (task §5); checklist in task §8; R6 signs off clean-machine tests, R8 signs the artefacts |
| **Disagreements** | Rules win over preference; PO wins on product; Lead Engineer wins on architecture; Security Reviewer has veto on data/crypto/privacy |
