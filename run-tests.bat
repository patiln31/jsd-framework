@echo off
title JSD Framework - Test Execution
echo ========================================
echo JSD Test Automation Framework
echo ========================================
echo.

echo Running test suite with Allure reporting...
call mvn clean test -DsuiteXmlFile=testng.xml

echo.
echo Generating Allure report...
call allure generate allure-results -o allure-report --clean

echo.
echo Opening Allure report...
call allure open allure-report

pause