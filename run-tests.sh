#!/bin/bash
echo "========================================"
echo "JSD Test Automation Framework"
echo "========================================"
echo

echo "Running test suite with Allure reporting..."
mvn clean test -DsuiteXmlFile=testng.xml

echo
echo "Generating Allure report..."
allure generate allure-results -o allure-report --clean

echo
echo "Opening Allure report..."
allure open allure-report