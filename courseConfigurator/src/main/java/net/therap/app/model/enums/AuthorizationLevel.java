package net.therap.app.model.enums;

import lombok.Getter;

/**
 * @author gazizafor
 * @since 14/8/25
 */
@Getter
public enum AuthorizationLevel {
    
    ADMIN("ADMIN", 5),
    
    OWNER("INSTRUCTOR", 4),
    
    INSTRUCTOR("INSTRUCTOR", 3),
    
    STUDENT_ENROLLED("STUDENT", 2),
    
    STUDENT("STUDENT", 1),
    
    PUBLIC("PUBLIC", 0);
    
    private final String role;
    private final int level;
    
    AuthorizationLevel(String role, int level) {
        this.role = role;
        this.level = level;
    }
    
    public boolean hasRole(String userRole) {
        AuthorizationLevel userLevel = getByRole(userRole);
        return userLevel != null && userLevel.getLevel() >= this.level;
    }
    
    public static AuthorizationLevel getByRole(String role) {
        for (AuthorizationLevel level : values()) {
            if (level.getRole().equals(role)) {
                return level;
            }
        }
        return null;
    }
}