package net.therap.server.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.therap.server.app.entity.interfaces.Persistence;
import net.therap.server.app.enums.UserRole;

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