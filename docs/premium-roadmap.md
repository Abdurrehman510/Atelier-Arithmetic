# Atelier Arithmetic — Premium ("Family Plus") Roadmap

| | |
|---|---|
| **Status** | Draft v1. A brainstorm turned into a roadmap. **Not an approved design.** Nothing here is built. |
| **Date** | 2026-11-08 |
| **Audience** | Product owner / builder |
| **Source** | EdTech strategy + child-development brainstorm, grounded in a read of the current codebase |
| **Review** | Part G marketing review completed 2026-11-08 with the `marketingskills` skills. Competitor facts partly verified by web search; pricing and packaging remain hypotheses to test. Decisions for the owner are in Part I. |

---

## Part A — Strategy

### A.1 Goal

Turn Atelier Arithmetic from a well-built arithmetic quiz into a premium **adaptive maths-and-reasoning coach for ages 8–15**. Parents are the buyer and the child is the user. The premium tier must offer something a free quiz app cannot: **explaining why a child is stuck, and giving the family a concrete next step.**

### A.2 Positioning (working draft)

> Not a quiz app. The adaptive maths-thinking coach that shows parents *why* their child is stuck, works offline, and needs no account.

**Parent jobs to be done**
1. Close gaps early without becoming the nagging tutor.
2. Make screen time feel productive.
3. Know what to do this week.

**Four forces**

| Force | Content |
|---|---|
| Push | Quizzes and worksheets show a score but not the reason for the mistakes; tutoring is expensive; screen-time guilt |
| Pull | Error diagnosis, printable targeted worksheets, privacy (offline, no account) |
| Habit | School-assigned apps, paper workbooks |
| Anxiety | "Is it just a game?" / "Will my child get hooked?" / "What data does it collect?" / "Does it match school?" |

**Differentiation (revised by the Part G review).** Khan Academy offers a free Parent Dashboard (mastery, minutes, skills practised, assignable practice), so a progress dashboard alone does not justify a purchase. The paid value rests on four hypotheses (confirm by hands-on trials and parent interviews): explained error types, printable targeted worksheets with a paper check-in loop, offline privacy with no account, and one flat family price. See G.1–G.2.

### A.3 Principles and anti-goals

**Principles**
- Reward mastery and process, not volume.
- Serve autonomy, competence and relatedness (Self-Determination Theory).
- Adapt silently; keep the child in control (a visible "challenge dial").
- Parents get actions, not just charts.
- Offline-first and private by default; any online feature is opt-in, parent-gated and collects the minimum.

**Anti-goals (this is a failure even if it "works")**
- No loot boxes, variable-ratio rewards, FOMO sales or pay-to-win.
- No streak-loss punishment or guilt messaging.
- No persistent identifiers or child PII leaving the device.
- No "boosts IQ / brain training" claims and no clinical or diagnostic claims.
- No ranking of siblings against each other.

### A.4 Evidence key and claims policy

Labels are the author's reading of the research and **should be verified before they appear in marketing copy**: **[S]** strong, **[M]** moderate, **[C]** contested.

| Idea | Evidence | Source |
|---|---|---|
| Retrieval practice / testing effect | [S] | Roediger & Karpicke 2006 |
| Spaced (distributed) practice | [S] | Cepeda et al. 2006 |
| Interleaved practice in maths | [S] | Rohrer et al. 2015 and later classroom trials |
| Worked examples, fading, self-explanation | [S] | Sweller; Renkl; Chi et al. |
| Concreteness fading | [S] | Fyfe et al. 2014 |
| Fraction knowledge predicts later algebra | [S] | Siegler et al. 2012 |
| Spatial skills are malleable and predict STEM success | [S] | Uttal et al. 2013; Wai et al. 2009 |
| Bayesian Knowledge Tracing | [S] | Corbett & Anderson 1995 |
| Elo ratings in adaptive learning | [M] | Pelánek 2016 |
| ~85% success as a learning sweet spot | [M] | Wilson et al. 2019 (heuristic, not a law) |
| Hypercorrection of confident errors | [M] | Butterfield & Metcalfe 2001 |
| Protégé effect (teaching a helper) | [M] | Chase et al. 2009 |
| Parent maths anxiety affects children; a home maths app offset it | [M] | Berkowitz et al. 2015 (*Science*) |
| Extrinsic rewards can crowd out interest | [S] | Deci, Koestner & Ryan 1999 |
| Working-memory "brain training" far transfer | **[C]** | Melby-Lervåg & Hulme 2013, 2016 |
| Growth-mindset interventions | **[C]** (small effects) | Sisk et al. 2018 |
| "Learning styles" | Debunked | Pashler et al. 2008 |

