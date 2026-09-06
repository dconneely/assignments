#!/bin/sh
cd "$(dirname "$0")"

JAR_FILE="build/libs/scope-1.0.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Building project..."
    ../gradlew -q --console=plain jar
fi

if [ $# -eq 0 ]; then
    java -jar "$JAR_FILE"
else
    INPUT_FILE="$1"
    shift
    if [ ! -f "$INPUT_FILE" ]; then
        echo "Error: File '$INPUT_FILE' not found"
        exit 1
    fi
    cat "$INPUT_FILE" | java -jar "$JAR_FILE" "$@"
fi
