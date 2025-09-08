package net.therap.auth.server.entity;



import jakarta.persistence.*;
import lombok.*;
import net.therap.auth.server.enums.KeyStatus;
import net.therap.auth.server.entity.interfaces.Persistence;

/**
 * @author apurboturjo
 * @since 8/14/25
 */

@Entity
@Table(name = "auth_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthKey extends Persistence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_key_generator")
    @SequenceGenerator(name = "auth_key_generator", sequenceName = "public_key_seq", allocationSize = 5)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String kid;
    
    @Lob
    @Column(nullable = false)
    private String publicKey;
    
    @Lob
    @Column(nullable = false)
    private String privateKey;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyStatus status;
}