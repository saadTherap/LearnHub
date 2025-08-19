package net.therap.auth.lib.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author apurboturjo
* @since 8/14/25
*/
@FeignClient(
        name = "publicKeyClient",
        url = "http://192.168.0.215:8091"
)
public interface PublicKeyClient {
    
    @GetMapping("/keys/pk")
    ResponseEntity<String> getPublicKey(@RequestParam("kid") String kid);
}