@echo off
cd /d "%~dp0"

echo Starting Selenium Grid...
docker-compose up -d

echo Grid started successfully!
echo Grid UI: http://localhost:4444