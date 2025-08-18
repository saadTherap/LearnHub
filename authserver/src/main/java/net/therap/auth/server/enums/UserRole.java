<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/enums/UserRole.java
package net.therap.auth.server.enums;
========
package net.therap.server.app.enums;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/enums/UserRole.java

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
