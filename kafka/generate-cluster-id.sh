#!/bin/bash

CLUSTER_ID=$(head -c 16 /dev/urandom | base64 | tr -d '=' | tr '/+' '_-')

echo "Generated Base64 UUID: $CLUSTER_ID"
echo "CLUSTER_ID=$CLUSTER_ID" > /kafka/cluster-id.env
