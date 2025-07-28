package net.therap.util;

import net.therap.enums.UserRole;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
public class ServiceUtils {
    
    public static UserRole toSystemFormatUserRole(String role) {
        return UserRole.valueOf(role.toUpperCase());
    }
}