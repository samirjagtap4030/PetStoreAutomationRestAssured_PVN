@echo off
REM Change to the folder where this batch file is located (project root)
cd /d "%~dp0"

REM Run the test suite
mvn clean compile test
