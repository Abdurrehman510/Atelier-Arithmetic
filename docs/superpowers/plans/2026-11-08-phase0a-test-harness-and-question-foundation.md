# Phase 0A — Test Harness, Schema Versioning and Question Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Give the app an automated test harness, a versioned database, and questions that carry a skill tag, a checkable solution trace and model-side answer checking, with **no visible change** for children.

**Architecture:** First pin today's generator output with a golden-master file and invariant tests. Then split `QuestionGenerator` (576 lines) into package-private per-category classes that also return a `Skill` and `SolutionStep`s. These are computed from values already drawn, with no extra random draws, so seeded output stays byte-identical. `SessionRepository` gets `PRAGMA user_version` migrations and stores skill, per-question difficulty and hint presses for every answer.

**Tech Stack:** Java (JDK 17 API, built with JDK 21 using `--release 17`), Swing, SQLite via `lib/sqlite-jdbc-3.42.0.0.jar`, JUnit 5 console launcher 1.11.4 (test-only), PowerShell test script (Windows PowerShell 5.1 and pwsh 7).

**Spec:**
- `docs/premium-roadmap.md`: Part C (Phase 0 row and exit criteria), Part D (G0, A1, A8), Part E (data model).
- `docs/task.md`: DEBT-01, DEBT-02, DEBT-11, FEAT-02, FEAT-11.
- `docs/rules.md`: all MUST rules apply.

**Scope:** plan 1 of 2 for Phase 0. Plan 2 is `2026-11-08-phase0b-macos-build-pipeline.md` (X1). X3 (recruiting assessment advisors) is not software and is not planned here.

**Decisions this plan makes (owner may override before Task 1):**
1. **The `Task` interface is deferred.** Phase 0 extends `Question` with skill, solution and `check()`. The interface arrives with the first non-numeric mechanic (Phase 3), when its real shape is known. Task 11 records this in the roadmap.
2. **Initial skill set (A1)** = one skill per existing generator sub-type: 55 skills plus `custom`. Prerequisite links wait for the advisors (X3).
3. **Only columns with real data are added now:** `skill_id`, per-question `difficulty`, `hints_used`. The confidence, error-type, timer-visible and mode columns arrive with the features that fill them.
4. **`skill_id` and per-question `difficulty` are encrypted** with `CryptoHelper`, like `sessions.category` (R-SEC-01).
5. **Test framework:** JUnit 5 console standalone 1.11.4, vendored test-only in `lib-test/`, never packaged. This **needs the owner's approval** (R-ARC-09).

## Global Constraints

