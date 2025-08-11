package net.therap.auth.context;


import org.springframework.stereotype.Component;

/**
 * @author apurboturjo
 * @since 8/11/25
 */
@Component
public class RequestTokenContext {

    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    public void setToken(String token) {
        TOKEN_HOLDER.set(token);
    }

    public String getToken() {
        return TOKEN_HOLDER.get();
    }

    public void clear() {
        TOKEN_HOLDER.remove();
    }
}