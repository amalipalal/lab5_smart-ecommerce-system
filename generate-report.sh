#!/bin/bash
# Performance Report Generator Script
# Usage: ./generate-report.sh [log-file-path]
# Example: ./generate-report.sh logs/application.log

LOG_FILE="${1:-logs/application.log}"

echo "Generating Performance Report..."
echo ""

if [ ! -f "$LOG_FILE" ]; then
    echo "Error: Log file not found: $LOG_FILE"
    exit 1
fi

mvn -q compile exec:java \
    -Dexec.mainClass="com.example.ecommerce_system.util.PerformanceReportGenerator" \
    -Dexec.args="$LOG_FILE"

if [ $? -ne 0 ]; then
    echo ""
    echo "Error generating report. Make sure Maven is installed and the project is built."
    exit 1
fi