- JDK 17 API only (R-COD-03). The test script compiles with `--release 17`.
- Compile with `-encoding UTF-8` and zero warnings: `javac -encoding UTF-8 -cp "lib/*" -sourcepath src src/com/mathquiz/QuizApp.java -d <tmpdir>` (R-TST-01, R-BLD-01).
- No network use at runtime (R-PRD-01). No new runtime dependency (R-ARC-09). The JUnit jar is test-only.
- `generateSeeded` stays a pure function of `(difficulty, category, seed, index)` (R-DOM-04). After the refactor, every line of the golden master must still match exactly.
- Every generated answer is an `int` with no overflow on the way; answers are exact and at least 1 (R-DOM-01, R-DOM-02). Expressions use ASCII `+ - * /` (R-DOM-03).
- Persisted strings never change: category names (R-DOM-07) and, from Task 3, skill IDs (new rule R-DOM-12).
- Expressions, profile names, categories and the new `skill_id`/`difficulty` columns are stored encrypted (R-SEC-01). All SQL uses `PreparedStatement` (R-COD-07).
- Schema changes go through `PRAGMA user_version` migrations, are backward-compatible, and never drop user data (R-DAT-03).
- Classes stay under about 500 lines (R-ARC-06). Packages stay `com.mathquiz.{config,model,service,util,view,view.tour}` (R-COD-01). Business rules stay out of panels (R-ARC-03).
- No visible UI or copy change in Phase 0. All feedback messages stay word-for-word.
- Tests never touch a real data folder (R-TST-07). They run only through `test.ps1`, which sets `user.home` to `build/test/home`.
- Work happens on branch `feat/phase0a-foundation` (R-GIT-01).
- Commit subjects are Conventional Commits (R-GIT-02). Bodies cite task IDs (R-GIT-03) and end with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>` (R-GIT-04).
- A change to the DB schema, config or user-visible behaviour updates the matching PRD/architecture section in the same commit (R-DOC-02).

## Review Focus

1. **A database written by a newer app version** (a parent reinstalls an older build). It must open and save without migrating down or deleting anything. Test: Task 9 `newerDatabaseIsLeftAlone`.
2. **A migration that fails half-way** (for example, a column already exists). History must still load and new sessions must still save, in legacy mode. Tests: Task 9 `failedMigrationKeepsHistoryReadable`, Task 10 `legacyModeStillSavesAndLoads`.
3. **Custom quizzes** from the Quiz Builder or loaded JSON have no generator skill. They must play and save exactly as before, stored with skill `custom`. Test: Task 10 `customQuizSessionSavesWithCustomSkill`.
4. **Daily Challenge fairness.** The same date must produce the same 10 questions before and after the refactor. Test: Task 2 golden master (dedicated Daily block for seed `20261108`) and Task 10 `dailyChallengeIsSeededMixed`.
5. **A difficulty change mid-session** (accepting "Level Up?"). Each saved question must carry the level it was actually generated at, not the session's final level. Tests: Task 10 `difficultyChangeAppliesToTheNextQuestion`, plus the manual check in Task 10.

---

## File structure

| Path | Status | Responsibility |
|---|---|---|
| `lib-test/junit-platform-console-standalone-1.11.4.jar` | new (binary) | Test runner, test-only |
| `test.ps1` | new | Compile main and test sources, run JUnit with a throw-away `user.home` |
| `test/com/mathquiz/...` | new | Tests mirror the main packages so package-private classes are reachable |
| `test/resources/golden/question-generator-v1.tsv` | new (generated) | Golden master of seeded generator output |
| `src/com/mathquiz/model/Skill.java` | new | Persisted skill catalog (A1) |
| `src/com/mathquiz/model/SolutionStep.java` | new | One exact arithmetic step (A8) |
| `src/com/mathquiz/model/AnswerCheck.java` | new | Result of checking a typed answer |
| `src/com/mathquiz/model/Question.java` | modify | Adds skill, solution, `check()` |
| `src/com/mathquiz/model/QuestionResult.java` | modify | Adds skill, difficulty, hints used |
| `src/com/mathquiz/service/ArithmeticQuestions.java` | new | Addition, Difference, Multiplication, Division |
| `src/com/mathquiz/service/OrderOfOperationsQuestions.java` | new | Special |
| `src/com/mathquiz/service/FractionQuestions.java` | new | Fractions (incl. percentages, ratios) |
| `src/com/mathquiz/service/PatternQuestions.java` | new | Patterns |
| `src/com/mathquiz/service/AlgebraQuestions.java` | new | Algebra |
| `src/com/mathquiz/service/MeasurementQuestions.java` | new | Measurement |
| `src/com/mathquiz/service/QuestionGenerator.java` | modify | Facade only: `generate`, `generateSeeded`, Mixed choice, fallback |
| `src/com/mathquiz/service/QuestionSource.java` | new | Question precedence (custom → Daily → generator), moved out of `GamePanel` |
| `src/com/mathquiz/service/SchemaMigrations.java` | new | `user_version` migrations |
| `src/com/mathquiz/service/SessionRepository.java` | modify | Injectable DB path/profile, migrations, new columns |
| `src/com/mathquiz/view/GamePanel.java` | modify | Uses `QuestionSource`, `Question.check`, counts hints |
| `run.bat`, `package.ps1` | modify | `-encoding UTF-8` |

---

### Task 1: Test harness and UTF-8 build flags

**Files:**
- Create: `lib-test/junit-platform-console-standalone-1.11.4.jar`, `test.ps1`, `test/com/mathquiz/testutil/Results.java`, `test/com/mathquiz/TestEnvironmentTest.java`, `test/com/mathquiz/model/QuizSessionGradeTest.java`
- Modify: `.gitignore` (add `!lib-test/*.jar` after `!lib/*.jar`), `run.bat:6`, `package.ps1:62`, `docs/architecture.md` §2 (Tests row), `docs/rules.md` (R-COD-02 and R-BLD-01 "Now" → ✔; delete the R-COD-02/R-BLD-01 row in §16), `docs/task.md` (DEBT-01, DEBT-02 progress notes)

**Interfaces:**
- Produces: `.\test.ps1`, which exits 0 only when everything compiles and every test passes. All later tasks use it.
- Produces the test helpers that every later test uses to build results (Task 10 is the only place that changes them):
  - `Results.correct(String expression, int answer) : QuestionResult`
  - `Results.wrong(String expression, int answer, int given) : QuestionResult`

- [ ] **Step 0: Confirm the owner approved the test-only JUnit jar (R-ARC-09).** If not approved, stop and report.

- [ ] **Step 1: Download the jar and verify its checksum**

```powershell
New-Item -ItemType Directory -Force lib-test | Out-Null
$url = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar"
Invoke-WebRequest $url -OutFile lib-test/junit-platform-console-standalone-1.11.4.jar
Invoke-WebRequest "$url.sha1" -OutFile "$env:TEMP\junit.sha1"
(Get-FileHash lib-test/junit-platform-console-standalone-1.11.4.jar -Algorithm SHA1).Hash.ToLower() -eq (Get-Content "$env:TEMP\junit.sha1" -Raw).Trim()
```
Expected: `True`. Licence: EPL-2.0 (test-only, not shipped).

- [ ] **Step 2: Write `test.ps1`.** Behaviour, in order:
  1. `$ErrorActionPreference = 'Stop'`, then `Set-Location $PSScriptRoot`.
  2. Delete and recreate only `build/test`.
  3. Set `$sep = [IO.Path]::PathSeparator`.
  4. Write all `src/**/*.java` paths to `build/test/main-sources.txt` (forward slashes, one quoted path per line, `Set-Content -Encoding ascii`). Run `javac -encoding UTF-8 --release 17 -cp "lib/*" -d build/test/main "@build/test/main-sources.txt"`.
  5. Do the same for `test/**/*.java` into `build/test/classes`, with classpath `build/test/main$sep` + `lib/*$sep` + `lib-test/*`.
  6. After each `javac`, `exit $LASTEXITCODE` if it is non-zero. Native commands do not throw in PowerShell 5.1.
  7. Create `build/test/home`, then run:
     `java "-Duser.home=<absolute path of build/test/home>" -jar lib-test/junit-platform-console-standalone-1.11.4.jar execute --class-path "build/test/classes$sep" + "build/test/main$sep" + "lib/sqlite-jdbc-3.42.0.0.jar" --scan-class-path build/test/classes --fail-if-no-tests --disable-banner --details=tree`
  8. `exit $LASTEXITCODE`.

- [ ] **Step 3: Write the tests**

```java
// test/com/mathquiz/TestEnvironmentTest.java
@Test
void testsRunWithAThrowAwayHomeFolder() {
    String home = System.getProperty("user.home").replace('\\', '/');
    assertTrue(home.endsWith("/build/test/home"),
            "Run tests through test.ps1 only (R-TST-07); user.home was " + home);
    assertTrue(AppConfig.getAppDir().replace('\\', '/').startsWith(home));
}
```

```java
// test/com/mathquiz/model/QuizSessionGradeTest.java
// session = new QuizSession(total, "Easy", "Addition"); record `correct` × Results.correct(...) then the rest × Results.wrong(...)
@ParameterizedTest
@CsvSource(delimiter = '|', quoteCharacter = '"', value = {
    "19|20|A++", "94|100|A+", "17|20|A+", "84|100|A", "39|50|A", "77|100|B+",
    "13|20|B+", "53|100|B", "2|5|C+", "1|3|C", "33|100|D", "0|10|D" })
void gradeFollowsThePrdThresholds(int correct, int total, String grade)

@ParameterizedTest
@CsvSource(delimiter = '|', quoteCharacter = '"', value = {
    "19|20|Outstanding — True Mastery!", "17|20|Excellent — Impressive Skills!",
    "39|50|Very Good — Strong Performance!", "13|20|Good — Solid Foundation!",
    "53|100|Progressing — Keep Practicing!", "2|5|Developing — You're Learning!",
    "1|3|Getting Started — Stay Curious!", "33|100|Room to Grow — Try an Easier Level!" })
void remarkStaysGrowthMinded(int correct, int total, String remark)
```

- [ ] **Step 4: Run `.\test.ps1`.** Expected: all tests PASS. These pin existing behaviour.

- [ ] **Step 5: Prove the grade test can fail.** Temporarily change `pct >= 95` to `pct > 95` in `QuizSession.getGrade()` and run `.\test.ps1`. Expected: FAIL on row `19|20|A++` (expected `A++`, got `A+`). Revert the change and run again. Expected: PASS.

- [ ] **Step 6: Add `-encoding UTF-8` to the `javac` lines in `run.bat` and `package.ps1`.** Then run `javac -encoding UTF-8 -cp "lib/*" -sourcepath src src/com/mathquiz/QuizApp.java -d $env:TEMP\aa-build`. Expected: no output.

- [ ] **Step 7: Update the docs** listed under Files.

- [ ] **Step 8: Commit**

```bash
git add lib-test test.ps1 test .gitignore run.bat package.ps1 docs/architecture.md docs/rules.md docs/task.md
git commit -m "test: add JUnit 5 harness, test.ps1 and grading tests" -m "DEBT-01 (harness, QuizSession grades), DEBT-02 (UTF-8 compile flags). JUnit console standalone 1.11.4 is test-only, approved by the owner (R-ARC-09)." -m "Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 2: Golden master and invariant tests for today's generator

**Files:**
- Create: `test/com/mathquiz/service/GoldenMasterWriter.java`, `test/resources/golden/question-generator-v1.tsv` (generated), `test/com/mathquiz/service/QuestionGeneratorGoldenMasterTest.java`, `test/com/mathquiz/service/QuestionGeneratorInvariantTest.java`
- Modify: `docs/rules.md` (R-DOM-11 and R-TST-03 "Now" → ✔ for generators)

**Interfaces:**
- Produces the fixture format: one comment header line starting with `#`, then tab-separated rows of `category`, `difficulty`, `seed`, `index`, `expression`, `answer`.
- Produces the sample set:
  - **Main block:** categories `Addition, Difference, Multiplication, Division, Mixed, Special, Fractions, Patterns, Algebra, Measurement` × difficulties `Easy, Medium, Hard` × `k = 0..49`, using `seed = 20260101 + k` and `index = k % 10` (1,500 rows).
  - **Daily block:** `Mixed`, `Medium`, seed `20261108`, index `0..9` (10 rows).
- Produces `QuestionGeneratorInvariantTest`, which Tasks 5–8 extend.

- [ ] **Step 1: Write `GoldenMasterWriter.main(String[] args)`.** It writes the samples above to `args[0]` in UTF-8 with `\n` line endings. The header is: `# category	difficulty	seed	index	expression	answer — generated from <git short hash>; do not edit by hand`.

- [ ] **Step 2: Generate the fixture.** Run `.\test.ps1` (to compile), then:
  `java -cp "build/test/classes;build/test/main" com.mathquiz.service.GoldenMasterWriter test/resources/golden/question-generator-v1.tsv`
  then `(Get-Content test/resources/golden/question-generator-v1.tsv).Count`.
  Expected: `1511`.

- [ ] **Step 3: Write the golden-master test**

```java
@Test
void seededOutputMatchesTheGoldenMaster() throws IOException {
    List<String> rows = Files.readAllLines(Path.of("test/resources/golden/question-generator-v1.tsv"), UTF_8)
            .stream().filter(l -> !l.startsWith("#")).toList();
    assertEquals(1510, rows.size(), "fixture truncated");
    for (String row : rows) {
        String[] f = row.split("\t");
        Question q = new QuestionGenerator().generateSeeded(f[1], f[0], Long.parseLong(f[2]), Integer.parseInt(f[3]));
        assertEquals(f[4], q.getExpression(), row);
        assertEquals(Integer.parseInt(f[5]), q.getCorrectAnswer(), row);
    }
}
```

- [ ] **Step 4: Write the invariant test** (R-DOM-11, R-TST-03). It is parameterised over the 10 categories × 3 difficulties, with 10,000 samples each via `generateSeeded(diff, cat, 1_000_000L + i, i % 10)`.

```java
assertDoesNotThrow(...);                       // the whole sample loop
assertFalse(q.getExpression().isBlank());
assertTrue(q.getCorrectAnswer() >= 1, q.getExpression());
assertTrue(q.getExpression().chars().noneMatch(c -> c == '×' || c == '÷' || c == '−'));

@Test
void unknownCategoryFallsBackToAddition()   // FR-QGN-04
// for s in 0..99: generateSeeded("Easy","Geometry",s,0) equals generateSeeded("Easy","Addition",s,0) (expression and answer)
```

- [ ] **Step 5: Run `.\test.ps1`.** Expected: PASS.

- [ ] **Step 6: Prove the golden master can fail.** Temporarily change Addition easy `random.nextInt(90) + 10` to `random.nextInt(90) + 11` and run. Expected: FAIL naming an `Addition	Easy` row. Revert and run again. Expected: PASS.

- [ ] **Step 7: Commit:** `test(generator): pin seeded output with a golden master and invariant tests`. Body: `DEBT-01, R-DOM-11.` plus the trailer.

---

### Task 3: Skill catalog

**Files:**
- Create: `src/com/mathquiz/model/Skill.java`, `test/com/mathquiz/model/SkillTest.java`
- Modify:
  - `docs/architecture.md`: §5.2 (add `Skill` row), and a new §7.6 "Skill catalog" listing the IDs and stating they are persisted keys.
  - `docs/rules.md`: add **R-DOM-12** "Skill IDs are persisted keys: never rename or reuse one; add new IDs only" (Now: ✔).

**Interfaces:**
- Produces:

```java
public enum Skill {
    /* constants in the table below, in this order */;
    public String id();                                // persisted key
    public String category();                          // stored category string (R-DOM-07) or "Custom"
    public static Optional<Skill> fromId(String id);   // empty for null or unknown
    public static List<Skill> forCategory(String category); // exact match, declaration order
}
```

| Constant | id | category | Produced by (today's sub-type) |
|---|---|---|---|
| ADD_2DIGIT_2TERMS | `add.2digit.2terms` | Addition | Easy |
| ADD_3DIGIT_3TERMS | `add.3digit.3terms` | Addition | Medium |
| ADD_4DIGIT_4TERMS | `add.4digit.4terms` | Addition | Hard |
| SUB_2DIGIT_2TERMS | `sub.2digit.2terms` | Difference | Easy |
| SUB_3DIGIT_3TERMS | `sub.3digit.3terms` | Difference | Medium |
| SUB_4DIGIT_4TERMS | `sub.4digit.4terms` | Difference | Hard |
| MUL_2DIGIT_BY_1DIGIT | `mul.2digit_by_1digit` | Multiplication | Easy |
| MUL_2DIGIT_3FACTORS | `mul.2digit.3factors` | Multiplication | Medium |
| MUL_3DIGIT_3FACTORS | `mul.3digit.3factors` | Multiplication | Hard |
| DIV_BASIC | `div.basic` | Division | Easy |
| DIV_CHAINED | `div.chained` | Division | Medium `(A / B) / C` |
| DIV_2DIGIT_QUOTIENT | `div.2digit_quotient` | Division | Hard |
| ORD_SUM_TIMES_DIFF | `ord.sum_times_diff` | Special | type 0 `(A + B) * (C - D)` |
| ORD_DIFF_OVER_SUM | `ord.diff_over_sum` | Special | type 1 |
| ORD_QUOTIENT_SUM | `ord.quotient_sum` | Special | type 2 |
| ORD_PRODUCT_OVER_PRODUCT | `ord.product_over_product` | Special | type 3 |
| ORD_QUOTIENT_MINUS_PRODUCT | `ord.quotient_minus_product` | Special | type 4 |
| FRAC_OF_QUANTITY | `frac.of_quantity` | Fractions | Easy; Medium sub 2 |
| FRAC_MIXED_TO_IMPROPER | `frac.mixed_to_improper` | Fractions | Medium sub 0 |
| FRAC_SIMPLIFY | `frac.simplify` | Fractions | Medium sub 1 |
| PCT_OF_QUANTITY | `pct.of_quantity` | Fractions | Hard sub 0 |
| RATIO_MISSING_PART | `ratio.missing_part` | Fractions | Hard sub 1 |
| FRAC_PRODUCT_OF_QUANTITY | `frac.product_of_quantity` | Fractions | Hard sub 2 |
| PAT_ARITHMETIC | `pat.arithmetic` | Patterns | Easy sub 0 |
| PAT_ALTERNATING_STEPS | `pat.alternating_steps` | Patterns | Easy sub 1 |
| PAT_GROWING_STEP | `pat.growing_step` | Patterns | Medium sub 0 |
| PAT_GEOMETRIC | `pat.geometric` | Patterns | Medium sub 1 |
| PAT_ALTERNATING_OPS | `pat.alternating_ops` | Patterns | Medium sub 2 |
| PAT_FIBONACCI | `pat.fibonacci` | Patterns | Hard sub 0 |
| PAT_SQUARES_OFFSET | `pat.squares_offset` | Patterns | Hard sub 1 |
| PAT_INTERLEAVED | `pat.interleaved` | Patterns | Hard sub 2 |
| PAT_POLYNOMIAL | `pat.polynomial` | Patterns | Hard sub 3 |
| ALG_ONE_STEP_ADD | `alg.one_step_add` | Algebra | Easy sub 0 `x + A = B` |
| ALG_ONE_STEP_SUB | `alg.one_step_sub` | Algebra | Easy sub 1 `x - A = B` |
| ALG_ONE_STEP_SUB_FROM | `alg.one_step_sub_from` | Algebra | Easy sub 2 `A - x = B` |
| ALG_ONE_STEP_MUL | `alg.one_step_mul` | Algebra | Easy sub 3 `Ax = B` |
| ALG_TWO_STEP | `alg.two_step` | Algebra | Medium sub 0, 1 `Ax ± B = C` |
| ALG_TWO_STEP_SUB_FROM | `alg.two_step_sub_from` | Algebra | Medium sub 2 `B - Ax = C` |
| ALG_TWO_STEP_DIV | `alg.two_step_div` | Algebra | Medium sub 3 `(x + B) / A = C` |
| ALG_VARS_BOTH_SIDES | `alg.vars_both_sides` | Algebra | Hard sub 0, 1 |
| ALG_SIMULTANEOUS_SUM_DIFF | `alg.simultaneous_sum_diff` | Algebra | Hard sub 2 |
| ALG_SIMULTANEOUS_ELIMINATION | `alg.simultaneous_elimination` | Algebra | Hard sub 3 |
| MEAS_CONVERT_LENGTH | `meas.convert_length` | Measurement | Easy sub 0, 1, 2 |
| MEAS_CONVERT_MASS | `meas.convert_mass` | Measurement | Easy sub 3 |
| MEAS_CONVERT_CAPACITY | `meas.convert_capacity` | Measurement | Easy sub 4 |
| TIME_CONVERT | `time.convert` | Measurement | Medium sub 0 |
| TIME_CONVERT_COMPOUND | `time.convert_compound` | Measurement | Medium sub 1 |
| GEO_PERIMETER_RECT | `geo.perimeter_rect` | Measurement | Medium sub 2 |
| GEO_AREA_SQUARE | `geo.area_square` | Measurement | Medium sub 3 |
| GEO_AREA_RECT | `geo.area_rect` | Measurement | Medium sub 4 |
| MEAS_CONVERT_LENGTH_COMPOUND | `meas.convert_length_compound` | Measurement | Hard sub 0 |
| MEAS_CONVERT_CAPACITY_COMPOUND | `meas.convert_capacity_compound` | Measurement | Hard sub 1 |
| GEO_AREA_FROM_PERIMETER | `geo.area_from_perimeter` | Measurement | Hard sub 2 |
| GEO_PERIMETER_FROM_AREA | `geo.perimeter_from_area` | Measurement | Hard sub 3 |
| GEO_VOLUME_PRISM | `geo.volume_prism` | Measurement | Hard sub 4 |
| CUSTOM | `custom` | Custom | Quiz Builder / loaded quizzes |

- [ ] **Step 1: Write the failing tests**

```java
@Test void skillIdsAreStable()
// assertEquals(List.of(<all 56 ids above, in table order>), Arrays.stream(Skill.values()).map(Skill::id).toList())
@Test void idsAreUniqueAndWellFormed()
// every id matches "[a-z]+(\\.[a-z0-9_]+)*"; new HashSet<>(ids).size() == Skill.values().length
@Test void everyGeneratorCategoryHasSkills()
// forCategory(c) non-empty for the 9 categories Addition…Measurement; forCategory("Custom") == List.of(CUSTOM)
@Test void fromIdRoundTripsAndRejectsUnknown()
// fromId(s.id()) == Optional.of(s) for all; fromId("nope").isEmpty(); fromId(null).isEmpty()
```

- [ ] **Step 2: Run `.\test.ps1`.** Expected: compile FAIL, because `Skill` does not exist yet.
- [ ] **Step 3: Implement `Skill`** exactly as in the table.
- [ ] **Step 4: Run `.\test.ps1`.** Expected: PASS.
- [ ] **Step 5: Commit:** `feat(model): add persisted skill catalog`. Body: `FEAT-11 (roadmap A1). New rule R-DOM-12.` plus the trailer.

---

### Task 4: Solution steps, answer checking and the extended `Question`

**Files:**
- Create: `src/com/mathquiz/model/SolutionStep.java`, `src/com/mathquiz/model/AnswerCheck.java`, `test/com/mathquiz/model/SolutionStepTest.java`, `test/com/mathquiz/model/QuestionTest.java`
- Modify:
  - `src/com/mathquiz/model/Question.java`
  - `docs/PRD.md` FR-QGN-01: "A `Question` consists of an expression, an `int` answer, a skill and an exact step-by-step solution (empty for custom questions)".
  - `docs/architecture.md` §5.2 rows.

**Interfaces:**
- Consumes: `Skill` (Task 3).
- Produces:

```java
public record SolutionStep(char op, int left, int right, int result) {
    // compact constructor: op ∈ {'+','-','*','/'}; result == left op right exactly
    // (division: right != 0 and left % right == 0); otherwise IllegalArgumentException. Negative values are allowed.
    public static SolutionStep of(char op, int left, int right);   // computes result; Math.*Exact → ArithmeticException on overflow
}
public record AnswerCheck(Status status, int value) {
    public enum Status { EMPTY, NOT_A_WHOLE_NUMBER, CORRECT, INCORRECT }
    public boolean isAnswered();                                    // CORRECT or INCORRECT
}
public final class Question {
    public Question(String expression, int correctAnswer);          // Skill.CUSTOM, empty solution
    public Question(String expression, int correctAnswer, Skill skill, List<SolutionStep> solution);
    // null skill → NullPointerException; non-empty solution whose last result != correctAnswer → IllegalArgumentException
    public String getExpression();  public int getCorrectAnswer();
    public Skill getSkill();        public List<SolutionStep> getSolution();   // unmodifiable copy
    public AnswerCheck check(String rawInput);
    // null or blank after trim → EMPTY; Integer.parseInt(trimmed) fails → NOT_A_WHOLE_NUMBER; equal → CORRECT; else INCORRECT
}
```

- [ ] **Step 1: Write the failing tests**

```java
// SolutionStepTest
assertEquals(85, SolutionStep.of('+', 47, 38).result());
assertEquals(25, SolutionStep.of('-', 43, 18).result());
assertEquals(-2, SolutionStep.of('-', 3, 5).result());
assertEquals(84, SolutionStep.of('*', 12, 7).result());
assertEquals(12, SolutionStep.of('/', 84, 7).result());
assertThrows(IllegalArgumentException.class, () -> SolutionStep.of('/', 10, 4));
assertThrows(IllegalArgumentException.class, () -> SolutionStep.of('/', 5, 0));
assertThrows(IllegalArgumentException.class, () -> SolutionStep.of('%', 1, 2));
assertThrows(ArithmeticException.class,     () -> SolutionStep.of('*', Integer.MAX_VALUE, 2));
assertThrows(IllegalArgumentException.class, () -> new SolutionStep('+', 2, 2, 5));

