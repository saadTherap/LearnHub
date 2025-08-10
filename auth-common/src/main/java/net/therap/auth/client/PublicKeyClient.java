package net.therap.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Component
@FeignClient(name = "publicKeyClient", url = "http://127.0.0.1:8090/auth/pk")
public interface PublicKeyClient {
    
    @GetMapping()
    ResponseEntity<String> getPublicKey(@RequestParam("kid") String keyId);
}
