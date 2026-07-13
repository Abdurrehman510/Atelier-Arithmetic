# Atelier Arithmetic — Windows Packaging & Deployment Guide

This document describes the native build, Windows installer packaging, and code signing pipeline for the **Atelier Arithmetic** desktop application.

---

## 📋 Prerequisites

To compile, build a custom minimal Java runtime, package the application, and generate a Windows MSI Installer, your machine must meet the following requirements:

1. **Java Development Kit (JDK 17 or 21)**:
   - Ensure a complete JDK is installed.
   - Set the `JAVA_HOME` environment variable pointing to your JDK installation directory (e.g., `C:\Program Files\Java\jdk-21`).
2. **WiX Toolset v3.11 or newer**:
   - Required by `jpackage` to compile the MSI installer.
   - Download the installer from the [WiX Toolset Website](https://wixtoolset.org/releases/v3.11.2/stable/) and run it.
   - **Important**: Add the WiX installation binary folder (e.g., `C:\Program Files (x86)\WiX Toolset v3.11\bin`) to your Windows `PATH` environment variable so `candle.exe` and `light.exe` are globally executable.
3. **Windows SDK (for SignTool)**:
   - Required if code signing is enabled.
   - Install the Windows SDK from the Microsoft Developer site to obtain `signtool.exe`.

---

## 🛠️ Automated Build & Packaging Pipeline

The application features a fully automated PowerShell script, [package.ps1](file:///d:/MathQuizApp/package.ps1), which automates the build, icon translation, runtime generation, and installer pipeline.

### Step-by-Step Build Instructions

Open a PowerShell terminal as Administrator, navigate to the project root directory, and execute:

```powershell
powershell -File package.ps1
```

The script performs the following phases automatically:
1. **Tool Verification**: Checks for local compiler elements and locates WiX (`candle.exe`/`light.exe`).
2. **Artifact Clean**: Removes any old `/build` or `/dist` output folders.
3. **Asset Conversion**: Compiles and runs [IcoConverter.java](file:///d:/MathQuizApp/src/com/mathquiz/util/IcoConverter.java) to dynamically translate the high-res 1024x1024 `logo.png` into a standard Microsoft `logo.ico` file.
4. **App Compilation**: Compiles all Java files, copies the logo resource, and bundles them into an executable JAR (`dist/atelier-arithmetic.jar`).
5. **Runtime Linking (`jlink`)**: Invokes the modular `jlink` utility to link a lightweight custom JRE (containing only `java.base, java.desktop, java.sql, java.xml, java.naming, jdk.charsets`), optimized with `--strip-debug --no-man-pages --no-header-files` and compressed using maximum `zip-9` compression. This reduces the JRE footprint from 150MB down to ~35MB.
6. **Native Executable Launcher (`jpackage`)**: Bundles the JRE and JARs into a native Windows launcher directory structure (`dist/AtelierArithmetic/`).
7. **MSI Installer (If WiX is Present)**: Compiles a professional native Windows installer `dist/AtelierArithmetic-1.0.0.msi` equipped with Start Menu and Desktop shortcuts, directory choose dialogs, automatic uninstall hooks, and a static upgrade UUID (`71a2e7c3-3882-4df4-94fa-7e9b422a59cc`) to cleanly overwrite old versions during upgrades.
8. **Portable ZIP Fallback (If WiX is Missing)**: If WiX is not installed, the script falls back gracefully to packaging the app-image into a portable archive `dist/AtelierArithmetic-v1.0.0-Portable.zip`.

---

## 🔑 Code Signing Pipeline

Code signing ensures Windows does not block the application with high-severity SmartScreen warnings. The pipeline is automated in [sign.ps1](file:///d:/MathQuizApp/sign.ps1).

### 1. EV USB Hardware Token Signing (Production Release)

Extended Validation (EV) certificates stored on hardware security tokens (like YubiKey or SafeNet eToken) are highly recommended. They provide immediate Microsoft SmartScreen reputation.

To sign using an EV Hardware Token:
1. Insert your USB token into the build machine.
2. Run the signing script, passing your smart card pin and cryptographic container name:

```powershell
powershell -File sign.ps1 -Pin "YourTokenPin" -ContainerName "YourKeyContainerName" -TargetFile "dist\AtelierArithmetic-1.0.0.msi"
```

The script will locate `signtool.exe` from the Windows SDK, sign the binary with SHA-256 using the Microsoft Base Smart Card Crypto Provider, apply a cryptographically secure DigiCert RFC 3161 timestamp, and perform a validation check via `signtool verify /pa`.

### 2. Software Developer Certificate Signing (Staging/Testing)

If you are using a standard software certificate stored in a `.pfx` file:

```powershell
powershell -File sign.ps1 -CertPath "C:\path\to\developer_cert.pfx" -CertPassword "YourPfxPassword" -TargetFile "dist\AtelierArithmetic-1.0.0.msi"
```

---

## 🖥️ Silent / Automated Installation

For deployment in educational classrooms or bulk workstation installations, the MSI installer supports fully silent background installation:

```cmd
msiexec /i AtelierArithmetic-1.0.0.msi /qn /norestart
```

* `/i` — Installs the product.
* `/qn` — Quiet mode, no user interface.
* `/norestart` — Prevents computer restart after setup.

---

## 📈 Windows SmartScreen Reputation Building

When you distribute a newly signed Windows application, users may see a blue "Windows Protected Your PC" popup. To prevent this:
1. **Use an EV Code Signing Certificate**: EV certificates build immediate SmartScreen reputation, meaning users will *never* see the warning popup.
2. **Submit to Microsoft Portal**: If you are using a standard Code Signing Certificate, submit your signed installer to the [Microsoft Security Intelligence Portal](https://www.microsoft.com/en-us/wdsi/filesubmission) for analysis. This accelerates reputation building and clears the warning within 24-48 hours.
