@echo off
cd /d "%~dp0"
set ALLURE_HOME=%CD%\.allure\allure-2.24.0
set PATH=%ALLURE_HOME%\bin;%PATH%

mvn clean test -DsuiteXmlFile=testng.xml -Dsurefire.suiteXmlFiles=

if %ERRORLEVEL% EQU 0 (
    if exist "allure-report" rmdir /s /q "allure-report"
    echo Starting Allure server...
    "%ALLURE_HOME%\bin\allure.bat" serve allure-results --port 0
) else (
    echo Tests failed. Check results and try again.
    pause
)