@echo off
title JENKINS SERVER
echo ========================================
echo STARTING JENKINS SERVER
echo ========================================
echo.

echo Starting Jenkins on port 8080...
echo Jenkins will be available at: http://localhost:8080
echo.

java -jar jenkins.war --httpPort=8080

echo.
echo Jenkins server stopped.
pause