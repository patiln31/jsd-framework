#!/bin/bash
echo "========================================"
echo "Starting Selenium Grid"
echo "========================================"
echo

echo "Starting Docker Selenium Grid with Chrome and Firefox nodes..."
docker-compose up -d

echo
echo "Grid started successfully!"
echo "Access Grid Console: http://localhost:4444/ui"
echo
echo "To stop the grid, run: ./stop-grid.sh"