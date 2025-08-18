<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/entity/User.java
package net.therap.auth.server.entity;
========
package net.therap.server.app.entity;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/entity/User.java

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/entity/User.java
import net.therap.auth.server.entity.interfaces.Persistence;
import net.therap.auth.server.enums.UserRole;
========
import net.therap.server.app.entity.interfaces.Persistence;
import net.therap.server.app.enums.UserRole;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/entity/User.java

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends Persistence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @SequenceGenerator(name = "users_generator", sequenceName = "users_seq", initialValue = 1, allocationSize = 5)
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    
    @Override
    public String toString() {
        return email + " ," + role;
    }
}