// QuestionTest
@Test void customConstructorUsesCustomSkillAndNoSteps()        // skill CUSTOM, solution empty
@Test void solutionMustEndAtTheAnswer()
// new Question("47 + 38", 86, Skill.ADD_2DIGIT_2TERMS, List.of(SolutionStep.of('+', 47, 38))) → IllegalArgumentException
@Test void solutionIsUnmodifiable()                             // getSolution().add(...) → UnsupportedOperationException
@Test void checkOfNullIsEmpty()                                 // new Question("6 * 7", 42).check(null).status() == EMPTY
@ParameterizedTest  // question "6 * 7" = 42
@CsvSource(delimiter = '|', quoteCharacter = '"', value = {
    "42|CORRECT|42", "\" 42 \"|CORRECT|42", "042|CORRECT|42", "+42|CORRECT|42", "٤٢|CORRECT|42",
    "41|INCORRECT|41", "-42|INCORRECT|-42",
    "\"\"|EMPTY|0", "\"   \"|EMPTY|0",
    "abc|NOT_A_WHOLE_NUMBER|0", "4 2|NOT_A_WHOLE_NUMBER|0", "3.5|NOT_A_WHOLE_NUMBER|0", "99999999999|NOT_A_WHOLE_NUMBER|0" })
void checkMatchesTodaysParsingRules(String input, AnswerCheck.Status status, int value)
// assert status always; assert value only when status.isAnswered()
```

- [ ] **Step 2: Run `.\test.ps1`.** Expected: compile FAIL (missing types).
- [ ] **Step 3: Implement** `SolutionStep`, `AnswerCheck` and the `Question` changes. Existing callers (`CustomQuizService`, `QuizBuilderPanel`, the generator) keep compiling through the two-argument constructor.
- [ ] **Step 4: Run `.\test.ps1`.** Expected: PASS, including the golden master.
- [ ] **Step 5: Commit:** `feat(model): add solution steps and answer checking to Question`. Body: `FEAT-11 (roadmap A8, G0 part 1), R-ARC-03.` plus the trailer.

---

### Tasks 5–8: shared rules for splitting the generator

These rules apply to every generator task:

- **Random draws are untouchable.** Each new method draws random numbers in exactly the same order, with the same bounds, as the private method it replaces. That includes the retry loops in `gcd`, `while (b <= 0)` and similar. Steps are computed from values already drawn. Expression text and answers do not change.
- **Each new class is package-private** (`final class XxxQuestions` in `com.mathquiz.service`) with static methods `Question name(String diff, Random rng)`, where `diff` is lower-case `easy|medium|hard`. `QuestionGenerator.generate` lower-cases its input as today and calls them. The old private methods are deleted.
- **Steps follow the natural working order**, with one operation per `SolutionStep.of(...)`. The last step's result is the answer, which the `Question` constructor enforces. Where the steps table below says `−|step|`, a negative step is written as a subtraction of its absolute value.
- **The invariant test grows with each task.** `QuestionGeneratorInvariantTest` gains a map `EXPECTED_SKILLS: category → difficulty → Set<Skill>` and a set `MIGRATED` of categories. For every category in `MIGRATED` (with `Mixed` added in Task 5), over the 10,000 samples per difficulty, it asserts:

```java
assertNotEquals(Skill.CUSTOM, q.getSkill());
assertFalse(q.getSolution().isEmpty());
assertEquals(q.getCorrectAnswer(), q.getSolution().get(q.getSolution().size() - 1).result());
seen.add(q.getSkill());
// after the loop:
assertEquals(EXPECTED_SKILLS.get(cat).get(diff), seen);   // exact set; Mixed = union of the four core categories at that difficulty
```

- **Before implementing**, each task first adds its categories to `MIGRATED` and `EXPECTED_SKILLS`, then runs `.\test.ps1` and expects FAIL (skill `CUSTOM`, empty solution). After implementing, the golden master **and** the invariants must PASS.

---

### Task 5: Arithmetic generators emit skills and steps

**Files:**
- Create: `src/com/mathquiz/service/ArithmeticQuestions.java`
- Modify: `src/com/mathquiz/service/QuestionGenerator.java` (delegate and delete four private methods), `test/com/mathquiz/service/QuestionGeneratorInvariantTest.java`

**Interfaces:**
- Consumes: `Skill`, `SolutionStep`, `Question` (Tasks 3–4).
- Produces: `ArithmeticQuestions.addition / difference / multiplication / division (String diff, Random rng) : Question`

| Skill | Steps |
|---|---|
| ADD_* | running sum left to right: `a+b`, then `+c`, `+d` |
| SUB_* | running difference left to right |
| MUL_* | running product left to right |
| DIV_BASIC, DIV_2DIGIT_QUOTIENT | `dividend ÷ divisor` |
| DIV_CHAINED | `dividend ÷ divisor = middle`; `middle ÷ extra` |

`EXPECTED_SKILLS`: each of Addition, Difference, Multiplication and Division maps Easy, Medium and Hard to the single skill in the Task 3 table. `MIGRATED` = {Addition, Difference, Multiplication, Division, Mixed}.

- [ ] **Step 1: Extend the invariant test** as described in the shared rules, and add:

```java
@Test void unknownCategoryUsesAnAdditionSkill()
// generateSeeded("Easy","Geometry",7L,0).getSkill() == Skill.ADD_2DIGIT_2TERMS
```

- [ ] **Step 2: Run `.\test.ps1`.** Expected: FAIL (Addition skill is `CUSTOM`).
- [ ] **Step 3: Implement `ArithmeticQuestions`** and delegate from `QuestionGenerator`.
- [ ] **Step 4: Run `.\test.ps1`.** Expected: PASS, with the golden master unchanged.
- [ ] **Step 5: Commit:** `refactor(generator): move arithmetic generators to ArithmeticQuestions with skills and steps`. Body: `FEAT-11 (A1, A8). Golden master unchanged.` plus the trailer.

---

### Task 6: Special and Fractions

**Files:**
- Create: `src/com/mathquiz/service/OrderOfOperationsQuestions.java` (`special`), `src/com/mathquiz/service/FractionQuestions.java` (`fractions`; `gcd` moves here)
- Modify: `QuestionGenerator.java`, `QuestionGeneratorInvariantTest.java` (`MIGRATED` += Special, Fractions)

| Skill | Steps (generator variable names) |
|---|---|
| ORD_SUM_TIMES_DIFF | `a+b=t1`; `c−d=t2`; `t1×t2` |
| ORD_DIFF_OVER_SUM | `a−b=t1`; `c+d=t2`; `t1÷t2` |
| ORD_QUOTIENT_SUM | `a÷b=q1`; `c÷d=q2`; `q1+q2` |
| ORD_PRODUCT_OVER_PRODUCT | `a×b=t1`; `c×d=t2`; `t1÷t2` |
| ORD_QUOTIENT_MINUS_PRODUCT | `a÷b=q1`; `c×d=t`; `q1−t` |
| FRAC_OF_QUANTITY | `total÷b=m`; `m×a` (Easy total = `c`; Medium sub 2 total = `b*mult`) |
| FRAC_MIXED_TO_IMPROPER | `a×c=t`; `t+b` |
| FRAC_SIMPLIFY | `(b*d)÷d` |
| PCT_OF_QUANTITY | `x×p=t`; `t÷100` |
| RATIO_MISSING_PART | `valA÷a=f`; `f×b` |
| FRAC_PRODUCT_OF_QUANTITY | `b×d=q`; `E÷q=m`; `a×c=n`; `m×n` |

`EXPECTED_SKILLS`:
- Special: all five ORD skills at every difficulty.
- Fractions Easy: {FRAC_OF_QUANTITY}.
- Fractions Medium: {FRAC_MIXED_TO_IMPROPER, FRAC_SIMPLIFY, FRAC_OF_QUANTITY}.
- Fractions Hard: {PCT_OF_QUANTITY, RATIO_MISSING_PART, FRAC_PRODUCT_OF_QUANTITY}.

- [ ] **Step 1:** Extend the invariant test. **Step 2:** Run it and expect FAIL. **Step 3:** Implement. **Step 4:** Run it and expect PASS. **Step 5:** Commit `refactor(generator): move Special and Fractions generators with skills and steps` (body `FEAT-11`, trailer).

---

### Task 7: Patterns

**Files:**
- Create: `src/com/mathquiz/service/PatternQuestions.java` (`patterns`)
- Modify: `QuestionGenerator.java`, `QuestionGeneratorInvariantTest.java` (`MIGRATED` += Patterns)

| Skill | Steps |
|---|---|
| PAT_ARITHMETIC | `s4+step`, or `s4−|step|` when `step < 0` |
| PAT_ALTERNATING_STEPS | `s5+a` |
| PAT_GROWING_STEP | `s5−s4=last`; `last+k=next`; `s5+next` |
| PAT_GEOMETRIC | `s4×r` |
| PAT_ALTERNATING_OPS | `s5×2` |
| PAT_FIBONACCI | `s5+s6` |
| PAT_SQUARES_OFFSET | `6×6=36`; `36+c`, or `36−c` when the sign is negative |
| PAT_INTERLEAVED | `s5+d` |
| PAT_POLYNOMIAL | `n(n+1)`: `6×7`. `n²−n`: `6×6=36`; `36−6` |

`EXPECTED_SKILLS`:
- Easy: {PAT_ARITHMETIC, PAT_ALTERNATING_STEPS}
- Medium: {PAT_GROWING_STEP, PAT_GEOMETRIC, PAT_ALTERNATING_OPS}
- Hard: {PAT_FIBONACCI, PAT_SQUARES_OFFSET, PAT_INTERLEAVED, PAT_POLYNOMIAL}

- [ ] **Step 1:** Extend the invariant test. **Step 2:** Run it and expect FAIL. **Step 3:** Implement. **Step 4:** Run it and expect PASS. **Step 5:** Commit `refactor(generator): move Patterns generator with skills and steps` (body `FEAT-11`, trailer).

---

### Task 8: Algebra and Measurement; `QuestionGenerator` becomes a facade

**Files:**
- Create: `src/com/mathquiz/service/AlgebraQuestions.java` (`algebra`), `src/com/mathquiz/service/MeasurementQuestions.java` (`measurement`)
- Modify:
  - `QuestionGenerator.java`: only `random`, `generate`, `generateSeeded`, the Mixed choice and the fallback remain.
  - `QuestionGeneratorInvariantTest.java`: delete `MIGRATED`; the assertions now cover every category.
  - `docs/architecture.md` §5.2: `QuestionGenerator` becomes a facade; add six rows.
  - `docs/rules.md`: remove `QuestionGenerator` from the R-ARC-06 lists in §3 and §16.

| Skill | Steps |
|---|---|
| ALG_ONE_STEP_ADD | `B−A` |
| ALG_ONE_STEP_SUB | `B+A` (`B` may be negative) |
| ALG_ONE_STEP_SUB_FROM | `A−B` |
| ALG_ONE_STEP_MUL | `B÷A` |
| ALG_TWO_STEP | `Ax+B=C`: `C−B=t`; `t÷A`. `Ax−B=C`: `C+B=t`; `t÷A` |
| ALG_TWO_STEP_SUB_FROM | `B−C=t`; `t÷A` |
| ALG_TWO_STEP_DIV | `A×C=t`; `t−B` |
| ALG_VARS_BOTH_SIDES | `A−C=k`; then `D−B=t` (sub 0) or `D+B=t` (sub 1); `t÷k` |
| ALG_SIMULTANEOUS_SUM_DIFF | `sum+difference=t`; `t÷2` |
| ALG_SIMULTANEOUS_ELIMINATION | `doubleSum−sum` |
| MEAS_CONVERT_LENGTH | `val×100` (m→cm), `val×10` (cm→mm), `val×1000` (km→m) |
| MEAS_CONVERT_MASS, MEAS_CONVERT_CAPACITY | `val×1000` |
| TIME_CONVERT | `val×60` |
| TIME_CONVERT_COMPOUND | `h×60=t`; `t+m` |
| GEO_PERIMETER_RECT | `l+w=t`; `t×2` |
| GEO_AREA_SQUARE | `s×s` |
| GEO_AREA_RECT | `l×w` |
| MEAS_CONVERT_LENGTH_COMPOUND | `m×100=t`; `t+cm` |
| MEAS_CONVERT_CAPACITY_COMPOUND | `l×1000=t`; `ml+t` |
| GEO_AREA_FROM_PERIMETER | `p÷4=s`; `s×s` |
| GEO_PERIMETER_FROM_AREA | `a÷l=w`; `l+w=t`; `t×2` |
| GEO_VOLUME_PRISM | `l×w=t`; `t×h` |

`EXPECTED_SKILLS`:
- Algebra Easy: the four ONE_STEP skills.
- Algebra Medium: {ALG_TWO_STEP, ALG_TWO_STEP_SUB_FROM, ALG_TWO_STEP_DIV}.
- Algebra Hard: {ALG_VARS_BOTH_SIDES, ALG_SIMULTANEOUS_SUM_DIFF, ALG_SIMULTANEOUS_ELIMINATION}.
- Measurement Easy: {MEAS_CONVERT_LENGTH, MEAS_CONVERT_MASS, MEAS_CONVERT_CAPACITY}.
- Measurement Medium: {TIME_CONVERT, TIME_CONVERT_COMPOUND, GEO_PERIMETER_RECT, GEO_AREA_SQUARE, GEO_AREA_RECT}.
- Measurement Hard: the five Hard skills in the table.

- [ ] **Step 1: Extend the invariant test** for all categories, and add:

```java
@Test void everySkillExceptCustomIsProducedSomewhere()
// union of all EXPECTED_SKILLS sets == EnumSet.complementOf(EnumSet.of(Skill.CUSTOM))
```

- [ ] **Step 2: Run `.\test.ps1`.** Expected: FAIL.
- [ ] **Step 3: Implement**, then check `(Get-Content src/com/mathquiz/service/QuestionGenerator.java).Count`. Expected: under 100. Every new class must be under 500 lines.
- [ ] **Step 4: Run `.\test.ps1`.** Expected: PASS.
- [ ] **Step 5: Commit:** `refactor(generator): finish split; QuestionGenerator is a facade`. Body: `FEAT-11 (A1, A8). Resolves R-ARC-06 for QuestionGenerator.` plus the trailer.

---

### Task 9: Versioned database schema (DEBT-11)

**Files:**
- Create: `src/com/mathquiz/service/SchemaMigrations.java`, `test/com/mathquiz/service/LegacyDatabaseFixture.java`, `test/com/mathquiz/service/SchemaMigrationsTest.java`
- Modify:
  - `src/com/mathquiz/service/SessionRepository.java`
  - `docs/architecture.md` §7.2: schema v2, `user_version`, indexes, legacy mode.
  - `docs/rules.md`: R-DAT-03 "Now" → ✔.

**Interfaces:**
- Produces:

```java
final class SchemaMigrations {
    static final int CURRENT_VERSION = 2;
    /** Upgrades in one transaction and returns the version afterwards.
     *  A database newer than CURRENT_VERSION is left untouched (its version is returned).
     *  If a step fails: roll back, log one line to System.err (no personal data), return the unchanged version. */
    static int migrate(Connection conn) throws SQLException;   // throws only if user_version cannot be read
}
public SessionRepository(String dbPath, Supplier<String> currentProfile);  // creates parent dirs, runs migrations
public SessionRepository();   // = this(AppConfig.getAppDir() + File.separator + "atelier_arithmetic.db",
                              //        () -> AppConfig.getInstance().getCurrentProfile())
