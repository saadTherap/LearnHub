# Documentation for Using Global Cache Library in Multi-Service Projects

## Overview

This document provides a guide to integrating the **Hazelcast Cache Library** into your multi-service Spring Boot application. The library offers a simple solution for caching using **Hazelcast**, including cache operations, cache invalidation, and soft deletion integration. The setup enables a global caching system, reducing the need for repeated code and simplifying cache management across multiple services.

## How to Integrate the Library in Your Service

### Step 1: Add the Library as a Dependency

1. In your `build.gradle` file, add the library module as a dependency under the `dependencies` block for any service that needs to access the caching functionality:

```gradle
dependencies {
    implementation project(':infra:cache-support')  // Add this in the relevant services
}
```

### Step 2: Configure `application.properties`

To configure Hazelcast caching in your service, define the necessary properties in the `application.properties` file.

```properties
# Enable Hazelcast cache configuration
cache.hazelcast.enabled=true

# Define the path to the Hazelcast client YAML configuration
cache.hazelcast.clientYaml=hazelcast-client.yaml

# If cache is optional (e.g., if you want to fail gracefully if cache is unavailable)
cache.hazelcast.optional=true
```

### Step 3: Use Cache Services in Your Service Classes

After setting up the library and configuring it, you can use the cache services in your service classes to cache data and invalidate cache entries after a transaction commit.

1. **Constructor Injection of `CacheInvalidationUtil`**

   In your service class, inject the `CacheInvalidationUtil` and use it for cache invalidation after saving or updating an entity.

#### Example: InstructorService

```java
public class InstructorService {

    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final InstructorRepository instructorRepository;
    private final MessageSource messageSource;
    
    // Constructor to inject necessary services
    public InstructorService(InstructorRepository instructorRepository, MessageSource messageSource, CacheInvalidationUtil cacheInvalidationUtil) {
        this.instructorRepository = instructorRepository;
        this.messageSource = messageSource;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
    }
}
```

2. **Constructor Injection of `HazelcastCacheService`**

   In your controller, inject `HazelcastCacheService` to read and write cached data.

#### Example: CourseController

```java
public class CourseController {

    private final CourseMapper courseMapper;
    private final CourseService courseService;
    private final DtoHelper dtoHelper;
    private final HazelcastCacheService hazelcastCacheService;

    // Constructor to inject services
    public CourseController(CourseMapper courseMapper, CourseService courseService, DtoHelper dtoHelper,
                            HazelcastCacheService hazelcastCacheService) {
        this.courseMapper = courseMapper;
        this.courseService = courseService;
        this.dtoHelper = dtoHelper;
        this.hazelcastCacheService = hazelcastCacheService;
    }
}
```

### Step 4: Cache Operations

You can perform cache operations such as **put**, **get**, **remove**, and **clear** using `HazelcastCacheService`.

#### Example:

```java
// Using HazelcastCacheService to put a value in cache
hazelcastCacheService.put("mapName", key, value);

// Using HazelcastCacheService to get a value from cache
String value = hazelcastCacheService.get("mapName", key);
```

### Step 5: Transactional Cache Invalidation

To ensure cache is invalidated only after the transaction successfully commits, use `CacheInvalidationUtil`:

```java
// Invalidate cache after transaction commit
cacheInvalidationUtil.invalidateCachesAfterCommit(id, CacheConstants.CACHE_NAME);
```

This will register the invalidation for the cache after the current transaction completes successfully.

---

## Sample Usage

For a **CourseService**, you can follow these steps:

1. **Inject `CacheInvalidationUtil`** into the service constructor.
2. **Invalidate caches** after any update or creation operation to ensure the cache is refreshed with the latest data.

### Example: CourseService

```java
public class CourseService {

    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final CourseRepository courseRepository;
    
    public CourseService(CourseRepository courseRepository, CacheInvalidationUtil cacheInvalidationUtil) {
        this.courseRepository = courseRepository;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
    }

    @Transactional
    public Course saveCourse(Course course) {
        Course savedCourse = courseRepository.save(course);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(savedCourse.getId()), CacheConstants.COURSES);
        return savedCourse;
    }
}
```

---

## Testing Your Code

Make sure you write unit tests to verify the caching behavior in your services.

1. **Mock `HazelcastInstance`** and **`HazelcastCacheService`** to test caching logic.
2. Use **Mockito** to verify that caches are correctly invalidated after a transaction commit.

---

## Conclusion

By following this guide, your services can easily integrate with a global Hazelcast cache library, reducing code duplication and improving cache management across your multi-service Spring Boot application.

For more information or advanced configuration, feel free to explore Hazelcast's [official documentation](https://hazelcast.com/docs/) or refer to Spring's [cache abstraction documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache).
