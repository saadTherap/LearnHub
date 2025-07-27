package net.therap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.therap.enums.UserRole;

import java.util.Objects;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Entity
@Table(name = "users")
@Data
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
    
    public Boolean hasId() {
        return Objects.nonNull(id);
    }
}