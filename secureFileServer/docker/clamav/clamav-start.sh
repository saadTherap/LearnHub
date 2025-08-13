#!/bin/bash

CONTAINER_NAME=clamav
IMAGE=mkodockx/docker-clamav:alpine
PORT=3310
LOG_DIR=/var/log/secure-file-storage
LOG_FILE=$LOG_DIR/clamav.log

if docker ps --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo "[INFO] ClamAV container '$CONTAINER_NAME' is already running."
    exit 0
fi

echo "[INFO] Starting ClamAV container..."
docker run -d \
  --name $CONTAINER_NAME \
  -p $PORT:3310 \
  --restart=unless-stopped \
  $IMAGE

echo "[INFO] Waiting for ClamAV (clamd) to become ready on port $PORT..."
until nc -z localhost $PORT; do
  echo "[WAIT] ClamAV not ready yet..."
  sleep 2
done

echo "[INFO] ClamAV is now running on port $PORT"

echo "[INFO] Tailing logs to $LOG_FILE"
docker logs -f $CONTAINER_NAME | tee -a "$LOG_FILE"
