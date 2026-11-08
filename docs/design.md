# Atelier Arithmetic — Design System and UX Specification

| Field | Value |
|---|---|
| Document version | 1.0 |
| Date | 2026-11-08 |
| Basis | Colour, font, spacing and behaviour values are read directly from the Swing source (`AppTheme`, panels, `TourOverlay`, `MascotPainter`, `SoundService`). Contrast ratios are hand-computed from those RGB values with the WCAG 2.x formula and are approximate. |
| Related | [PRD.md](PRD.md) · [architecture.md](architecture.md) · [rules.md](rules.md) · [task.md](task.md) · [role_skills.md](role_skills.md) |

> Visual design is implemented in code, not in a design tool. This document is the written specification of what the code does and what new UI must follow ([rules.md](rules.md) R-UI-*). Where the code is inconsistent, §16 lists the debt.

---

## 1. Design principles

| # | Principle | In practice |
|---|---|---|
| D1 | **Warm editorial, not "kiddie"** | Cream parchment background, bronze-gold accent, serif display headings; playful elements are limited to Archie and emoji. |
| D2 | **Encouraging always** | Every grade, message and empty state is positive; red is used for information (wrong answer), never for shaming. |
| D3 | **One obvious next action** | Each screen has a single dark primary button; secondary actions are quiet outlined/ghost buttons. |
| D4 | **Cards on calm ground** | Content sits in white (or graphite) cards with a 1 px hairline border on a flat background. |
| D5 | **Adults tucked away** | Admin tools are small, secondary and gated. |
| D6 | **Readable at any size** | Three font scales; large type for the question (32 pt serif). |
| D7 | **Light and dark are equals** | Both palettes are complete; every component re-themes live. |

---

## 2. Brand and voice

| Item | Specification |
|---|---|
| Name | **Atelier Arithmetic**; Home title "Royal Mathematics Quiz"; window title "Atelier Arithmetic · Royal Math Quiz" |
| Tagline (README) | "Where every question is a step forward." |
| Mascot | **Archie the Owl** — logo image (`logo.png`, 1024×1024) in headers/help/analytics; Java2D vector Archie in the tour; 🦉 emoji in text. Equipped shop item emoji is appended to 🦉 on Home. |
| Voice | Friendly guide, second person, short sentences, light enthusiasm. Archie "speaks" in quotes: *Archie says: "Ready for a math adventure?…"* |
| Emoji | One or two per message; always paired with words; wrapped in `Segoe UI Emoji` inside HTML labels. |
| Eyebrow labels | Small gold UPPERCASE text above titles (e.g. "ANALYTICS", "SMART PRACTICE MODE", "TEACHER & PARENT ZONE", "ARCHIE'S SHOP"). |

### 2.1 Copy do / don't
| Do | Don't |
|---|---|
| "Great effort! Practice makes champions." | "You failed." / "Bad score." |
| "❌ Wrong. The correct answer was: 42" (factual) | "Wrong!!!" in capitals, red screens, buzzers longer than 300 ms |
| "Type your answer first!" | "Invalid input." |
| "Only whole numbers, please!" | "NumberFormatException" |
| "Oops! Archie the Owl encountered a tiny calculation hiccup." | Showing stack traces to children |
| "Play a few regular games first so Archie can analyze your strengths!" (empty state) | Blank panels |

### 2.2 Canonical child-facing strings
| Context | Exact text |
|---|---|
| Correct | `✅ Correct! Great job!` |
| Wrong | `❌ Wrong. The correct answer was: <n>` |
| Empty answer | `Type your answer first!` |
| Non-integer | `Only whole numbers, please!` |
| Submit / next | `SUBMIT ANSWER` / `NEXT QUESTION  →` |
| Exit quiz (Esc) | title `Exit Quiz?` — "Do you want to exit the current quiz and return to categories? 🦉" |
| Level up | title `Level Up?` — "Archie notices you're doing amazing! 🦉 / Would you like to try the next difficulty level: <X>?" |
| Level down | title `Adjust Level?` — "No worries! Archie suggests taking it a bit easier. 🦉 / Would you like to change the difficulty to: <X>?" |
| Difficulty changed | `✨ Difficulty upgraded to <X>! ✨` / `✨ Difficulty adjusted to <X>! ✨` |
| Parental gate | title `Parent / Teacher Lock 🦉` — "This screen is for parents or teachers only. / Please solve this math puzzle to proceed: / ★ What is <word> times <word>? ★" |
| Gate failure | title `Lock Active` — "Incorrect answer. Verification failed! Access restricted." |
| Crash | title `Archie's Code Hiccup 🦉` — "Oops! Archie the Owl encountered a tiny calculation hiccup. We have saved a report. You can restart or continue playing! / Error Details: <message>" |
| Reset | title `Reset History?` — "Are you sure you want to clear all your math quiz history? This cannot be undone! 🦉" *(does not mention stars/shop — BUG-13)* |
| Shop – insufficient | title `Not Enough Stars` — "You need <p> ★ stars to unlock this! You currently have <b> ★ stars. Complete more quizzes to earn stars!" |
| Shop – confirm | title `Confirm Purchase` — `Unlock "<name>" for <p> ★ stars?` |
| Shop – success | title `Item Unlocked!` — "🎉 <emoji> "<name>" unlocked and equipped! Archie is wearing it right now on the Home screen!" |

