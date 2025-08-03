package net.therap.helper;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@FeignClient(name = "authClient", url = "https://server.local/")
public interface AuthClient {
    
    @PostMapping("/api/auth/refresh")
    ResponseEntity<String> refreshToken(@RequestHeader("Authorization") String expiredAccessToken);
}