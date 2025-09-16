@echo off
cd /d "%~dp0"

echo Starting Selenium Grid...
docker-compose up -d

echo Waiting for Grid to be ready...
timeout /t 10 /nobreak >nul

echo Running tests on Grid...
mvn clean test -DsuiteXmlFile=testng.xml -Dgrid.url=http://localhost:4444 -Dexecution.env=Docker-Grid

if %ERRORLEVEL% EQU 0 (
    echo Tests completed successfully!
) else (
    echo Tests failed!
)

echo Stopping Grid...
docker-compose down