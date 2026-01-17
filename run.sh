#!/bin/bash

# Compile the project
echo "Compiling..."
cd src
javac *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Compilation successful!"
echo ""

# Check if web mode is requested
if [ "$1" == "web" ]; then
    echo "Starting web server on http://localhost:8080"
    echo "Press Ctrl+C to stop"
    java Main web
else
    echo "Starting REPL mode..."
    echo "Type SQL commands or 'exit' to quit"
    echo ""
    java Main
fi
