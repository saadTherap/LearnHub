package net.therap.auth.server.repository;

import net.therap.auth.server.config.TestJpaConfig;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.respository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author apurboturjo
 * @since 9/2/25
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setEmail("demo@gmail.com");
        user.setPassword("Demo@123");
        user.setRole(UserRole.STUDENT);
        userRepository.save(user);
        
        User foundUser = userRepository.findByEmail("demo@gmail.com");
        
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("demo@gmail.com");
    }
    
    @Test
    void findByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        User foundUser = userRepository.findByEmail("notfound@gmail.com");
        
        assertThat(foundUser).isNull();
    }
}