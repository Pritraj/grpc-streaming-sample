#!/bin/bash

echo "🧹 Cleaning up existing containers..."
docker-compose down

echo "🚀 Rebuilding and starting services..."
docker-compose up --build --force-recreate
