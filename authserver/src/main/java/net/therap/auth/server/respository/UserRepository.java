<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/respository/UserRepository.java
package net.therap.auth.server.respository;

import net.therap.auth.server.entity.User;
========
package net.therap.server.app.respository;

import net.therap.server.app.entity.User;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/respository/UserRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
    
    boolean existsByEmail(String email);
}