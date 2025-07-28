package net.therap.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class ServiceUtil {
    
    public static UserRole toSystemFormatUserRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
            
        } catch (IllegalArgumentException ex) {
            log.error("Invalid user role string: {}", role);
            
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("err.user.role.invalid"));
        }
    }
}