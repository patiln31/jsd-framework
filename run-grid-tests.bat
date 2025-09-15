@echo off
cd /d "%~dp0"

echo Starting Selenium Grid...
docker-compose up -d

echo Waiting for Grid to be ready...
timeout /t 10 /nobreak >nul

echo Running tests on Grid...
mvn clean test -DsuiteXmlFile=testng.xml -Dgrid.url=http://localhost:4444

echo Stopping Grid...
docker-compose down