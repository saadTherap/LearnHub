package net.therap.app.model.enums;

import lombok.Getter;

/**
 * @author gazizafor
 * @since 14/8/25
 */
@Getter
public enum AuthorizationLevel {
    
    ADMIN("ADMIN"),
    
    INSTRUCTOR("INSTRUCTOR"),
    
    STUDENT("STUDENT"),
    
    OWNER("INSTRUCTOR"),
    
    PUBLIC("PUBLIC"),
    
    STUDENT_ENROLLED("STUDENT");
    
    private final String role;
    
    AuthorizationLevel(String role) {
        this.role = role;
    }
    
    public boolean hasRole(String userRole) {
        return this.getRole().equals(userRole);
    }
}