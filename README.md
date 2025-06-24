# 🚀 Caching Demo: Redis vs Memcached vs Hazelcast

A comprehensive Spring Boot application demonstrating and comparing **Redis**, **Memcached**, and **Hazelcast** caching strategies for the Sun Life Financial technical session.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [API Endpoints](#api-endpoints)
- [Cache Comparison](#cache-comparison)
- [Demo Session Guide](#demo-session-guide)
- [Performance Metrics](#performance-metrics)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

This application demonstrates the behavior, performance, and trade-offs of three popular caching solutions in a real-world Java Spring Boot environment:

- **Redis**: High-performance in-memory data store with persistence
- **Memcached**: Simple, high-performance distributed memory caching system
- **Hazelcast**: Java-native distributed computing platform with embedded caching

### Key Features

✅ **Three Independent Cache Implementations**  
✅ **MySQL Database Backend** with 1-second artificial latency  
✅ **Performance Metrics Collection** with TTL tracking  
✅ **Colorful ANSI Logs** for visual debugging  
✅ **Docker Orchestration** for all services  
✅ **Persistence Configuration** for Redis and Hazelcast  
✅ **Postman Collection** for easy testing  

## 🏗 Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Postman/UI    │    │  Spring Boot    │    │     MySQL       │
│                 │◄──►│   Application   │◄──►│   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
         ┌──────▼──────┐ ┌──────▼──────┐ ┌─────▼──────┐
         │    Redis    │ │  Memcached  │ │ Hazelcast  │
         │(Persistent) │ │(In-Memory)  │ │(Embedded+  │
         │             │ │             │ │Persistent) │
         └─────────────┘ └─────────────┘ └────────────┘
```

## 🛠 Tech Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.2.0 |
| **Build Tool** | Gradle 8.x |
| **Language** | Java 17 |
| **Database** | MySQL 8.0 |
| **Cache 1** | Redis 7 (with AOF persistence) |
| **Cache 2** | Memcached 1.6 (non-persistent) |
| **Cache 3** | Hazelcast 5.3 (embedded + file persistence) |
| **Containerization** | Docker & Docker Compose |
| **Documentation** | Swagger/OpenAPI 3 |

## 🚀 Quick Start

### Prerequisites

- **Docker** and **Docker Compose** installed
- **Java 17** (for local development)
- **Postman** (optional, for testing)

### 1. Clone and Run

```bash
# Clone the repository
git clone <repository-url>
cd caching-demo

# Start all services all together
**Click on dockerRun directly, have created a pipeline for all the required jobs.**

# Wait for all services to be healthy (~2-3 minutes)
```

### 2. Verify Services

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Access Swagger UI
open http://localhost:8080/swagger-ui/index.html
```

### 3. Import Postman Collection

Import `CachingDemo.postman_collection.json` into Postman for easy testing.

## 📡 API Endpoints

### Data Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/data` | Store key-value data in MySQL |
| `GET` | `/api/v1/data/{key}` | Get data directly from database |
| `DELETE` | `/api/v1/data/{key}` | Delete data from database |

### Cache Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/cache/redis/{key}` | Get data using Redis cache |
| `GET` | `/api/v1/cache/memcached/{key}` | Get data using Memcached |
| `GET` | `/api/v1/cache/hazelcast/{key}` | Get data using Hazelcast |
| `DELETE` | `/api/v1/cache/redis/{key}` | Evict key from Redis |
| `DELETE` | `/api/v1/cache/memcached/{key}` | Evict key from Memcached |
| `DELETE` | `/api/v1/cache/hazelcast/{key}` | Evict key from Hazelcast |

### Metrics & Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/metrics` | Get all performance metrics |
| `GET` | `/api/v1/metrics/{cacheType}` | Get metrics by cache type |
| `DELETE` | `/api/v1/metrics` | Clear all metrics |
| `GET` | `/api/v1/cache/hazelcast/stats` | Get Hazelcast statistics |

## ⚖️ Cache Comparison

| Feature | Redis | Memcached | Hazelcast |
|---------|-------|-----------|-----------|
| **Persistence** | ✅ AOF/RDB | ❌ No | ✅ File-based |
| **Data Types** | Rich (String, Hash, List, Set) | Simple (String) | Rich (Map, Queue, Set) |
| **TTL Support** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Clustering** | ✅ Redis Cluster | ✅ Basic | ✅ Built-in |
| **Java Integration** | Good (via Lettuce) | Good (via Clients) | Excellent (Native) |
| **Memory Efficiency** | High | Very High | High |
| **Use Cases** | Web apps, Real-time analytics | Simple web caching | Distributed apps, Microservices |



```bash
# Step 1: Store some data
curl -X POST http://localhost:8080/api/v1/data \
  -H "Content-Type: application/json" \
  -d '{
    "key": "weather-toronto",
    "value": {
      "city": "Toronto",
      "temperature": 22,
      "condition": "Sunny"
    }
  }'

# Step 2: Test Redis cache (first call - cache miss)
curl http://localhost:8080/api/v1/cache/redis/weather-toronto

# Step 3: Test Redis cache (second call - cache hit)
curl http://localhost:8080/api/v1/cache/redis/weather-toronto

# Step 4: Compare with Memcached
curl http://localhost:8080/api/v1/cache/memcached/weather-toronto
curl http://localhost:8080/api/v1/cache/memcached/weather-toronto

# Step 5: Compare with Hazelcast
curl http://localhost:8080/api/v1/cache/hazelcast/weather-toronto
curl http://localhost:8080/api/v1/cache/hazelcast/weather-toronto

# Step 6: View metrics
curl http://localhost:8080/api/v1/metrics?limit=10
```


## 📊 Performance Metrics

The application automatically collects and stores metrics for every cache operation:

```json
{
  "cache_key": "weather-toronto",
  "cache_type": "REDIS",
  "source": "CACHE_HIT",
  "duration_ms": 4,
  "ttl_expires_at": "2025-06-24T15:30:00",
  "created_at": "2025-06-24T14:00:00"
}
```

### Log Output Examples

```bash
🟢 📦 [CACHE HIT - REDIS] Key: weather-toronto | Fetched in 4ms | TTL expires at: 2025-06-24 15:30:00
🟡 💾 [CACHE MISS - MEMCACHED] Key: weather-london
🔴 [DB FETCH] Key: weather-london | Took 1013ms | Cached till: 2025-06-24 15:31:00
```

## 🔧 Configuration

### Cache TTL Settings

All caches use a **30-minute TTL** by default. Modify in `application.yml`:

```yaml
cache:
  ttl-minutes: 30
```

### Persistence Configuration

- **Redis**: AOF enabled with `appendfsync everysec`
- **Hazelcast**: File-based persistence in `/tmp/hazelcast-data`
- **Memcached**: No persistence (by design)

### Database Latency Simulation

Artificial 1-second delay is added to all database calls to simulate network latency:

```java
// In DataService.java
Thread.sleep(1000); // Simulate 1 second DB latency
```

## 🐛 Troubleshooting

### Common Issues

**1. Services not starting**
```bash
# Use composeDown option in gradle folder, then clean and dockerRun.
```

**2. Database connection issues**
```bash
# Wait for MySQL to be ready
docker-compose up mysql
# Wait for "ready for connections" message
```

**3. Cache connection problems**
```bash
# Test Redis connection
docker exec -it caching-demo-redis redis-cli ping

# Test Memcached connection
docker exec -it caching-demo-memcached memcached -V
```

**4. Application won't start**
```bash
# Check Java version
java -version

# Rebuild the application
docker-compose build --no-cache app
```

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Individual service health
curl http://localhost:8080/actuator/health/redis
curl http://localhost:8080/actuator/health/db
```


## 📝 Additional Resources

- [Spring Boot Cache Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.caching)
- [Redis Documentation](https://redis.io/documentation)
- [Memcached Documentation](https://memcached.org/)
- [Hazelcast Documentation](https://docs.hazelcast.com/)

---

**Happy Caching! 🚀**

*Prepared by Samrat*
