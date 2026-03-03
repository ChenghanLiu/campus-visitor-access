@echo off
setlocal

docker info >nul 2>&1
if %errorlevel% neq 0 (
  echo Docker is not running. Please start Docker Desktop and try again.
  pause
  exit /b
)

echo Starting containers (mysql + backend + frontend)...
docker compose up -d --build

echo.
echo Done.
echo Open: http://localhost:8083
pause