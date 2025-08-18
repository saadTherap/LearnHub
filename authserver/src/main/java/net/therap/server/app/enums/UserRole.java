package net.therap.server.app.enums;

import lombok.Getter;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Getter
public enum UserRole {
    
    STUDENT("Student"),
    
    INSTRUCTOR("Instructor"),
    
    ADMIN("Admin");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