---

## 3. Colour

All colours are `java.awt.Color` RGB. Code accessors in `AppTheme`: `getBgPrimary`, `getBgCard`, `getTextDark`, `getTextMuted`, `getBorderClr`, `getAccentGold`.

### 3.1 Light palette (default)
| Token | RGB | Hex | Use |
|---|---|---|---|
| `LIGHT_BG_PRIMARY` | 250, 249, 246 | `#FAF9F6` | Screen background (cream parchment) |
| `LIGHT_BG_CARD` | 255, 255, 255 | `#FFFFFF` | Cards, inputs, secondary buttons |
| `LIGHT_TEXT_DARK` | 28, 25, 23 | `#1C1917` | Headings, body, primary button fill |
| `LIGHT_TEXT_MUTED` | 120, 113, 108 | `#78716C` | Secondary text, labels |
| `LIGHT_BORDER_CLR` | 230, 227, 220 | `#E6E3DC` | 1 px card/input borders, grid lines, empty heat cells |
| `LIGHT_ACCENT_GOLD` | 184, 150, 110 | `#B8966E` | Eyebrows, balances, focus/hover, chart line, progress fill |

### 3.2 Dark palette
| Token | RGB | Hex | Use |
|---|---|---|---|
| `DARK_BG_PRIMARY` | 24, 24, 28 | `#18181C` | Screen background (graphite) |
| `DARK_BG_CARD` | 36, 36, 40 | `#242428` | Cards |
| `DARK_TEXT_DARK` | 242, 242, 247 | `#F2F2F7` | Headings/body (name kept, value is light) |
| `DARK_TEXT_MUTED` | 156, 163, 175 | `#9CA3AF` | Secondary text |
| `DARK_BORDER_CLR` | 63, 63, 70 | `#3F3F46` | Borders |
| `DARK_ACCENT_GOLD` | 220, 185, 115 | `#DCB973` | Accent |

> In dark mode the "dark text" token is the *light* colour; primary buttons use `getTextDark()` as fill and `getBgCard()` as text, so they invert automatically.

### 3.3 Semantic and feature colours
| Purpose | RGB | Hex | Where |
|---|---|---|---|
| Success | 34, 197, 94 | `#22C55E` | Correct feedback, strengths, "difficulty upgraded" |
| Error | 239, 68, 68 | `#EF4444` | Wrong feedback, validation errors, Reset button text |
| Positive action text | 34, 139, 34 | `#228B22` | "✅ Equipped" button, "Create Profile" |
| Hover/focus card (light) | 253, 251, 245 | `#FDFBF5` | Category card hover |
| Hover/focus card (dark) | 48, 48, 54 | `#303036` | Category card hover |
| Hint box bg (light/dark) | 254, 252, 246 / 40, 38, 35 | `#FEFCF6` / `#282623` | Hint panel (border = accent gold) |
| Help section bg / title | 253, 251, 247 / 120, 90, 50 | `#FDFBF7` / `#785A32` | Help cards (light) |
| Review row – correct (light) | 220,252,231 / 187,247,208 | `#DCFCE7` / `#BBF7D0` | alternating shades |
| Review row – wrong (light) | 254,226,226 / 252,202,202 | `#FEE2E2` / `#FCCACA` | |
| Review row – correct (dark) | 20,50,32 / 25,60,38 | `#143220` / `#193C26` | |
| Review row – wrong (dark) | 64,24,24 / 74,30,30 | `#401818` / `#4A1E1E` | |
| Review selection | 230, 227, 220 | `#E6E3DC` | |
| Tour mask | 0,0,0 @ α175 | — | Dim layer |
| Tour glow / bubble border | 184,150,110 @ α220 / opaque | — | Spotlight ring, bubble border |
| Tour bubble bg | 255, 253, 248 | `#FFFDF8` | |
| Tour SKIP button | 120, 113, 108 | `#78716C` | NEXT = `#1C1917` |

### 3.4 Radar chart category colours (vertex dots)
| # | Category | RGB | Hex |
|--:|---|---|---|
| 1 | Addition | 99,179,237 | `#63B3ED` |
| 2 | Difference | 154,205,50 | `#9ACD32` |
| 3 | Multiplication | 255,153,51 | `#FF9933` |
| 4 | Division | 218,112,214 | `#DA70D6` |
| 5 | Mixed | 64,224,208 | `#40E0D0` |
| 6 | Special | 255,99,132 | `#FF6384` |
| 7 | Fractions | 147,112,219 | `#9370DB` |
| 8 | Patterns | 255,215,0 | `#FFD700` |
| 9 | Algebra | 72,209,204 | `#48D1CC` |
| 10 | Measurement | 255,127,80 | `#FF7F50` |

