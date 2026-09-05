#!/bin/sh
cd "$(dirname "$0")"

JAR_FILE="build/libs/triangle-1.0.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Building project..."
    ./gradlew -q --console=plain jar
fi

if [ $# -ne 1 ]; then
    echo "Usage: $0 <triangle-file>"
    echo ""
    echo "Reads a triangle from the specified file and outputs a minimal path."
    exit 1
fi

INPUT_FILE="$1"
if [ ! -f "$INPUT_FILE" ]; then
    echo "Error: File '$INPUT_FILE' not found"
    exit 1
fi

cat "$INPUT_FILE" | java -jar "$JAR_FILE"
