@echo off
setlocal

echo =====================================
echo Starting system...
echo =====================================

REM Check Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not running.
    echo Please start Docker Desktop first.
    pause
    exit /b
)

echo Docker is running.

REM One-click start (THIS IS THE KEY LINE)
docker compose up -d --build

echo.
echo =====================================
echo System is starting.
echo =====================================
echo.
echo Please wait 20-40 seconds.
echo Then open:
echo http://localhost:8083
echo.
pause