#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

# Check for bundled JDK or system Java
if [ -f "$DIR/jdk/Contents/Home/bin/java" ]; then
    JAVA_BIN="$DIR/jdk/Contents/Home/bin/java"
    JAVAC_BIN="$DIR/jdk/Contents/Home/bin/javac"
elif command -v java &> /dev/null; then
    JAVA_BIN="java"
    JAVAC_BIN="javac"
else
    echo "[ERROR] Java not found. Please install JDK."
    exit 1
fi

CP=".:src:ojdbc8.jar"

echo "=========================================================="
echo "    Employee Leave Management System (Starting...)"
echo "=========================================================="

"$JAVAC_BIN" -cp "$CP" src/*.java
"$JAVA_BIN" -cp "$CP" Main
