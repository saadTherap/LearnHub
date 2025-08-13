package net.therap.secureFileServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import net.therap.secureFileServer.config.ApiKeyProperties;
import net.therap.secureFileServer.util.HmacUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * @author avidewan
 * @since 8/12/25
 */
@RestController
@RequestMapping("/dev/hmac")
@Tag(name = "Development", description = "HMAC signature generation for testing (remove in production)")
public class HmacDevController {

    private final ApiKeyProperties apiKeyProperties;

    public HmacDevController(ApiKeyProperties apiKeyProperties) {
        this.apiKeyProperties = apiKeyProperties;
    }

    @GetMapping("/generate")
    @Operation(summary = "Generate HMAC signature for testing")
    public ResponseEntity<HmacSignatureResponse> generateSignature(
            @RequestParam String clientKey,
            @RequestParam String method,
            @RequestParam String uri,
            @RequestParam(required = false) Long timestamp) {

        String secret = apiKeyProperties.getClients().get(clientKey);
        if (secret == null) {
            return ResponseEntity.badRequest().build();
        }

        if (timestamp == null) {
            timestamp = Instant.now().getEpochSecond();
        }

        String message = method.toUpperCase() + uri + timestamp;
        String signature = HmacUtils.hmacSHA256(secret, message);

        HmacSignatureResponse response = new HmacSignatureResponse();
        response.setClientKey(clientKey);
        response.setMethod(method.toUpperCase());
        response.setUri(uri);
        response.setTimestamp(timestamp);
        response.setSignature(signature);
        response.setMessage(message);

        return ResponseEntity.ok(response);
    }

    @Data
    public static class HmacSignatureResponse {
        private String clientKey;
        private String method;
        private String uri;
        private Long timestamp;
        private String signature;
        private String message;
    }
}
