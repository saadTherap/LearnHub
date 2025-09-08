package net.therap.auth.provider.record;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static net.therap.auth.provider.util.Constants.KEY_STATUS;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
@Entity
@Table(name = "auth_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyRecord {
    
    @Id
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String kid;
    
    @Lob
    @Column(nullable = false)
    private String publicKey;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    public boolean isActive() {
        return KEY_STATUS.equalsIgnoreCase(status);
    }
}