package net.therap.secureFileServer.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.therap.secureFileServer.config.ApiKeyProperties;
import net.therap.secureFileServer.util.HmacUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

/**
 * @author avidewan
 * @since 8/11/25
 */
@Component
@Slf4j
public class HmacAuthInterceptor implements HandlerInterceptor {

    private final ApiKeyProperties apiKeyProperties;
    private static final long ALLOWED_TIME_DRIFT_SECONDS = 300;

    public HmacAuthInterceptor(ApiKeyProperties apiKeyProperties) {
        this.apiKeyProperties = apiKeyProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String apiKey = request.getHeader("X-Api-Key");
        String signature = request.getHeader("X-Signature");
        String timestampStr = request.getHeader("X-Timestamp");

        log.info("=== Incoming HMAC Auth Request ===");
        log.info("API Key header: {}", apiKey);
        log.info("Signature header: {}", signature);
        log.info("Timestamp header: {}", timestampStr);
        log.info("Method: {}", request.getMethod());
        log.info("Request URI: {}", request.getRequestURI());

        if (apiKey == null || signature == null || timestampStr == null) {
            log.warn("Missing authentication headers");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authentication headers");

            return false;
        }

        String secret = apiKeyProperties.getClients().get(apiKey);
        log.info("Resolved secret for API Key: {}", secret != null ? "[FOUND]" : "[NOT FOUND]");

        if (secret == null) {
            log.warn("Invalid API key: {}", apiKey);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");

            return false;
        }

        long timestamp;

        try {
            timestamp = Long.parseLong(timestampStr);

        } catch (NumberFormatException e) {
            log.warn("Invalid timestamp format: {}", timestampStr);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid timestamp");

            return false;
        }

        long timeDiff = Math.abs(Instant.now().getEpochSecond() - timestamp);
        log.info("Time drift (seconds): {}", timeDiff);

        if (timeDiff > ALLOWED_TIME_DRIFT_SECONDS) {
            log.warn("Request expired: drift {}s > allowed {}s", timeDiff, ALLOWED_TIME_DRIFT_SECONDS);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Request expired");

            return false;
        }

        String message = request.getMethod() + request.getRequestURI() + timestampStr;
        log.info("Message for HMAC: [{}]", message);

        String expectedSignature = HmacUtils.hmacSHA256(secret, message);
        log.info("Expected Signature: {}", expectedSignature);

        if (!expectedSignature.equals(signature)) {
            log.warn("Invalid signature! Expected [{}], got [{}]", expectedSignature, signature);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature");

            return false;
        }

        log.info("HMAC validation successful.");

        return true;
    }
}