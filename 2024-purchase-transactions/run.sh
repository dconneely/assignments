#!/bin/sh
cd "$(dirname "$0")"

echo ""
echo "Starting application..."
echo "Open http://localhost:8080/ to view the application"
echo ""

./gradlew bootRun