### 3.5 Contrast (approximate)
| Foreground on background | Ratio | WCAG verdict |
|---|--:|---|
| Text dark `#1C1917` on parchment | ≈ 16.6 : 1 | AAA |
| Text muted `#78716C` on white / parchment | ≈ 4.8 / 4.6 : 1 | AA (small text) — just passes on parchment |
| **Accent gold `#B8966E` on white / parchment** | **≈ 2.7 / 2.6 : 1** | **Fails** 4.5 : 1 (text) and 3 : 1 (large text / UI) |
| **Success green `#22C55E` on white** | **≈ 2.3 : 1** | **Fails** (used for 13 pt bold feedback text) |
| **Error red `#EF4444` on white** | **≈ 3.8 : 1** | Fails for small text, passes large |
| Dark mode text `#F2F2F7` on card `#242428` | ≈ 13.9 : 1 | AAA |
| Dark mode muted `#9CA3AF` on card | ≈ 6.1 : 1 | AA |
| Dark mode gold `#DCB973` on card | ≈ 8.3 : 1 | AAA |
| White on primary button `#1C1917` | ≈ 17.5 : 1 | AAA |

Action: darker gold/green/red variants for light mode text (FEAT-05, rule R-UI-14).

---

## 4. Typography

| Role | Family | Size (base, pt) | Weight | Colour |
|---|---|--:|---|---|
| Question expression | Serif | 32 | Plain | Text dark |
| Screen title (Help) | Serif | 30 | Plain | Text dark |
| Screen titles (Category 27, Shop 26, Builder 26, Smart Practice 26, Badges 26, Analytics 24, Results 24, Home title 24) | Serif | 24–27 | Plain | Text dark |
| Review title | Serif | 28 | Plain | Text dark |
| Grade line (Results) | Serif | 18 | Bold | Accent gold |
| Smart Practice target | Serif | 28 | Bold | Text dark |
| Stat value (Analytics) | Serif | 22 | Bold | Text dark |
| Shop item name | Serif | 14 | Bold | Text dark |
| Eyebrow label | Serif (Home) / SansSerif | 10 | Bold (Home) / Plain | Accent gold |
| Card title | SansSerif | 11–13 | Bold | Text dark / gold / muted |
| Body / metrics | SansSerif | 11–14 | Plain | Text dark / muted |
| Input (answer) | SansSerif | 18 | Bold | Text dark |
| Buttons | SansSerif | 11–13 | Bold (primary) / Plain (secondary) | See §6 |
| Stat label | SansSerif | 9 | Bold, UPPERCASE | Muted |
| Emoji | `Segoe UI Emoji` | 34 (shop), 26–48 elsewhere | — | — (colour glyphs) |

* **Scale:** `AppTheme.scaleComponentFont` sets size = `round(baseSize × scale)`, scale ∈ {1.0, 1.25, 1.5}; **default 1.25**. HTML labels with inline `font-size` do not scale.
* **Fallbacks:** if `Segoe UI Emoji` is missing (non-Windows), shop emoji fall back to `Serif`.
* **Use ★ (U+2605)** for star counts in text — it renders in all fonts; ⭐ (U+2B50) does not.

---

## 5. Layout, spacing and shape

### 5.1 Window
Initial 820 × 620, minimum 720 × 550, centred; background = `getBgPrimary()`; icon = logo.

### 5.2 Spacing scale (observed)
Base steps used: **4 · 6 · 8 · 10 · 12 · 14 · 16 · 18 · 20 · 25 · 30 · 40**. Screen edge padding 30–40 px horizontally. Card padding 12–22 px (dense cards) or 40–60 px (focus cards).

### 5.3 Grids
| Screen | Grid |
|---|---|
| Home | 2 columns, weights **58 % / 42 %**, 20 px gutter; right column = 4 stacked cards (weights 0.28, 0.28, 0.22, 0.22; 12 px gaps) |
| Categories | `GridLayout(0, 3, 16, 16)` in a vertical scroll pane, padding 10/40/20/40 |
| Shop | `GridLayout(0, 3, 14, 14)`, padding 12/30/20/30 |
| Badges | `GridLayout(0, 2, 16, 16)`, each card 320 × 85 |
| Results | `GridLayout(2, 2, 16, 16)` |
| Analytics | rows: stat cards 1×4 (gap 14, max h 96) → charts 1×2 (gap 20, max h 330) → insights 1×2 (max h 200) → history/goals 1×2 (max h 200); page scrolls vertically |
| Quiz Builder | 2 columns: form card 45 % + table pane 55 % |

### 5.4 Shape and elevation
* Cards: flat, **1 px hairline border**, no shadow. Results cards are the exception: custom-painted **16 px rounded** rect with border.
* Tour bubble: 20 px radius, 2 px gold border, 4 px offset black α60 shadow.
* Inputs: 1 px border `(200,195,185)` in light mode (themed `BorderClr` after theme refresh), inner padding 5–10 px.

---

## 6. Components

