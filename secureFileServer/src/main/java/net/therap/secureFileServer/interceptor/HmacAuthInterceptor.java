package net.therap.secureFileServer.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class HmacAuthInterceptor implements HandlerInterceptor {

    private final ApiKeyProperties apiKeyProperties;
    private static final long ALLOWED_TIME_DRIFT_SECONDS = 300; // 5 minutes

    public HmacAuthInterceptor(ApiKeyProperties apiKeyProperties) {
        this.apiKeyProperties = apiKeyProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String apiKey = request.getHeader("X-Api-Key");
        String signature = request.getHeader("X-Signature");
        String timestampStr = request.getHeader("X-Timestamp");

        if (apiKey == null || signature == null || timestampStr == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authentication headers");

            return false;
        }

        String secret = apiKeyProperties.getClients().get(apiKey);

        if (secret == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");

            return false;
        }

        long timestamp;

        try {
            timestamp = Long.parseLong(timestampStr);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid timestamp");

            return false;
        }

        if (Math.abs(Instant.now().getEpochSecond() - timestamp) > ALLOWED_TIME_DRIFT_SECONDS) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Request expired");

            return false;
        }

        String message = request.getMethod() + request.getRequestURI() + timestampStr;
        String expectedSignature = HmacUtils.hmacSHA256(secret, message);

        if (!expectedSignature.equals(signature)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature");

            return false;
        }

        return true;
    }
}