#!/bin/bash

# Build the project first.
gradle shadowJar || { echo "Gradle build failed!"; exit 1; }

# Create the logs directory if it doesn't exist.
mkdir -p logs

start_server() {
    local port=$1
    echo "Starting Hazelcast server on port $port..."

    nohup java -jar build/libs/hazelcast-server-1.0.0-all.jar "$port" > "logs/hazelcast-server-$port.log" 2>&1 &
    # '$!' gets the PID of the last background process.
    echo $!
}

# Start three instances and store their PIDs.
echo "Starting all Hazelcast server instances..."
PID1=$(start_server 5701)
sleep 2 # Add a short delay to ensure the first instance has time to start.

PID2=$(start_server 5702)
sleep 2

PID3=$(start_server 5703)

echo "All Hazelcast server instances started!"
echo "PIDs: $PID1 $PID2 $PID3"