| Component | Spec |
|---|---|
| **Primary button** | Fill `getTextDark()`, text `getBgCard()` (inverts in dark); SansSerif **bold 12–13**; padding ≈ 10–12 × 26–36; no focus ring (`setFocusPainted(false)`); hand cursor. One per screen (e.g. "CHOOSE CATEGORY  →", "SUBMIT ANSWER", "🔄  PLAY AGAIN", "START SMART PRACTICE  →"). |
| **Secondary button** | Two variants: (a) *ghost* — background = screen bg, text muted, no border (Game/Category/Help footers: "❓ Guide", "← Back"); (b) *outlined* — card bg, text muted, 1 px border, padding 10 × 18 (Results footer, Back buttons in Shop/Badges/Analytics). |
| **Toggle / chip** | Top-bar buttons: SansSerif 11 plain, card bg, muted text, 1 px border, padding 6 × 12 (profile, scale, sound, theme, Daily Challenge). |
| **Card** | See §5.4; title row (bold 11–13) above content. |
| **Stat card** | Left icon (26 pt emoji, gold), right column: LABEL (9 bold caps muted) / VALUE (22 serif bold) / subtitle (10 muted). |
| **Category card** | Icon 26 pt → NAME (12 bold gold caps) → description (11 muted, centred HTML) → START button (dark, 11 bold, padding 6 × 22). Hover/focus: 2 px gold border + hover bg. Focusable for keyboard use. |
| **Shop item card** | Emoji 34 pt (padding 12/0/4/0 to avoid clipping) → CATEGORY (9 caps muted) → name (serif 14 bold) → description (10 muted) → price `★ n` (12 bold; gold if purchasable, muted if owned) + action button (11 bold, 1 px border, padding 4 × 10). Equipped card = 2 px gold border. |
| **Badge card** | Emoji 28 pt (🔒 when locked) + name (13 bold) + description (11 muted, width 180). Unlocked = 1.5 px rounded gold outline. |
| **Progress bar** | 8 px high, ≈ 180 px preferred, fill gold, track `(230,227,220)`, no border. |
| **Text field** | See §5.4; answer field is 8 columns wide, 18 pt bold. |
| **Hint box** | Under the answer: bg `#FEFCF6`/`#282623`, 1 px gold border, padding 8 × 12, 💡 icon + HTML text (12 pt). Hidden until requested; hidden again on the next question. |
| **Table (Review)** | Row height 34, no grid lines, 2 px inter-row spacing, header 12 bold; cells centred except Expression (left); columns preferred widths 30/230/100/120/90/60. |
| **Popup menus** | `JPopupMenu` for profile, sound, load-quiz; items SansSerif 12, current item prefixed `✓ ` and bold. |
| **Dialogs** | `JOptionPane`/`JDialog`, title with 🦉 where friendly; create-profile dialog is modal, non-resizable, centred. |
| **Speech bubble (tour)** | 320 × 160, see §10. |

### 6.1 States
| State | Treatment |
|---|---|
| Hover (buttons) | Cursor hand + quiet hover tick sound (`playHover`) |
| Press | Click pop (`playClick`) |
| Hover/focus (category card) | 2 px gold border, hover bg |
| Disabled | Native disabled look; "✅ Completed" replaces the Daily Challenge label |
| Empty | Friendly text (see §9) |
| Locked | 🔒 icon, muted text |
| Selected row (table) | `#E6E3DC` |

---

## 7. Screen specifications

Wireframes are schematic (proportions approximate).

### 7.1 Home (`welcome`)
```
┌ top bar (right-aligned chips) ──────────────────────────────────────────────────┐
│                               ★ 42  [👤 Guest] [🔍 Scale: 125%] [🔊 Sound] [🌙 Dark] │
├───────────────────────────────────────────────┬──────────────────────────────────┤
│ [logo 60px]  ATELIER ARITHMETIC               │ ┌ 📅 Daily Quest      🔥 3 Day Streak ┐│
│              Royal Mathematics Quiz           │ │ [📅 Daily Challenge]  M T W T F S S ││
│              🦉 Archie says: "Ready for…"      │ │                       ○ ● ● ○ ● ● ● ││
│ ┌ config card ─────────────────────────────┐  │ └─────────────────────────────────────┘│
│ │ NUMBER OF QUESTIONS:  [ 10      ]        │  │ ┌ 📊 Performance & Badges ─────────────┐│
│ │ DIFFICULTY LEVEL:     [ Easy  ▾ ]        │  │ │ 💾 12 Sessions completed             ││
│ │ CUSTOM QUIZZES:  [🛠️ Quiz Builder][📂 Load] │  │ │ 🏆 4 / 10 Badges unlocked            ││
│ │                                          │  │ │ [📊 Analytics]   [🏆 Badges]         ││
│ │        [ CHOOSE CATEGORY  → ]            │  │ └─────────────────────────────────────┘│
│ └──────────────────────────────────────────┘  │ ┌ 🛍️ Archie's Shop            ★ 42 ───┐│
│                                               │ │ Spend stars to unlock… / 🎩 Equipped ││
│                                               │ │ [🛍️ Open Mascot Shop]               ││
│                                               │ └─────────────────────────────────────┘│
│                                               │ ┌ 🎯 Smart Practice & Guide ──────────┐│
│                                               │ │ [🎯 Practice] [🦉 Tour] [❓ Guide]  ││
└───────────────────────────────────────────────┴──────────────────────────────────────┘
```
Behaviour: see PRD FR-SET-*. Greeting text changes when an accessory is equipped ("Looking great today! Ready for math?").

