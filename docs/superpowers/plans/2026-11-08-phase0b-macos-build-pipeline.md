# Phase 0B — macOS Build Pipeline (Unsigned) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

> **Status: DEFERRED (owner decision, 2026-11-08).** Windows first. Restart this plan when the triggers in `docs/premium-roadmap.md` decision D2 are met. Re-check action versions and runner labels then.

**Goal:** Build and test the app for macOS (Apple silicon) automatically in the cloud, producing an unsigned `.dmg` for internal testing, without the owner needing an Apple device.

**Architecture:** GitHub Actions on the existing repository (`github.com/Abdurrehman510/MathQuizApp`) runs the Phase 0A test suite on Windows and macOS runners. A new `package-mac.sh` builds a jpackage app image and DMG on the macOS runner, and the DMG is uploaded as a build artifact. A one-off manual smoke test on a rented or borrowed Mac confirms the packaged app really works.

**Tech Stack:** GitHub Actions (`actions/checkout@v4`, `actions/setup-java@v4` with Temurin 21, `actions/upload-artifact@v4`), bash, `jpackage`/`jlink` (JDK 21), macOS `sips` and `iconutil`.

**Spec:**
- `docs/premium-roadmap.md` Part C, Track X note **X1**: macOS packaging, built in CI because the owner has no Apple device; do not advertise Mac support until tested on real hardware.
- `docs/task.md` **FEAT-06**.
- `docs/rules.md` §11 (R-BLD).

**Depends on:** Plan 0A Task 1 (`test.ps1` and the test harness). Run this plan after Plan 0A Task 1 is merged, or on top of that branch.

**Decisions this plan makes (owner may override):**
1. **Apple silicon only** for now (`macos-latest` runners are arm64). Intel Macs come later, only if customers ask.
2. **Unsigned DMG, internal testing only.** Signing and notarization wait for an Apple Developer Program membership (see "Follow-up" at the end). Unsigned builds are never given to customers.
3. **Bundle identifier `com.atelierarithmetic.desktop`.** It is permanent, like the MSI upgrade UUID (new rule R-BLD-10).
4. **Menu-bar name `Atelier`.** jpackage requires `--mac-package-name` to be shorter than 16 characters.
5. **CI cost control.** The test matrix runs on pull requests, pushes to `main` and manual runs. The DMG job runs only on pushes to `main` and manual runs, because macOS minutes use up GitHub's free allowance faster.

## Global Constraints

- The owner has **no Apple device**. Every Mac step must run in CI or on a rented or borrowed Mac. Nothing may require a local Mac.
- The no-network rule R-PRD-01 applies to the app, not to CI. Workflow actions are build tooling, not app dependencies (R-ARC-09 unaffected).
- Compile with `-encoding UTF-8 --release 17` (R-BLD-01, R-COD-03).
- jpackage module list identical to `package.ps1`: `java.base,java.desktop,java.sql,java.xml,java.naming,jdk.charsets` (R-BLD-09).
- App version `1.0.0`, matching `package.ps1`. A single version source is still open (REL-01); do not invent a second scheme.
- Generated outputs (`build/`, `dist/`) are never committed (R-BLD-04). No secrets in the repository (R-SEC-03).
- Branch `feat/phase0b-macos-ci`. Conventional Commits with task IDs and `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>` (R-GIT-01..04).

## Review Focus

1. **Windows line endings in `package-mac.sh`.** bash fails with `$'\r': command not found`. Test: Task 2 adds `*.sh text eol=lf` to `.gitattributes`, and the CI job runs the script from a fresh checkout.
2. **The SQLite native library inside the packaged app.** If it fails to load, history silently stops saving. Tests: Task 1 runs the repository tests on macOS; Task 3 checks that `~/.atelier-arithmetic/atelier_arithmetic.db` is created by the installed app.
3. **Emoji and font rendering on macOS.** The UI asks for "Segoe UI Emoji", which macOS does not have. Test: Task 3 checklist item; any failure becomes a `BUG-` task.
4. **Gatekeeper blocks unsigned apps.** Testers need exact "Open Anyway" steps. Test: Task 3 follows and records them; Task 2 documents them in `DEPLOYMENT.md`.
5. **Missing JDK modules in the trimmed runtime** (for example, `java.sql` stripped). Test: Task 2 checks the `MODULES` line of the bundled runtime's `release` file.