**Claims policy.** Say "built on practice methods with strong research support (retrieval, spacing, interleaving, worked examples)". Do **not** say "boosts brain power". The FTC settled with Lumosity in 2016 over deceptive brain-training claims. Present attention and focus metrics as practice indicators, not diagnoses.

---

## Part B — Current state (from the code, 2026-11-08)

| Finding | Where | Consequence |
|---|---|---|
| Every question is a typed integer answer | `model/Question.java` (`expression`, `int correctAnswer`) | Interactive mechanics need a typed `Task` model |
| Per-question data is thin | `session_questions` table: expression, correct_answer, user_answer, correct, time_ms | No skill tag, error type, hints, attempts or confidence → a dashboard can only show percentages |
| Difficulty adaptation is rule-based | `AdaptiveDifficultyEngine` (3 levels, 5-right-up / 3-wrong-down, 5-question cooldown, Yes/No prompt) | Replace with skill-level mastery tracking |
| Hints parse the expression string | `HintService` | Fragile; generators should emit a structured solution trace |
| Analytics are per category | 10 categories; radar chart, heatmap, trend | Need roughly 60–100 micro-skills |
| Count-up timer on every question | `GamePanel` | Possible maths-anxiety trigger; make it optional |
| 🔥 daily streak counter | Home dashboard | Replace with a weekly rhythm |
| Emoji used as UI icons | Views | Replace with a consistent vector icon set for a premium look |
| Offline, AES-128 encrypted, no account, Windows MSI | README | A selling point; also a distribution constraint (Windows only) |

---

## Part C — Roadmap

Sizes are relative: **S** (days), **M** (1–2 weeks), **L** (several weeks), **XL** (a month or more).

| Phase | Theme | Features | Why this order |
|---|---|---|---|
| **0** | Foundation | G0, A1 (initial set), A8, schema changes (Part E) | Everything else depends on these. No visible feature on its own. |
| **1** | The paid hook | P1, P2, P3, P10, P11, P12, P14 (basics), P16, C1, A7, C6, C7 | The two hypotheses to prove first: parents pay for *explained errors* and *printable targeted worksheets* |
| **2** | Adaptive v2 | A2–A6, P4–P9, P13 | Needs the Phase 0 data and enough sessions to be meaningful |
| **3** | New mechanics | G1, G2, G3, G4–G8, C2–C5 | Highest development cost; build after the hook is validated |
| **4** | Progression and community | R1–R7, R8, R9, R11, then R10, then P15 | R10 is the only part needing a server and legal review; do it last |
| **L** | Launch track (parallel to Phase 1) | L1–L4 (Part G.6) | Launch v1 on Phase 1 scope only; Phases 3–4 ship as later releases |

**Phase exit criteria (proposed)**
- **0:** Existing quizzes run unchanged on the new `Task` model; new columns populated for every answered question; unit tests on solution traces.
- **1:** A pilot family can finish a session, see a plain-language weekly summary, print a worksheet and log its results. Willingness-to-pay evidence collected (Part F).
- **2:** Skill mastery estimates stabilise (no wild swings between sessions); the state machine fires on recorded test scenarios; no regressions in the Phase 1 flows.
- **3:** Each mechanic passes child usability sessions (Part F) with no unaided drop-off at the instructions screen.
- **4:** Weekly seed produces identical question sets on two separate machines; legal sign-off before any online data flow.

---

## Part D — Feature catalogue

Each feature has an ID, what it is, why it sells (parent view), how it helps (child development view), and its dependencies.

### D.1 Interactive gameplay and mechanics

**G0 — Task framework (foundation, M–L).** Replace `Question` with a `Task` interface: `createView()`, `evaluate()`, skill tag, solution trace, misconception detectors. Animate with a 16 ms `javax.swing.Timer` and Java2D. Every drag interaction gets a keyboard alternative, a drag threshold and a reduced-motion setting. Correct/wrong is shown with icon + text, never colour alone.