### 7.2 Categories (`categories`)
```
              Choose Your Arithmetic Discipline
  ┌────────────┐ ┌────────────┐ ┌────────────┐
  │     ➕     │ │     ➖     │ │     ✖️     │
  │  ADDITION  │ │ DIFFERENCE │ │MULTIPLICATION│   (10 cards, 3 per row, scrolls)
  │ Add numbers…│ │ Subtract…  │ │ Multiply…   │
  │  [ START ] │ │  [ START ] │ │  [ START ]  │
  └────────────┘ └────────────┘ └────────────┘
                      [❓ Guide] [← Back]
```
Icons in order: ➕ ➖ ✖️ ➗ 🔀 ⭐ 📊 📈 ⚖️ 📏. Descriptions: "Add numbers together and find the sum!", "Subtract and find the difference!", "Multiply numbers to get the product!", "Divide numbers perfectly — no remainders!", "A mix of all operations — ultimate challenge!", "Tricky compound expressions for math champs!", "Solve fractional proportions & percents!", "Find the missing numbers in sequences!", "Solve algebraic equations for X!", "Convert metric units & estimate areas!".

### 7.3 Game (`game`)
```
 Question 3 of 10  ━━━━━━━━━━░░░░░░░░░░                         ⏱ 7s
 ┌────────────────────────────────────────────────────────────┐
 │                       47 + 86 = ?                          │  (32 pt serif)
 │          YOUR ANSWER:  [ 133    ]  [💡 Hint]                │
 │                 ✅ Correct! Great job!                      │
 │ ┌ 💡  Partitioning Addition: 1. Add tens … ───────────────┐ │
 │ └──────────────────────────────────────────────────────────┘ │
 └────────────────────────────────────────────────────────────┘
                [❓ Guide]   [ SUBMIT ANSWER ]
```
Keys: Enter = submit/next · Esc = exit prompt · F1 / Ctrl+H = hint.

### 7.4 Results (`results`)
```
 BESPOKE LEARNING METRICS
 Performance Dashboard: Guest
 ┌ 📊 Key Metrics ───────────┐ ┌ 🏆 Session Mastery ─────────────┐
 │ 🎯 Score: 9 / 10          │ │ 🚀 Fastest solved: 12 * 4 = 48 … │
 │ 📈 Success Rate: 90.0%    │ │ 🧠 Challenge area: 345 + … (Incorrect)│
 │ 🌟 Grade: A+              │ │ 💡 Fast answers unlock chime…    │
 │ ⏱ Total Time: 1m 12s      │ └──────────────────────────────────┘
 │ ⚡ Avg Speed: 7.2s / question│ ┌ 🦉 Archie's Insights ────────────┐
 │ ★ +21 Stars Earned! (tooltip)│ │ 🦉💪  Excellent work! …          │
 └───────────────────────────┘ └──────────────────────────────────┘
 ┌ 🎯 Suggested Path ────────────────────────────────────────────┐
 │ You are ready for the next challenge! [STEP UP TO MEDIUM DIFFICULTY]│
 └────────────────────────────────────────────────────────────────┘
        [❓ Guide] [📋 Review Answers] [📄 Export Report] [🔄  PLAY AGAIN]
```
Star tooltip lines: `★ +<total> Stars Earned!`, `Base: +5`, then only non-zero: `Correct answers`, `Perfect score bonus`, `Grade bonus`, `Daily challenge`, `Streak bonus`.

### 7.5 Review (`review`)
Title "ANSWER REVIEW — Every question, your answer, and the correct answer"; table per §6; footer [← Back to Results] [🏠 Play Again].

### 7.6 Analytics (`analytics`)
```
 ANALYTICS                                      [🗑 Reset History] [← Back]
 Performance Dashboard
 ┌📈 OVERALL ACCURACY┐┌✏ QUESTIONS SOLVED┐┌⏱ AVERAGE SPEED┐┌🔥 PRACTICE STREAK┐
 │ 78.5%             ││ 240              ││ 6.4s           ││ 3d               │
 │ Best score: 100.0%││ Practice: 25.3 min││ Fastest: 1.2s  ││ Longest: 5d      │
 └───────────────────┘└──────────────────┘└────────────────┘└──────────────────┘
 ┌ Accuracy Trend (last 20 sessions) ─┐ ┌ Category Radar (average accuracy) ─┐
 │  smooth gold curve, area gradient  │ │  10-spoke radar                    │
 └────────────────────────────────────┘ └────────────────────────────────────┘
 ┌ 📅 Practice Activity & Recommendations ┐ ┌ ⚖️ Topic Mastery ─────────────────┐
 │ Heatmap (Last 28 Days): 4×7 dots       │ │ ✅ Addition: 92.0% Accuracy        │
 │ [logo] Archie says: "…try Practice…"   │ │ ⚠️ Fractions: 55.0% accuracy       │
 └────────────────────────────────────────┘ └────────────────────────────────────┘
 ┌ Recent Quiz History ───────────────┐ ┌ 🚀 Learning Goals & Quick Actions ─────┐
 │ 📄 Algebra (Medium) · 80% Score ·… │ │ ☑ Complete Daily Quest today           │
 └────────────────────────────────────┘ │ ☑ Keep practice streak alive           │
                                        │ [🎯 Smart Practice] [📅 Daily Challenge]│
```

