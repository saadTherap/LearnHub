package net.therap.entity;

import jakarta.persistence.*;
import lombok.*;
import net.therap.entity.interfaces.Persistence;
import net.therap.enums.UserRole;

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
    @SequenceGenerator(name = "users_generator", initialValue = 1, allocationSize = 5)
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", nullable = false, unique = true)
    private String password;
    
    @Column(name = "role", nullable = false)
    private UserRole role;
}