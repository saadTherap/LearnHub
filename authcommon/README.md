# Auth Common Library

Simple JWT authentication library for Spring Boot services.

## Setup

Add to your `build.gradle`:
```gradle
implementation project(":authcommon")
```

Update your main application class:
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.yourcompany.yourservice", "net.therap.auth.lib"})
@EnableFeignClients(basePackages = "net.therap.auth.lib.client")
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

Create filter configuration:
```java
@Configuration
public class AuthConfig {
    
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(TokenValidator tokenValidator) {
        List<String> excludedPaths = List.of(
            "/swagger-ui/", "/actuator/health", "/public/"
        );
        
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(tokenValidator, excludedPaths);
        
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        
        return registration;
    }
}
```

Add to your `application.properties`:
```yaml
auth.server.url: http://localhost:8091
```

## Usage

Access user info in your controllers:
```java
@GetMapping("/profile")
public ResponseEntity<String> getProfile(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    UserRequestCache.UserInfo userInfo = AuthDataUtil.getUserInfo(userId);
    
    if (userInfo != null) {
        String email = userInfo.email();
        String role = userInfo.role();
        // Use user data
    }
    
    return ResponseEntity.ok("Profile data");
}
```

That's it. The filter will automatically validate JWT tokens on all requests except the excluded paths.