### 7.7 Badges (`achievements`)
Eyebrow "ACHIEVEMENTS", title "Milestones & Badges", 2-column grid of badge cards; locked cards show 🔒.

### 7.8 Mascot Shop (`mascot_shop`)
Eyebrow "ARCHIE'S SHOP", title "Mascot Accessories 🦉", hint "Spend your ★ Stars to unlock cool looks for Archie!", right: `★ n Stars` + [← Back]; 3-column grid of 12 item cards (§6).

### 7.9 Smart Practice (`smart_practice`)
Centered card: big emoji (🎯 with history / 🌱 without), prompt line, target "Category (Difficulty)" in 28 pt serif bold, italic note "This practice session generates 10 tailored questions.", primary button.

### 7.10 Quiz Builder (`quiz_builder`)
Left card: QUIZ NAME / QUESTION EXPRESSION (e.g. 5 * 12 + 6) / CORRECT INTEGER ANSWER / [➕ ADD QUESTION]. Right: table `# | Expression | Answer` + [➖ Remove Selected] [💾 Save Custom Quiz]. Header: eyebrow "TEACHER & PARENT ZONE", title "Custom Quiz Builder", [← Back].

### 7.11 Help (`help`)
Logo, "🦉 Help & Guide", subtitle, 13 stacked section cards (title 14 bold brown `#785A32`, HTML body 12 px with its width pinned to 440 CSS px — Swing scales CSS px by ≈ 1.3, giving ≈ 572 px — and copy hard-wrapped to ≤ ~66 characters per line, continuation lines indented with `&nbsp;&nbsp;`), footer [← Back] [🦉 Replay Tour].

### 7.12 Create-profile dialog
Title "Create Child Profile"; bordered privacy notice ("🦉 Privacy First: To protect child safety, please do not use your real first or last name! Choose a fun nickname or generate a mascot name below."); name field + [🎭 Spark Mascot Name]; [Cancel] [Create Profile].

---

## 8. Navigation and keyboard

