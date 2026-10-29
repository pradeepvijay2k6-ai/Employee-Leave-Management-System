#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

echo "=========================================================="
echo "    Employee Leave Management System (Starting...)"
echo "=========================================================="

# 1. Ensure Docker Desktop is running
if ! docker info >/dev/null 2>&1; then
    echo "[INFO] Starting Docker Desktop..."
    open -a Docker
    for i in {1..25}; do
        if docker info >/dev/null 2>&1; then
            echo "[INFO] Docker Desktop is ready."
            break
        fi
        sleep 2
    done
fi

# 2. Ensure Oracle Database container is running
if docker ps -a --format '{{.Names}}' | grep -q "^oracle-db$"; then
    if ! docker ps --format '{{.Names}}' | grep -q "^oracle-db$"; then
        echo "[INFO] Starting Oracle Database container (oracle-db)..."
        docker start oracle-db >/dev/null 2>&1
        echo "[INFO] Waiting for database service to initialize..."
        for i in {1..20}; do
            if docker logs oracle-db 2>&1 | grep -q "DATABASE IS READY TO USE"; then
                echo "[INFO] Oracle Database is ready to use!"
                break
            fi
            sleep 2
        done
    fi
fi

# 3. Locate Java runtime
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

echo "[INFO] Compiling Java classes..."
"$JAVAC_BIN" -cp "$CP" src/*.java

echo "[INFO] Launching Desktop GUI..."
"$JAVA_BIN" -cp "$CP" Main
