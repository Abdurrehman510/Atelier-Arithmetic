@echo off
echo ==============================================
echo ATELIER ARITHMETIC - COMPILING AND LAUNCHING
echo ==============================================
mkdir bin 2>nul
javac -sourcepath src src\com\mathquiz\QuizApp.java -d bin
if %errorlevel% neq 0 (
    echo Compilation failed. Please ensure JDK is installed.
    pause
    exit /b %errorlevel%
)
xcopy /s /y /i src\com\mathquiz\resources bin\com\mathquiz\resources >nul 2>&1
echo Launching Desktop Application...
java -cp bin com.mathquiz.QuizApp
