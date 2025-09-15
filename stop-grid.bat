@echo off
cd /d "%~dp0"

echo Stopping Selenium Grid...
docker-compose down

echo Grid stopped successfully!