**G1 — Function Machines / Rule Detective (L).** Numbers enter a hidden machine. The child has a limited number of probe inputs, infers the rule, builds it from draggable blocks (×2, −3) and predicts the output for a large n. Later levels run machines backwards (equation solving).
- *Sells:* a detective game that teaches algebra.
- *Helps:* inductive reasoning, hypothesis testing (which probe is most informative?), function thinking.

**G2 — Balance Lab (L).** A pan balance with mystery bags (x). Only balance-preserving moves are allowed ("remove 2 from both sides"). The move history is the proof, scored on elegance. The same scene hosts logic puzzles ("A outweighs B, B outweighs C").
- *Sells:* algebra learned without memorised rules.
- *Helps:* equivalence, deduction, transitivity.

**G3 — Constraint Castle (L).** A Calcudoku-style grid with arithmetic cages and a unique solution. A Java backtracking solver generates and verifies puzzles offline. A pencil-marks tool externalises working memory. (Avoid the trademarked name "KenKen".)
- *Sells:* a Sudoku-style puzzle that drills arithmetic.
- *Helps:* constraint reasoning, planning, fact fluency.

**G4 — Number-line slider (S–M).** Place fractions, decimals or roots by dragging, with magnetic snap and an error-distance reveal. *Helps:* magnitude sense [S].

**G5 — Area-model builder (M).** Drag to partition rectangles to show products and fraction products. *Helps:* concrete-to-abstract transfer [S].

**G6 — Geometry sandbox (M).** Draggable vertices with live angle, perimeter and area readouts and goal prompts ("area 24, perimeter 20"). *Helps:* relationships over formulas.

**G7 — Net folder / mental rotation (M).** Fold cube nets, match rotated shapes. *Helps:* spatial skill [S].

**G8 — Estimation sliders (S).** Fermi-style "about how many?" with a tolerance band. *Helps:* number sense, plausibility checks.

### D.2 Cognitive growth and metacognition

