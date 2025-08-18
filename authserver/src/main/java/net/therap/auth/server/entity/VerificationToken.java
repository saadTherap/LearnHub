<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/entity/VerificationToken.java
package net.therap.auth.server.entity;
========
package net.therap.server.app.entity;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/entity/VerificationToken.java

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author apurboturjo
 * @since 7/29/25
 */
@Entity
@Table(name = "verification_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    
    private static final int EXPIRATION_MINUTES = 15;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}