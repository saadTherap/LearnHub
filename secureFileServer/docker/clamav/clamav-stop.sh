#!/bin/bash

CONTAINER_NAME=clamav

if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo "[INFO] Stopping ClamAV container..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
    echo "[INFO] ClamAV container stopped and removed."

else
    echo "[INFO] No ClamAV container found."
fi
