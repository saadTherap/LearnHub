package net.therap.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.enums.UserRole;
import net.therap.exception.InvalidRoleSpecifiedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


/**
 * @author apurboturjo
 * @since 7/27/25
 */
@Slf4j
public class ServiceUtil {
    
    public static final String USER_ROLE_ERROR = "No such user role exists";
    
    public static UserRole toSystemFormatUserRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleSpecifiedException(USER_ROLE_ERROR + ": " + role);
        }
    }
}