### 8.1 Map
See state diagram in [architecture §6.3](architecture.md#63-navigation-model). Every non-Home screen offers a way back to Home or its origin.

### 8.2 Keyboard
| Screen | Keys | Action |
|---|---|---|
| Any with a focused text field | Enter | Submit/next (Game), confirm default button |
| Game | Esc | Exit confirmation → Categories |
| Game | F1, Ctrl+H | Show hint |
| Categories | ← → | Previous/next card (wraps) |
| Categories | ↑ ↓ | Card −3 / +3 (modulo 10) |
| Categories | Enter, Space | Start the focused category |
| Categories | Esc | Back to Home |
| Home | (on entry) | Question-count field focused and selected |

Tour and Home buttons are mouse/Tab driven.

---

## 9. Feedback, states and empty states

| Situation | Feedback |
|---|---|
| Correct answer | Green text + C5-E5-G5 arpeggio |
| Wrong answer | Red text with the correct answer + descending buzz |
| Invalid input | Red text under the field; question not consumed |
| Quiz start | Rising 4-note arpeggio |
| Quiz end | Fanfare |
| Navigation | Quick upward slide chime |
| Purchase | Dialog; Home/shop balance updates immediately |
| Daily done | Button becomes "✅ Completed", disabled |
| No history (Analytics) | "Complete a few sessions to see your trend!" (chart), "No category data yet" (radar), "No sessions played yet!", "Play sessions to view topic strengths!" |
| No history (Smart Practice) | 🌱 "Play a few regular games first so Archie can analyze your strengths!" / "No history data yet" |
| No custom quizzes | "No custom quizzes found! Create one using the Quiz Builder first. 🦉" |
| No strengths | " 🔍 Keep practicing to build strengths!" |
| All strong | " 🎉 Outstanding! No weak areas detected!" |

Results advice text (by accuracy): ≥95 "True Math Mastery! You calculated everything flawlessly…" 🦉🌟 · ≥80 "Excellent work! You have strong core math skills…" 🦉💪 · ≥50 "Solid performance! You solved many correctly…" 🦉👍 · else "Great effort! Practice makes champions…" 🦉🌱.

---

## 10. Tour overlay and mascot

### 10.1 Overlay (glass pane)
| Property | Value |
|---|---|
| Mask | black α175, area minus spotlight |
| Spotlight | target bounds + **14 px** padding, 20 px corner radius, 3 px glow stroke `(184,150,110,220)` |
| Bubble | 320 × 160, 20 px radius, bg `#FFFDF8`, border 2 px gold, shadow offset 4 px α60 |
| Text | SansSerif 14, word-wrapped, 14 px inset |
| Counter | "Step *i* of *N*" SansSerif 11, `(180,150,110)`, top of bubble |
| Buttons | NEXT → / FINISH ✓ (dark `#1C1917`, 120×34) and SKIP TOUR (muted, 110×34), SansSerif bold 11, inside the bubble bottom edge |
| Placement | `BOTTOM_CENTER / TOP_CENTER / LEFT_CENTER / RIGHT_CENTER / SCREEN_CENTER`; clamped to keep ≥ 10 px margin and room (80 + 20 px) for the mascot at left |
| Mascot | 80 px, placed left of the bubble (right if no room), vertically centred on the bubble |
| Input | All mouse events consumed except NEXT/SKIP |

### 10.2 Archie (Java2D, `MascotPainter`)
Body = circle with gradient `(210,150,80)→(160,100,45)`, outline `(120,70,20)` 2 px; two ear tufts (±15° triangles); belly oval `(245,220,170)`; white eyes (r = size/8) with rim `(80,50,10)`, pupils `(40,20,5)`, highlight; triangular beak `(240,180,60)`; wings `(160,100,45)` α200; feet = 3-toed lines in beak colour.

| Expression | Differences |
|---|---|
| WAVING | Smile + raised right wing with a hand dot |
| HAPPY | Smile |
| EXCITED | Smile + pupils shifted up-left by `eyeR/4` |
| THINKING | Raised left brow line + slight smirk line instead of a smile |
| POINTING | Smile + extended wing with a gold finger dot |

### 10.3 Tour content map (24 steps)
| # | Screen | Target | Bubble | Mascot |
|--:|---|---|---|---|
| 1 | welcome | — (intro) | centre | WAVING |
| 2 | welcome | profile button | bottom | POINTING |
| 3 | welcome | font-scale button | bottom | THINKING |
| 4 | welcome | theme button | bottom | HAPPY |
| 5 | welcome | calendar strip | top | EXCITED |
| 6 | welcome | Quiz Builder | top | THINKING |
| 7 | welcome | Load Quiz | top | POINTING |
| 8 | welcome | question count | bottom | POINTING |
| 9 | welcome | difficulty | bottom | THINKING |
| 10 | welcome | Analytics | top | POINTING |
| 11 | welcome | Badges | top | EXCITED |
| 12 | welcome | Smart Practice | top | HAPPY |
| 13 | welcome | Choose Category | top | EXCITED |
| 14 | categories | category grid | top | POINTING |
| 15 | game | expression | bottom | THINKING |
| 16 | game | answer field | top | POINTING |
| 17 | game | hint button | top | THINKING |
| 18 | game | timer | bottom | POINTING |
| 19 | game | submit | top | HAPPY |
| 20 | results | grade label | right | EXCITED |
| 21 | results | Export Report | top | THINKING |
| 22 | results | Review Answers | top | POINTING |
| 23 | review | table | top | HAPPY |
| 24 | welcome | — (outro) | centre | WAVING |

---

## 11. Audio design

Format: PCM 44.1 kHz, 8-bit signed, mono; sine wave; linear fade-out envelope. Output level = `amp × volumeFactor` (volume/100; 0 when muted; default volume 70 %). "Gap" = `Thread.sleep` between notes.

| Event (method) | Notes | Duration | Amp × vol | Triggered by |
|---|---|---|--:|---|
| Hover (`playHover`) | 1200 Hz | 15 ms | 0.10 | button hover (instrumented buttons) |
| Click (`playClick`) | 600 Hz | 30 ms | 0.35 | button press; sound-menu selections |
| Correct (`playCorrect`) | C5 523.25 → E5 659.25 → G5 783.99 | 100/100/140 ms, 70 ms gaps | 0.5/0.5/0.6 | right answer |
| Incorrect (`playIncorrect`) | slide 220 → 150 Hz | 300 ms | 0.40 | wrong answer |
| Launch (`playLaunch`) | C4 261.63, E4 329.63, G4 392.00, C5 523.25 | 150/150/200/350 ms, 90 ms gaps | 0.4/0.4/0.5/0.5 | frame constructed |
| Transition (`playTransition`) | slide 400 → 800 Hz | 180 ms | 0.30 | navigation |
| Quiz start (`playQuizStart`) | C5, E5, G5, C6 1046.50 | 120/120/120/250 ms, 80 ms gaps | 0.5/0.5/0.5/0.6 | start of a quiz |
| Tick (`playTick`) | 900 Hz | 10 ms | 0.12 | each second of the question timer |
| Fanfare (`playFanfare`) | C4, E4, G4 played **one after another** (200 ms each), 150 ms gap, C5 (450 ms) | ≈ 1.2 s | 0.35/0.35/0.35/0.5 | quiz completed |
| Category select (`playCategorySelect`) | A5 880 Hz | 150 ms | 0.45 | **unused** |
| Streak (`playStreak`) | slide 300 → 900 Hz | 400 ms | 0.50 | **unused** |
| Achievement (`playAchievement`) | C6, E6 1318.51, G6 1567.98 | 100/100/300 ms | 0.4/0.4/0.5 | **unused** |
| Notification (`playNotification`) | 440 Hz ×2 | 80 + 150 ms | 0.40 | **unused** |

Mute and volume presets (100/75/50/25 %) are reached through the gated Sound menu. Rule R-AUD-*.

---

## 12. Data visualisation

| Chart | Specification |
|---|---|
| **Accuracy line chart** | Padding L40 R16 T12 B32. Y grid at 0/25/50/75/100 % (border colour; labels 9 pt muted). Points evenly spaced. Smooth S-curve between points (cubic Bézier with control points at the horizontal midpoint, y of each endpoint). Area fill: gold α80 → α0 vertical gradient. Stroke 2.5 px round gold. Markers: 8 px circles, card-coloured fill + 2 px gold ring. X labels: first (`1`), middle (if ≥ 5 points) and last (`n`). Needs ≥ 2 points else message "Complete a few sessions / to see your trend!". |
| **Category radar** | 10 spokes starting at 12 o'clock, clockwise in `ALL_CATEGORIES` order; rings at 25/50/75/100 % (labels for 25–75); data polygon gold α55 fill + 2 px gold outline; 8 px vertex dots in category colours (§3.4); labels 9 pt muted at radius + 12. Empty (all zero) → outline polygon + "No category data yet". |
| **Heatmap** | `GridLayout(4, 7, 5, 5)` of 12 × 12 px squares; gold = practised, border colour = not; oldest top-left → today bottom-right; caption "Heatmap (Last 28 Days): ". Binary (no intensity). |
| **Calendar strip (Home)** | Daily Challenge chip + 7 day units (weekday initial 9 pt, ● gold if practised else ○ border colour), oldest → today. |

---

## 13. Theming and scaling behaviour

1. `QuizFrame.updateAllThemes()` → `applyTheme()` on every panel → `scaleComponentFont()` → `updateComponentTreeUI()` ([architecture §6.7](architecture.md#67-theming-and-scaling)).
2. Panels create with the light palette then re-colour in `applyTheme()` (recursive `recolorTree` heuristics based on component type, font size or text — e.g. labels > 20 pt are "dark text").
3. Persisted: `darkMode`, `fontSizeScale`.
4. Toggle labels: theme button shows the *target* mode ("🌙 Dark" in light mode, "☀️ Light" in dark mode); scale button shows the current scale ("🔍 Scale: 125%").
5. Known gaps: dynamically rebuilt content may be unscaled until the next refresh (BUG-14); recolour heuristics key off font size/text and are fragile (DEBT-05).

---

## 14. Accessibility

| Area | Current | Gap / target |
|---|---|---|
| Text size | 3 levels (100/125/150 %), default 125 % | HTML-embedded sizes don't scale |
| Contrast | See §3.5 | Gold/green/red on light fail AA (FEAT-05) |
| Keyboard | Category screen fully; Game via Enter/Esc/F1/Ctrl+H; Tab navigation on Swing defaults | Tour is mouse-only; no visible focus ring on custom-styled buttons (`setFocusPainted(false)`); verify Analytics/Shop |
| Screen readers | No accessible names/descriptions set | Add `AccessibleContext` names (FEAT-05) |
| Colour independence | Review rows use ✅/❌ text as well as colour; badges use 🔒 vs emoji | Heatmap relies on colour only |
| Motion | No animations except tour overlay repaint | — |
| Audio | Optional, volume/mute | No visual substitute needed for core flow (feedback is textual) |
| Hit targets | Buttons ≥ ~30 px high at 125 % | Verify small chips at 100 % |

---

## 15. Assets and iconography

| Asset | Details |
|---|---|
| `src/com/mathquiz/resources/logo.png` | 1024 × 1024, 281,993 B, **JPEG-encoded** (content) with `.png` extension; loaded via `getResource("/com/mathquiz/resources/logo.png")` and scaled with `SCALE_SMOOTH` (Home 60, Help 55, Analytics 32, window icon full size). Embedded as Base64 `data:image/png` in HTML reports. |
| `logo.ico` | Generated by `IcoConverter`: single 256 × 256 PNG-compressed image in an ICO container (32 bpp); not committed. |
| Icons | Unicode emoji only (no icon font/images). `★` is the star glyph. |
| Fonts | System fonts only: Serif, SansSerif, Segoe UI Emoji. |

---

## 16. Design debt and improvement list

| ID | Item | Task |
|---|---|---|
| 1 | Each view keeps private light-palette constants and re-colours with heuristics; centralise in `AppTheme` and component styles | DEBT-05 |
| 2 | Primary/secondary button creation is copy-pasted in ~10 panels; extract a `Styles` helper | DEBT-05 |
| 3 | Contrast failures for gold/green/red text on light backgrounds | FEAT-05 |
| 4 | No focus indication on custom buttons; tour not keyboard-accessible | FEAT-05 |
| 5 | Unlock moment for badges (toast, sound, +15 ★) missing; sounds exist but unused | BUG-06 |
| 6 | Results "STEP UP" buttons don't change the difficulty | BUG-02 |
| 7 | Real PNG with transparency for the logo (current file is JPEG with a white/solid background) | DEBT-13 |
| 8 | Heatmap could encode intensity (sessions per day) and add tooltips | FEAT |
| 9 | Help copy fixed on 2026-11-08 (DOC-02). The **tour** copy still describes mascot feedback, animation and "interactive" charts that do not exist | DOC-06 |
| 10 | Possible clipping at 150 % in the 720 × 550 minimum window — verify every screen | BUG-14 |
