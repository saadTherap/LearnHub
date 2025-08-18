package net.therap.auth.provider.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author apurboturjo
 * @since 8/14/25
 */

@Entity
@Table(name = "public_keys")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyEntity extends Persistence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String kid;
    
    @Lob
    @Column(nullable = false)
    private String publicKey;
}