#!/bin/bash

# Build the project first
./gradlew shadowJar || { echo "Gradle build failed!"; exit 1; }

mkdir -p logs

echo "Starting Hazelcast server on port 5701..."
java -jar build/libs/hazelcast-server-1.0.0-all.jar > logs/hazelcast-server-5701.log 2>&1 &
PID1=$!

sleep 2

echo "Starting Hazelcast server on port 5702..."
java -jar build/libs/hazelcast-server-1.0.0-all.jar > logs/hazelcast-server-5702.log 2>&1 &
PID2=$!

sleep 2

echo "Starting Hazelcast server on port 5703..."
java -jar build/libs/hazelcast-server-1.0.0-all.jar > logs/hazelcast-server-5703.log 2>&1 &
PID3=$!

echo "All Hazelcast server instances started!"
echo "PIDs: $PID1 $PID2 $PID3"

# Optional: wait for all to finish (CTRL+C to kill all)
wait $PID1 $PID2 $PID3
