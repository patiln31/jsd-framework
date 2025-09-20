#!/bin/bash
echo "========================================"
echo "Stopping Selenium Grid"
echo "========================================"
echo

echo "Stopping Docker Selenium Grid..."
docker-compose down

echo
echo "Grid stopped successfully!"