package net.therap.auth.lib.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author apurboturjo
* @since 8/14/25
*/
@FeignClient(
        name = "publicKeyClient",
        url = "https://app-rnd01.therapdev.net"
)
public interface PublicKeyClient {
    
    @GetMapping("/keys/pk")
    ResponseEntity<String> getPublicKey(@RequestParam("kid") String kid);
}