int getSchemaVersion();       // package-private; version after migration (Task 10 branches on it)
// LegacyDatabaseFixture (test util):
static void createV1(Path db, String profile);   // today's DDL, user_version 0, 1 session ("Addition","Easy","A+")
                                                 // with questions "12 + 30"=42 (answered 42) and "50 - 8"=42 (answered 41); encrypted like save()
```

- **Migration SQL.** Step 0→1 is the two existing `CREATE TABLE IF NOT EXISTS` statements, moved verbatim out of `initDatabase`, then `PRAGMA user_version = 1`. Step 1→2:

```sql
ALTER TABLE session_questions ADD COLUMN skill_id TEXT;
ALTER TABLE session_questions ADD COLUMN difficulty TEXT;
ALTER TABLE session_questions ADD COLUMN hints_used INTEGER NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_sessions_profile ON sessions(profile_name);
CREATE INDEX IF NOT EXISTS idx_session_questions_session ON session_questions(session_id);
PRAGMA user_version = 2;
```

- [ ] **Step 1: Write the failing tests.** Each uses `@TempDir Path dir` and profile `"Guest"`.

```java
@Test void freshDatabaseIsCreatedAtCurrentVersion()
// user_version == 2; PRAGMA table_info(session_questions) contains skill_id, difficulty, hints_used;
// sqlite_master has indexes idx_sessions_profile and idx_session_questions_session
@Test void legacyDatabaseUpgradesAndKeepsHistory()
// createV1 → open → version 2; loadRaw() has 1 session, category "Addition", questions "12 + 30" (correct) and "50 - 8" (wrong);
// direct SQL: skill_id IS NULL, difficulty IS NULL, hints_used = 0 for both rows
@Test void openingTwiceIsHarmless()
// open, open again → version 2, still exactly 1 session
@Test void newerDatabaseIsLeftAlone()                         // Review Focus 1
// createV1, open (→2), then set PRAGMA user_version = 3 → open again → version 3; save(new session) succeeds; loadRaw().size() == 2
@Test void failedMigrationKeepsHistoryReadable()              // Review Focus 2
// createV1, then ALTER TABLE session_questions ADD COLUMN skill_id TEXT (half-migrated) → open → the 1→2 step fails,
// the single transaction rolls back, getSchemaVersion() == 0 (legacy mode = any version < 2);
// loadRaw() still returns the session with both questions
```

- [ ] **Step 2: Run `.\test.ps1`.** Expected: compile FAIL (no constructor or `SchemaMigrations`).
- [ ] **Step 3: Implement.** `initDatabase` creates the parent directory, runs `SchemaMigrations.migrate`, and stores the result. All SQL uses `PreparedStatement` except fixed DDL and pragmas.
- [ ] **Step 4: Run `.\test.ps1`.** Expected: PASS.
- [ ] **Step 5: Commit:** `feat(db): version the schema with PRAGMA user_version and add v2 columns`. Body: `DEBT-11, FEAT-02, FEAT-11 (roadmap Part E, minimal subset).` plus the trailer.

---

### Task 10: Record skill, per-question difficulty and hint presses

**Files:**
- Create: `src/com/mathquiz/service/QuestionSource.java`, `test/com/mathquiz/service/QuestionSourceTest.java`, `test/com/mathquiz/service/SessionRepositoryTest.java`, `test/com/mathquiz/service/DbPeek.java` (test-only `main` for the manual check)
- Modify:
  - `src/com/mathquiz/model/QuestionResult.java`, `src/com/mathquiz/service/SessionRepository.java`, `src/com/mathquiz/view/GamePanel.java`, `test/com/mathquiz/testutil/Results.java`
  - `docs/PRD.md`: FR-HNT-01 "Hint presses are recorded per question (not shown in the UI yet)"; FR-ADP-04 "each saved question also stores the difficulty it was generated at".
  - `docs/architecture.md`: §6.5 (question source is now `QuestionSource`) and §7.2 (`loadRaw` question keys).

**Interfaces:**
- Consumes: `Question.check`, `Question.getSkill` (Task 4); `SessionRepository(String, Supplier<String>)` and `getSchemaVersion()` (Task 9).
- Produces:

```java
public QuestionResult(String expression, int correctAnswer, int userAnswer, boolean correct,
                      long timeSpentMs, Skill skill, String difficulty, int hintsUsed);
