@echo off
echo ==========================================================
echo    Starting Employee Leave Management System (Windows)
echo ==========================================================

REM Set Classpath
set CP=.;src;lib\*;ojdbc8.jar;ojdbc11.jar

echo [1/2] Compiling Java source files...
javac -cp "%CP%" src\*.java

if %ERRORLEVEL% EQU 0 (
    echo [2/2] Launching Application...
    java -cp "%CP%" Main
) else (
    echo [ERROR] Compilation failed.
    pause
)