---

### Task 1: CI runs the test suite on Windows and macOS

**Files:**
- Create: `.github/workflows/ci.yml`
- Modify: `docs/architecture.md` §2 (add a "CI" row), `DEPLOYMENT.md` (new "Continuous integration" section), `docs/task.md` (FEAT-06 progress note)

**Interfaces:**
- Consumes: `test.ps1` (Plan 0A Task 1).
- Produces: workflow `ci` with job `test` (matrix `windows-latest`, `macos-latest`). Task 2 adds a job that `needs: test`.

- [ ] **Step 1: Check whether the repository is public or private** on GitHub (Settings → General). If private, check the Actions minutes on the owner's plan (Settings → Billing), because macOS minutes count at a higher rate. Record the result in the PR description.
- [ ] **Step 2: Write `.github/workflows/ci.yml`**
  - Triggers: `pull_request`, `push` to `main`, `workflow_dispatch`.
  - Job `test`, matrix `os: [windows-latest, macos-latest]`, `runs-on: ${{ matrix.os }}`.
  - Steps: `actions/checkout@v4`; `actions/setup-java@v4` with `distribution: temurin` and `java-version: '21'`; then run `./test.ps1` with `shell: pwsh`.
- [ ] **Step 3: Push the branch and open a pull request.** Expected: both matrix legs green, with the same test count on both. If macOS fails, fix the cause in the code or `test.ps1`. Do not skip tests.
- [ ] **Step 4: Prove CI can fail.** Push a temporary commit that breaks one assertion (for example, expect `"A+"` for `19|20` in `QuizSessionGradeTest`). Expected: both legs red. Revert, push, and expect green.
- [ ] **Step 5: Update the docs** listed under Files.
- [ ] **Step 6: Commit:** `ci: run the test suite on Windows and macOS`. Body: `FEAT-06 (X1 step 1).` plus the trailer.

---

### Task 2: `package-mac.sh` builds an unsigned DMG in CI

**Files:**
- Create: `package-mac.sh`, `.gitattributes` (only the line `*.sh text eol=lf`; do not add `* text=auto` here, to avoid renormalising the repository)
- Modify:
  - `.github/workflows/ci.yml`: add job `mac-dmg`.
  - `DEPLOYMENT.md`: macOS build section and tester "Open Anyway" steps.
  - `docs/rules.md`: add **R-BLD-10** "The macOS bundle identifier `com.atelierarithmetic.desktop` MUST never change" (Now: ✔).
  - `docs/task.md`: FEAT-06 progress.

**Interfaces:**
- Produces: `bash ./package-mac.sh`, which leaves `dist/mac/AtelierArithmetic.app` and exactly one `dist/mac/*.dmg`. Workflow artifact `AtelierArithmetic-macos-arm64-unsigned`.

- [ ] **Step 1: Write `package-mac.sh`.** Begin with `#!/usr/bin/env bash`, then `set -euo pipefail`, then `cd "$(dirname "$0")"`. Then, in order:
  1. `rm -rf build/mac dist/mac`.
  2. `javac -encoding UTF-8 --release 17 -cp "lib/*" -sourcepath src src/com/mathquiz/QuizApp.java -d build/mac/classes`, then copy `src/com/mathquiz/resources/logo.png` to `build/mac/classes/com/mathquiz/resources/`.
  3. `jar --create --file build/mac/input/atelier-arithmetic.jar --main-class com.mathquiz.QuizApp -C build/mac/classes .`, then copy `lib/sqlite-jdbc-3.42.0.0.jar` into `build/mac/input/`.
  4. **Icon.** (Updated 2026-11-16: `logo.png` no longer exists; the brand set is in `brand/`, see FEAT-12.) Export `brand/app-icon.svg` to PNG at 16, 32, 64, 128, 256, 512 and 1024 px (the repo already holds 16–256 px in `src/com/mathquiz/resources/icons/`; render the larger ones from the SVG, e.g. with headless Chrome or `rsvg-convert` on the macOS runner). Fill `build/mac/AtelierArithmetic.iconset` with `icon_{16,32,128,256,512}x{same}.png` and their `@2x` variants, then finish with `iconutil -c icns build/mac/AtelierArithmetic.iconset -o build/mac/logo.icns`.
  5. Build the app image:
     `jpackage --type app-image --name AtelierArithmetic --input build/mac/input --main-jar atelier-arithmetic.jar --main-class com.mathquiz.QuizApp --add-modules java.base,java.desktop,java.sql,java.xml,java.naming,jdk.charsets --jlink-options '--strip-debug --no-man-pages --no-header-files --compress zip-9' --icon build/mac/logo.icns --app-version 1.0.0 --vendor "Atelier Arithmetic" --mac-package-identifier com.atelierarithmetic.desktop --mac-package-name Atelier --dest dist/mac`
  6. Module check: `grep -q 'java.sql' dist/mac/AtelierArithmetic.app/Contents/runtime/Contents/Home/release`. On failure, print a message naming R-BLD-09 and exit 1.
  7. Build the DMG: `jpackage --type dmg --app-image dist/mac/AtelierArithmetic.app --name AtelierArithmetic --app-version 1.0.0 --vendor "Atelier Arithmetic" --dest dist/mac`.