// null skill/difficulty → NullPointerException; hintsUsed < 0 → IllegalArgumentException
public Skill getSkill(); public String getDifficulty(); public int getHintsUsed();

public final class QuestionSource {
    public QuestionSource(QuestionGenerator generator);
    public record Prepared(Question question, String difficulty) {}
    /** custom list (by current index) → "Daily Challenge" = generateSeeded(difficulty, "Mixed", todaySeed, index)
     *  → generate(difficulty, category). difficulty = session.getDifficulty() at call time. */
    public Prepared next(QuizSession session, List<Question> customQuestions, long todaySeed);
}
// loadRaw() question maps gain: "skillId" (String or null), "difficulty" (String or null), "hintsUsed" (Integer; 0 for legacy rows)
// Results helper: correct/wrong now use Skill.ADD_2DIGIT_2TERMS, "Easy", 0 hints; add
//   Results.of(String expr, int answer, int given, Skill skill, String difficulty, int hints)
```

- **Repository behaviour.** When `getSchemaVersion() >= 2`, `save` writes `skill_id = encrypt(skill.id())`, `difficulty = encrypt(difficulty)` and `hints_used`, and `loadRaw` reads them (decrypting). When the version is below 2 (legacy mode), both use today's column lists and `loadRaw` fills `null, null, 0`.

- [ ] **Step 1: Write the failing tests**

```java
// QuestionSourceTest
@Test void dailyChallengeIsSeededMixed()                 // Review Focus 4
// next(new QuizSession(10,"Medium","Daily Challenge"), null, 20261108L).question().getExpression()
//   equals new QuestionGenerator().generateSeeded("Medium","Mixed",20261108L,0).getExpression()
@Test void difficultyChangeAppliesToTheNextQuestion()    // Review Focus 5
// session(10,"Easy","Addition"): next → difficulty "Easy", skill ADD_2DIGIT_2TERMS;
// session.setDifficulty("Hard"): next → difficulty "Hard", skill ADD_4DIGIT_4TERMS
@Test void customQuestionsComeFirst()
// custom = [new Question("7 * 8", 56), new Question("9 * 6", 54)], session(2,"Medium","Custom: Times")
// next → same instance as custom.get(0), difficulty "Medium", skill CUSTOM

