@echo off
REM ============================================================
REM  Push project changes to GitHub
REM  Asks for a commit message, then add -> commit -> push
REM ============================================================

cd /d "%~dp0"

echo.
set /p msg=Enter commit message:

if "%msg%"=="" (
    echo No commit message entered. Aborting.
    pause
    exit /b 1
)

echo.
echo Staging changes...
git add .

echo Committing...
git commit -m "%msg%"

echo Pushing to GitHub...
git push origin main

echo.
echo Done!
pause