- [ ] **Step 2: Add the `mac-dmg` job.**
  - `needs: test`, `runs-on: macos-latest`.
  - Condition: `if: github.event_name == 'workflow_dispatch' || (github.event_name == 'push' && github.ref == 'refs/heads/main')`.
  - Steps: checkout; setup-java (Temurin 21); `bash ./package-mac.sh`; `actions/upload-artifact@v4` with name `AtelierArithmetic-macos-arm64-unsigned`, path `dist/mac/*.dmg` and `retention-days: 14`.
- [ ] **Step 3: Run the workflow manually** on the branch (Actions → ci → Run workflow). Expected: `test` is green; `mac-dmg` is green; the log shows the module check passing; the run's artifact list contains one DMG larger than 10 MB.
- [ ] **Step 4: Update the docs** listed under Files.
- [ ] **Step 5: Commit:** `build(mac): add package-mac.sh and unsigned DMG job`. Body: `FEAT-06 (X1 step 2). New rule R-BLD-10.` plus the trailer.

---

### Task 3: Smoke test on a real Mac (manual)

**Files:**
- Modify: `docs/task.md` (§10 verification log entry and FEAT-06 status; one new `BUG-` task per failed item), `docs/premium-roadmap.md` (X1 note: "unsigned build verified on <date>, macOS <version>")

**Interfaces:**
- Consumes: the DMG artifact from Task 2.

- [ ] **Step 1: Get access to an Apple silicon Mac** for about an hour: a cloud Mac rented by the hour, or a tester's Mac. Record the macOS version.
- [ ] **Step 2: Install.** Download the artifact, open the DMG, and drag the app to Applications.
- [ ] **Step 3: First launch.**
  - Expected: macOS blocks the unsigned app.
  - Follow System Settings → Privacy & Security → "Open Anyway". On older macOS: right-click the app → Open.
  - Record the exact steps seen, and update `DEPLOYMENT.md` if they differ.
- [ ] **Step 4: Run the checklist** and record pass or fail for each item:
  - The app opens and the guided tour starts.
  - An Easy Addition quiz plays end to end. Results and Review show it.
  - Analytics opens after the parental gate.
  - `ls ~/.atelier-arithmetic` shows `atelier_arithmetic.db` (SQLite native loaded).
  - Quit and reopen: history is still there.
  - Emoji in buttons, the hint box and results render as emoji, not boxes.
  - Dark mode works, and 150% scale works without clipping.
  - Sound plays, and mute works.
- [ ] **Step 5: File follow-ups.** Each failed item becomes a `BUG-` task in `docs/task.md` (R-DOC-05). Update the docs listed under Files.
- [ ] **Step 6: Commit:** `docs: record macOS smoke test`. Body: `FEAT-06 (X1 step 3).` plus the trailer.

---

## Follow-up (not in this plan): signing and notarization

These steps are blocked until the owner joins the Apple Developer Program, which has a paid yearly membership. Enrolment may need an Apple device or app for identity verification, so check before planning dates. When unblocked:
1. Create a Developer ID Application certificate.
2. Store the certificate and notarization credentials only as GitHub encrypted secrets (R-SEC-03).
3. Add `--mac-sign` options to the app-image step.
4. Submit with `xcrun notarytool submit --wait`, then run `xcrun stapler staple`.
5. Re-run the Task 3 checklist on the signed build.

Only signed and notarized builds may reach customers.