// SessionRepositoryTest (@TempDir, profile "Guest")
@Test void savedQuestionsRoundTripSkillDifficultyAndHints()
// Results.of("12 + 30", 42, 42, ADD_2DIGIT_2TERMS, "Easy", 2) → loadRaw: skillId "add.2digit.2terms", difficulty "Easy", hintsUsed 2
@Test void customQuizSessionSavesWithCustomSkill()       // Review Focus 3
// session "Custom: Times" with Skill.CUSTOM → skillId "custom"; category "Custom: Times"
@Test void skillAndDifficultyAreNotStoredInPlainText()   // R-SEC-01
// raw skill_id != "add.2digit.2terms" and CryptoHelper.decrypt(raw) equals it; same for difficulty "Easy"
@Test void legacyRowsLoadWithNullSkillAndZeroHints()
// createV1 → open → loadRaw questions: skillId null, difficulty null, hintsUsed 0
@Test void legacyModeStillSavesAndLoads()                // Review Focus 2
// half-migrated fixture (as in Task 9) → save one session → loadRaw().size() == 2; new rows have skillId null
```

- [ ] **Step 2: Run `.\test.ps1`.** Expected: compile FAIL.
- [ ] **Step 3: Implement** `QuestionResult`, `QuestionSource`, the `Results` helper and the repository changes.
- [ ] **Step 4: Run `.\test.ps1`.** Expected: PASS.
- [ ] **Step 5: Wire `GamePanel`.** No visible change.
  - Add a field `questionSource = new QuestionSource(generator)`, plus `currentDifficulty` and `hintsUsedForCurrent`.
  - `loadNextQuestion` takes `question` and `difficulty` from `questionSource.next(activeSession, customQuestions, getTodaySeed())` and resets `hintsUsedForCurrent = 0`.
  - The hint listener runs `if (!awaitingNext) hintsUsedForCurrent++;` before showing the hint. F1 and Ctrl+H already go through `hintButton.doClick()`.
  - `handleSubmit` uses `currentQuestion.check(answerField.getText())`:
    - `EMPTY` → the existing "Type your answer first!" message.
    - `NOT_A_WHOLE_NUMBER` → the existing "Only whole numbers, please!" message.
    - Otherwise, record `new QuestionResult(expression, answer, check.value(), status == CORRECT, timeSpentMs, currentQuestion.getSkill(), currentDifficulty, hintsUsedForCurrent)`.
- [ ] **Step 6: Zero-warning compile** (R-TST-01 command). Expected: no output.
- [ ] **Step 7: Manual check on a throw-away home** (R-TST-07).
  1. Compile to `bin` as `run.bat` does, then run `java -Duser.home=%TEMP%\aa-manual -cp "bin;lib/*" com.mathquiz.QuizApp`.
  2. Start Easy Addition with 10 questions:
     - Q1: press 💡, F1 and Ctrl+H, then answer.
     - Q2: no hint.
     - Q3: answer, then press 💡 after submitting.
     - Answer Q1–Q5 correctly and accept "Level Up?". Finish.
  3. Run `java -Duser.home=%TEMP%\aa-manual -cp "build/test/classes;build/test/main;lib/*" com.mathquiz.service.DbPeek`. It prints the newest session's rows as `index skillId difficulty hintsUsed`.
  4. Expected: Q1 `add.2digit.2terms Easy 3`; Q2 and Q3 `… Easy 0`; Q6 onward `add.3digit.3terms Medium 0`. Empty and non-number input show the same messages as before.
- [ ] **Step 8: Commit:** `feat(game): record skill, per-question difficulty and hint presses`. Body: `FEAT-02 (storage; display still open), FEAT-11, R-ARC-03 (question precedence moved to QuestionSource).` plus the trailer.

---

### Task 11: Phase 0A verification and register update

**Files:**
- Modify:
  - `docs/task.md`: §3 rows with commit hashes. DEBT-01 note: generators and grading covered; targets 3–9 still open. DEBT-02 note: encoding done; `.gitattributes`/version still open. DEBT-11 → done. FEAT-02 → storage done, display open. §10 verification-log entry.
  - `docs/premium-roadmap.md`: Part C Phase 0 row "0A done (date)"; G0 note "Task interface deferred to the first non-numeric mechanic (Phase 3); Phase 0 extended `Question`".

- [ ] **Step 1: Run `.\test.ps1`.** Expected: PASS. Record the test count shown in the summary.
- [ ] **Step 2: Zero-warning compile** (R-TST-01). Expected: no output.
- [ ] **Step 3: Check the three data states (R-TST-05)**, each with a throw-away `user.home`:
  1. **First launch:** empty home; play one quiz; Analytics shows it.
  2. **Upgrade:** `git worktree add $env:TEMP\aa-main main`, build and run it there, and play 2 quizzes with it. Then start this branch's build on the same home. Analytics still shows both sessions plus new ones. `DbPeek` shows null skill for the old rows.
  3. **Empty profile:** create a new profile; empty states appear.
- [ ] **Step 4: Run the task.md §7 manual regression cases** for gameplay, hints, results/review, analytics, Daily Challenge and the Quiz Builder (custom quiz plays and saves).
- [ ] **Step 5: Update the docs** listed under Files.
- [ ] **Step 6: Commit:** `docs: record Phase 0A completion`. Body: `DEBT-01, DEBT-02, DEBT-11, FEAT-02, FEAT-11.` plus the trailer.