**C1 — Mistake Lab (M–L).** About 30 seconds, skippable, shown after a wrong answer:
1. **Normalise.** No red ❌. Archie says "Interesting! Let's follow your thinking." The child's answer and the correct one appear on a number line or visual.
2. **Diagnose.** The wrong answer is matched to a known misconception (e.g. 43−18=35 smaller-from-larger; 3+4×2=14 left-to-right). Archie asks "Did you add before multiplying?" and the child answers Yes / Not quite.
3. **Reflect.** The child taps a cause chip (rushed / didn't know the method / small slip / guessed / confusing wording). The journal records patterns.
4. **Repair.** A worked example, then a near-twin question with new numbers. Success earns a "Bug Squashed" badge and schedules review at +1 and +3 days.
- *Sells:* addresses the *reason* for mistakes, not just the score.
- *Helps:* attribution, self-monitoring, repair. Depends on G0, A8, A5.

**C2 — Find Archie's Bug (M).** Archie shows a worked solution containing one mistake; the child taps the wrong step. *Sells:* critical thinking. *Helps:* error analysis in an emotionally safe frame [M].

**C3 — Strategy Studio (M).** Name and teach make-ten, compensation, doubling/halving, split-and-add; occasionally ask "which strategy did you use?". *Helps:* flexible thinking rather than one memorised algorithm.

**C4 — Memory Lane (M).** Chained steps shown one at a time with an optional scratchpad. Marketed as **mental-arithmetic stamina, not IQ** [C].

**C5 — Spatial Lab (M).** Rotation, nets, symmetry, tangrams (builds on G7).

**C6 — Think-Time and confidence tags (S–M).** Hints unlock after a short pause; the child may predict before a reveal; before submitting the child picks sure / maybe / guess. *Helps:* effortful retrieval [S]; calibration (feeds P5).

**C7 — Calm Mode (S).** Timer hidden or optional (parent setting), timing data still recorded. Not all children are affected by timers, so the dashboard (P7) measures the effect per child.

### D.3 Adaptive engine and Smart Hints (all on-device)

**A1 — Skill graph (L).** 60–100 micro-skills with prerequisites (e.g. `ADD.2digit.carry`, `FRAC.compare.unlike`). Generators expose difficulty knobs: digits, carries, operand count, distractors.

**A2 — Mastery tracking (M).** Bayesian Knowledge Tracing or Elo-style ratings per skill; a skill counts as mastered at p ≥ 0.95 across at least 2 days. New table `skill_state`.

**A3 — Item selection (M).** Choose the next item for ~80–85% predicted success (heuristic).

**A4 — Multi-signal state machine (M).** Signals relative to the child's own per-skill baseline: correct/wrong, speed, hint level, answer edits, idle time, skips.

| State | Detected by | Response |
|---|---|---|
| Bored | Long fast-correct run | Raise difficulty silently, or add a puzzle mechanic |
| Struggling | Slow, wrong, hints | Worked example plus an easier twin |
| Frustrated | 2+ wrong with rising time, skips, rapid random entries | Archie break, hide timer, switch to a strength skill, end on a win |
| Fatigued | Accuracy decay versus the session's first quarter | "Great stopping point." |

Pacing: roughly 10 / 15 / 20 minutes for ages 8–9 / 10–12 / 13–15, with 30–40% interleaved review. Always try to end a session on a success.

**A5 — Spaced review scheduler (M).** Leitner/SM-2-style due dates per skill; a "Daily Warm-up" of about 5 due items.

**A6 — Challenge dial (S–M).** Replaces the Yes/No difficulty dialog. The system suggests, the child decides.

**A7 — Smart Hint ladder (M).** Never reveals the answer before the last rung.

| Rung | Content |
|---|---|
| 0 | Highlight the key numbers: "What do you already know?" |
| 1 | Name the strategy type |
| 2 | Socratic question |
| 3 | Partially worked scaffold with blanks (area model / number line) |
| 4 | Fully worked twin problem, then return to the original |
| 5 | Reveal, then schedule a near-twin |

Hints are chosen from the misconception detected in the wrong attempt. Hint use gives partial mastery credit and no star penalty for the first hint.

**A8 — Structured solution traces (M).** Generators emit a step tree instead of `HintService` string parsing. Foundation for C1, A7 and P3.

### D.4 Parent Companion dashboard

Open with a 30-second plain-language summary. Charts sit underneath.

| ID | Feature | Notes |
|---|---|---|
| **P1** | Narrative summary | Generated from data: "Maya mastered 3 skills this week. The main gap is carrying in subtraction." |
| **P2** | Skill map | Micro-skill states (Not started / Learning / Fluent / Mastered) with icon + label; curriculum tags (Common Core, UK NC, Singapore, IB, CBSE) |
| **P3** | Error-type breakdown | Carry, sign, order of operations, fact recall, misreading. The key differentiator versus percentage-only apps |
| **P4** | Fluency vs accuracy quadrant | Automatic / careful / rushing / struggling |
| **P5** | Metacognition metrics | Confidence calibration, hint-independence trend, self-correction rate, Fix-it success |
| **P6** | Focus indicators | Accuracy by minute in session, time-of-day patterns. Practice indicators, not diagnoses; not a clinical tool |
| **P7** | Timer effect | Accuracy with versus without the timer, per child |
| **P8** | Strengths profile | Computation, pattern/algebra, spatial, logic, estimation, memory span, compared with the child's own baseline, not other children |
| **P9** | Predictions | Projected mastery date, plateau flag, review-due flag, "ready to advance". Show an uncertainty range; withhold below a minimum data threshold |
| **P10** | Printable adaptive worksheets (PDF) | Worked-example box, ~10 targeted problems interleaved with a few strong skills, 2 "find the mistake" items, parent answer key explaining likely errors |
| **P11** | Worksheet Check-in | A seed code in the footer lets the parent tap right/wrong per item; mastery updates. Closes the paper-to-app loop |
| **P12** | Parent coach cards | "Try asking…" and "avoid 'I was never a maths person'" (static content, early win) |
| **P13** | Parent controls | Daily cap, bedtime lock, "this week's focus" assignments matched to the school unit |
| **P14** | Trust features | Weekly report as PDF/HTML with no server, CSV export, one-click data delete, multi-child view with no sibling ranking |
| **P15** | Teacher / tutor PDF | For parent-teacher meetings |
| **P16** | Insight preview (free tier) | Parent-area only, behind the ParentalGate: shows one real insight from the child's own data and previews the locked skill map and worksheet page 1. The conversion moment (Part G.4) |

**Chart rules** (dataviz): one y-axis per chart, never dual-axis; direct labels and a legend for 2+ series; a table-view toggle; status colours reserved for status and always with an icon and label; run the palette validator for colour-blind separation. Recommend replacing the 10-spoke radar with a sorted horizontal bar or dot plot (author's judgement).

### D.5 Retention, engagement and global features

| ID | Feature | Notes |
|---|---|---|
| **R1** | Atelier progression | Each mastered skill restores a room in Archie's Atelier (multiplication → Gear Room, fractions → Bakery, spatial → Map Room, algebra → Observatory); the child places artifacts (autonomy) |
| **R2** | Feather evolution | One feather per mastered domain; no randomness |
| **R3** | Stars redesign | Award for stretch problems solved first try, Fix-it successes, new strategies, falling hint dependence, good calibration. Daily cap; "Archie closes the atelier for today" |
| **R4** | Weekly rhythm | "N of 7 days" ring with rest days built in; replaces the daily streak and has no loss message |
| **R5** | Teach Archie | The child explains steps or corrects Archie's reasoning (builds on C2) |
| **R6** | Family Rewards and certificates | Parent defines real-life rewards tied to mastery milestones; printable certificates |
| **R7** | Narrative chapters | One chapter every couple of weeks, unlocked by milestones, each built around a reasoning puzzle type |
| **R8** | Seeded weekly challenge | Seed from the UTC ISO week; generated on-device (`java.util.Random` is specified by its Javadoc, so identical sets worldwide, offline). Score = accuracy + elegance (steps, hints) with a time cap; default comparison is the child's own past self |
| **R9** | Result codes | Short signed string (e.g. `W45-7F3A-9K2Q`) pasted into a local leaderboard (family, classroom, cousins abroad). No accounts, no PII. A client-side key is extractable, which is acceptable for low-stakes use |
| **R10** | Optional online layer | Opt-in, parent-gated. Static read-only JSON on a CDN for challenge announcements and content packs; a "Global Community Quest" anonymous counter; percentile bands from aggregate score buckets with **no IDs**. **Legal review required** (COPPA / GDPR-K treat some persistent identifiers as personal data) |
| **R11** | Local co-op | *Asymmetric Puzzle* (two children each see half the clues and must talk) and *Parent–Child Duo*; class "quest code" extending the Custom Quiz Builder |

---

## Part E — Data model and architecture (proposal)

**`session_questions` — add columns:** `skill_id`, `attempts`, `hints_used`, `max_hint_level`, `confidence` (1–3), `error_type`, `timer_visible` (0/1), `mode` (screen / worksheet).

**New tables:**
- `skill_state(profile, skill_id, p_known, last_seen, due_at, median_ms)`
- `review_schedule(profile, skill_id, due_at, reason)`
- `journal(profile, ts, text, source)`
- `worksheet(id, profile, seed, created_at, skills)` and `worksheet_item(...)`

**Code structure**
- `Task` + per-mechanic view classes; generators emit solution traces and misconception detectors.
- Replace `AdaptiveDifficultyEngine` and `HintService` behind interfaces so Phase 0 can ship before the new engine.
- All adaptive logic is pure Java and unit-testable; no ML dependencies.
- Migrations must preserve the existing encrypted SQLite data and profile history.

---

## Part F — Validation plan

1. **5–8 parent interviews:** the last time they paid for or cancelled a learning tool, and what they would pay for.
2. **5–8 child sessions per age band (8–10, 11–12, 13–15):** prototype Function Machines and the Mistake Lab; watch for unprompted replay and time-to-understand.
3. **2–4 week pilot with 10–15 families:** test the two paid-hook hypotheses (P10/P11 worksheets and the P1 weekly summary).
4. **Waitlist / pricing page** to measure intent to pay.

---

## Part G — Marketing review (completed 2026-11-08)

**Method.** Reviewed with skills from the `marketingskills` marketplace (v2.14.2): `product-marketing`, `pricing` (+ pricing-models reference), `paywalls` (+ mobile-subscription reference), `launch`, `competitors`, `offers`, and the first part of `churn-prevention`. They were read from the local marketplace clone because the plugin was added but not yet installed. **Not applied:** `cro`, `copywriting`, `marketing-psychology`, `customer-research`, `referrals`, `free-tools`, `ab-testing`. These skills are written mainly for SaaS and mobile subscription apps; Family Plus is consumer desktop software bought by parents, so every recommendation below is an adaptation and says so.

**Confidence.** Competitor facts come from one round of web search (third-party sources unless noted). Pricing and packaging remain **hypotheses to test**, not conclusions.

### G.1 Competitor check (as of 2026-11-08)

Rule applied from the `competitors` skill: "not found in a search" is not "doesn't have". Nothing below claims a competitor lacks a feature.

| Competitor | Roadmap said | What the check found | Status |
|---|---|---|---|
| Khan Academy | "free mastery learning" | Khan's own 2025 blog describes a **free Parent Dashboard** showing learning minutes, skills practised and skills mastered, with the ability to assign practice units. | **Current, and it changes the strategy** (G.2) |
| Prodigy | "game-first with a membership upsell" | The base game is free (third-party). Third-party pages list Math tiers around $9.95 / $14.95 / $19.95 per month; Prodigy's own 2025 pages show older, higher prices. Third-party sources say parent-portal visibility varies by tier. | Prices **unverified**; "game-first" is my characterisation, not sourced |
| IXL | "subscription adaptive practice with analytics" | Subscription confirmed (third-party: about $9.95/month or $79/year for one subject; extra children reported at about $40/year; frequent coupons). The search did **not** confirm how adaptive practice or parent reports work. | Subscription current; adaptive and analytics claims **unverifiable** |
| DragonBox | "algebra via manipulation" | **Product changed.** The original DragonBox Algebra 5+ was removed from Google Play on 2021-12-21; the current app is "Kahoot! Algebra by DragonBox". Third-party trackers disagree on price ($5.99/month, $35.99/year, $19.99 one-time, or a Kahoot! Kids subscription). | **Changed**; pricing unverified |

**Before any competitor claim goes into public copy:** hands-on trial of each product, the product's own pricing page, and an "as of" date on every fact.

### G.2 Positioning change

Khan's free Parent Dashboard means **a progress dashboard on its own is not a paid differentiator.** Parents can already get mastery progress for free. The earlier draft treated the dashboard as the headline; it should be the delivery vehicle for four things I did not find evidence of in this check (absence is **not** established; these are hypotheses to confirm by hands-on trials and parent interviews):

1. **Why the child is stuck:** error-type diagnosis (P3), not just percentages.
2. **A physical next step:** targeted printable worksheets plus the paper check-in loop (P10, P11).
3. **Privacy by architecture:** offline, no account, encrypted.
4. **A family price:** one flat price for several children (see G.3).

Updated one-liner: *The adaptive maths-thinking coach that tells you why your child is stuck and hands you this week's worksheet, with no account and nothing leaving your computer.*

### G.3 Pricing and packaging (hypotheses, from the `pricing` and `offers` skills)

- **Anchors (third-party, unverified):** IXL about $79/year for one subject; Prodigy Core about $58.95/year. Floor of differentiation: Khan, free. Price between the next-best alternative and the value delivered, never by matching competitors.
- **First price is a bet to learn from, not an optimum.** Starting bets to test: **Arm A**, a flat annual family price near **$79**; **Arm B**, a one-time licence at roughly **2–2.5× the annual price**. The skill's rule of thumb is to start higher and avoid the ultra-low "$9 trap", which creates false traction and attracts the customers who churn most. These numbers are derived from the anchors above, not from demand data.
- **Value metric: per family, flat** (up to N child profiles), not per child or per subject. The skill says to reach for flat-rate first. Competitors appear to charge per child or per subject (third-party), so this is also a differentiator.
- **Don't gate the feature that delivers core value** (pricing-models reference). This **changes the earlier Free/Plus draft**:
  - **Free:** all practice, the current rule-based adaptive difficulty, the hint ladder (A7) and a basic Mistake Lab (C1 steps 1–3). The child-facing learning loop stays free; it is the trust and word-of-mouth engine, and withholding help from children would contradict the positioning.
  - **Family Plus:** parent insights (P2–P9), printable worksheets and check-in (P10, P11), skill-level adaptive engine (A2–A5), the Mistake Lab "repair" round with spaced review, mechanic packs (G1–G8), multi-child profiles, teacher/tutor PDF.
  - Decision D1 in Part I.
- **No discounts to acquire.** Discount-askers churn at roughly 2× the rate of full-price customers (figure from the skill's source), and coupons anchor the product as cheap; the category appears coupon-heavy. Allowed: sibling/upgrade offers, real seasonal windows, and a **capped** "founding family" early-access price (a real cap, never a fake countdown).
- **Seasonality.** Parent usage follows the school year, so offer a **pause** (the `churn-prevention` skill's answer to seasonal need) or a school-year plan.
- **Trial length is a test.** Insights need roughly a week of sessions to mean anything, so a 3–4 day trial would expire before the first useful report. Hypothesis: 14 days or more, with a reminder before any conversion.
- **Hard paywall vs freemium.** The `paywalls` reference cites RevenueCat 2026: hard-paywall mobile apps converted roughly 5× more users to paid in 35 days than freemium apps, with similar one-year retention. That is mobile-subscription data and may not transfer to desktop family software. **Keep freemium** because the free tier carries trust and word of mouth, and revisit with real numbers.
- **Store fees.** The skill only covers Apple and Google. If you sell through the Microsoft Store, check its current terms; a direct MSI sale needs a payment provider instead. Not covered here.
- **How to test price without breaking the offline promise.** No in-app telemetry. Run price experiments on the **website purchase page** (split by visitor) and with pilot cohorts. A Van Westendorp survey in parent interviews gives a range, not proof of demand.

### G.4 Conversion path and paywall (from `paywalls`)

- **Show upgrade prompts only in the parent area, behind the existing ParentalGate.** Children never see purchase prompts. Check app-store and COPPA/GDPR-K rules with counsel.
- **Trigger after the aha moment.** The first weekly summary becomes available after enough sessions. Show a **preview built from the child's own data** (one real insight, the rest locked): "Maya's first skill map is ready" with a specific button ("Unlock Maya's full skill map and worksheet"). Added as feature P16.
- **Worksheet preview:** page 1 free, the rest unlocked.
- **Rules:** a visible "Not now" or "Continue with Free", at most one prompt per week with a cool-down measured in days, no fake countdowns, no guilt copy, never during a child's play session.
- **Measurement without child data:** pilot interviews and website-side funnel metrics. Any in-app counters must be opt-in, anonymous and aggregate (see R10 and Part H).

### G.5 Retention for parents (from `churn-prevention`, adapted)

- The **weekly summary plus the next worksheet** is the recurring value and the usage signal; it justifies renewal without any pressure on the child.
- **If subscription:** easy self-serve cancellation (some regulations require it), a one-question exit survey, save offers matched to the reason (pause for seasonal, downgrade for cost), no guilt copy, no default discount.
- **If one-time licence:** the cancel flow and dunning do not apply; retention then means paid updates and content packs.
- Reconciles with Part A.3: parent retention uses value delivered; child-facing retention stays non-manipulative.

### G.6 Launch plan (from `launch`)

**L1–L4 are launch tasks that run in parallel with Phase 1.**

| ID | Task | Notes |
|---|---|---|
| L1 | Landing page and email waitlist | Owned channel; claims follow Part A.4 |
| L2 | Licence mechanism and parent-gated upgrade screen | Offline signed licence file or payment-provider integration (decision D4) |
| L3 | Website price experiment | Split by visitor; no in-app telemetry |
| L4 | Early-access cohort | Capped "founding family" pricing, real limit only |

- **Readiness gate (Simple, Lovable, Complete).** Family Plus v1 is **Phase 1 only**. Its one job: *know why my child is stuck and what to practise this week.* Phases 3 and 4 are later releases ("launch again and again"); do not gate launch on them. That is the "just one more feature" trap.
- **Phase mapping:** internal (existing users and friendly families, Part F step 3) → alpha (waitlist and invites) → beta (invite in batches of 5–10%) → early access (capped paid cohort) → full launch.
- **Channels (ORB).** *Owned:* website, email list, an "evidence notes" blog explaining the methods in plain language. *Rented:* Microsoft Store listing, short demo videos, one or two parenting or homeschool communities. *Borrowed:* homeschool and teacher creators, and tutors, who get the teacher PDF (P15).
- **Product Hunt: low priority.** The skill describes its audience as tech-savvy early adopters; the buyer here is a parent.
- **Post-launch:** onboarding emails for parents, changelog and release notes as a signal of active development. For comparison content, start with an honest "how to choose a maths app" guide that says who each tool suits; publish competitor-specific pages only after dated, verified facts.

### G.7 Claims and copy check (from `offers` and `competitors`)

- **Never use:** "game-changing", "next-level", "10x", "secret method"; "limited time" without a real deadline; "worth $X" without a comparable; "100% guaranteed" without stated conditions.
- **"Scientifically proven" is not defensible.** Use "built on practice methods with strong research support" and cite them (Part A.4). No IQ, brain-training or clinical claims.
- **Guarantee:** consider a plain refund window rather than an outcome promise; do not promise grade improvements.
- **Competitor mentions:** dated and sourced facts only; "not listed (as of date)" instead of "doesn't have".

### G.8 What this review changed in the document

- A.2: added the revised differentiation (the Khan finding).
- C: added the launch track (L1–L4) and P16 to Phase 1.
- D.4: added P16 (insight preview).
- G.3: **revised the Free/Plus split** (child-facing learning loop stays free).
- H: added telemetry, seasonality and discount-culture risks.
- I: replaced the open questions with decisions D1–D5.

### G.9 Sources (accessed 2026-11-08)

- Khan Academy blog: [2025 Khan Academy Updates Every Parent Should Know](https://blog.khanacademy.org/?p=19766)
- Prodigy pricing (third-party): [Prodigy Cost Breakdown](https://nibble-app.com/blog/prodigy-cost), [How Much Does Prodigy Membership Cost?](https://www.thepricer.org/how-much-does-prodigy-membership-cost/)
- IXL pricing (third-party): [How Much Does IXL Cost?](https://www.thepricer.org/how-much-does-ixl-cost/), [IXL Cost](https://nibble-app.com/blog/ixl-cost)
- DragonBox / Kahoot! Algebra (third-party trackers): [Kahoot! Algebra by DragonBox](https://apppricinglab.com/app/google_play/com.kahoot.algebra5), [DragonBox Algebra 5+](https://apps.apple.com/us/app/dragonbox/id522069155)
- Marketing frameworks: `marketingskills` v2.14.2 (`pricing`, `paywalls`, `launch`, `competitors`, `offers`, `churn-prevention`)

---

## Part H — Risks

| Risk | Mitigation |
|---|---|
| Phase 0 refactor touches every quiz flow | Keep old and new engines behind an interface; migrate category by category |
| Misconception detectors give wrong diagnoses | Always confirm with the child ("Did you…?"); log "Not quite" answers to tune detectors |
| Predictions look authoritative on thin data | Uncertainty ranges and minimum-data thresholds |
| Focus indicators read as medical advice | Practice-indicator wording plus a not-clinical disclaimer |
| Online features trigger child-privacy law | Opt-in, aggregate-only, no IDs, legal review before R10 |
| Windows-only limits the market | Evaluate cross-platform packaging before launch (Part G.6, decision D2) |
| Scope: this document is large | Ship Phase 1 and validate before committing to Phases 3–4 |
| Price tests and funnel metrics conflict with the offline promise | No in-app telemetry by default; test price on the website and with pilot cohorts; any in-app counters opt-in, anonymous and aggregate |
| Parent usage is seasonal (school year, summer) | Pause option or school-year plan (Part G.3) |
| Category coupon culture pulls the price down | No acquisition discounts; sibling, upgrade and capped founding-family offers only |
| Hard-paywall conversion data comes from mobile subscription apps | Treat as unproven for desktop; stay freemium and revisit with real numbers |
| Competitor facts go stale or are wrong (several already changed) | Dated, sourced facts only; re-verify before any public copy |

---

## Part I — Decisions for the owner

| ID | Decision | Recommendation (not final) |
|---|---|---|
| D1 | Free vs Family Plus split | Keep the child-facing learning loop free (practice, hints, basic Mistake Lab, current adaptive rules); charge for parent insights, worksheets, skill-level adaptive engine, mechanic packs and multi-child (G.3) |
| D2 | Platform reach: cross-platform packaging before launch? | Decide before committing to launch spend; the app ships as a Windows MSI today |
| D3 | Which curricula to tag first (Common Core, UK NC, Singapore, IB, CBSE)? | Pick the one or two where the first paying parents are |
| D4 | Licence model and sales channel | Test annual family vs one-time licence; direct MSI sale vs Microsoft Store (check store terms) |
| D5 | Online community feature (R10): wanted at all? | Defer until the offline positioning has proven itself; needs legal review |