#!/bin/bash

echo "🚀 Starting Caching Demo Application..."
echo "📋 This will start Redis, Memcached, Hazelcast, MySQL, and Spring Boot app"
echo ""

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install it first."
    exit 1
fi

echo "✅ Docker is running"
echo "🔧 Building and starting all services..."
echo ""

# Start all services
docker-compose up --build

echo ""
echo "🎉 Application should be running!"
echo "📡 API Base URL: http://localhost:8080"
echo "📚 Swagger UI: http://localhost:8080/swagger-ui/index.html"
echo "❤️  Health Check: http://localhost:8080/actuator/health"
echo ""
echo "📬 Import CachingDemo.postman_collection.json into Postman for easy testing!"
