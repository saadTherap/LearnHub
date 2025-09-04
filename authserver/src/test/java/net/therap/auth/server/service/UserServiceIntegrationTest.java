package net.therap.auth.server.service;

import jakarta.transaction.Transactional;
import net.therap.auth.server.config.TestServiceConfig;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.UserRepository;
import net.therap.auth.server.util.MessageUtil;
import net.therap.cache.support.HazelcastCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author apurboturjo
 * @since 8/31/25
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestServiceConfig.class)
class UserServiceIntegrationTest {
    
    @MockitoBean
    private DeletionService deletionService;
    
    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void saveUser_ShouldPersistNewUser() {
        User user = new User();
        user.setEmail("test@demo.com");
        user.setPassword("Demo@123");
        user.setRole(UserRole.STUDENT);
        
        User saved = userService.saveUser(user);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isEnabled()).isFalse();
        assertThat(userRepository.findByEmail("test@demo.com")).isNotNull();
    }
    
    @Test
    void findByEmail_ShouldThrow_WhenUserNotFound() {
        assertThatThrownBy(() -> userService.findByEmail("unknown@x.com"))
                .isInstanceOf(AuthServerException.class)
                .hasMessageContaining("No user found");
    }
    
    @Test
    void deleteById_ShouldSoftDeleteAndSendMessage_WhenStudent() {
        User user = new User();
        user.setEmail("stud@demo.com");
        user.setPassword("Demo@123");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);
        
        userService.deleteById(user.getId());
        
        User deleted = userRepository.findById(user.getId()).get();
        assertThat(deleted.isDeleted()).isTrue();
        
        verify(deletionService, times(1))
                .sendStudentDeletionInfo("stud@demo.com");
    }
    
    @Test
    void toggleUserStatus_ShouldFlipEnabledFlag() {
        User user = new User();
        user.setEmail("flip@demo.com");
        user.setPassword("Demo@123");
        user.setRole(UserRole.INSTRUCTOR);
        user.setEnabled(false);
        user = userRepository.save(user);
        
        User updated = userService.toggleUserStatus(user.getId());
        
        assertThat(updated.isEnabled()).isTrue();
    }
    
    @Test
    void forceLogout_ShouldNotCallHazelcastRemovalIfNotForcedLoggedout() {
        User user = new User();
        user.setEmail("logout@demo.com");
        user.setPassword("Demo@123");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);
        
        User finalUser = user;
        assertThatThrownBy(() -> userService.forceLogout(finalUser.getId()))
                .isInstanceOf(AuthServerException.class)
                .hasMessageContaining(MessageUtil.getMessage("err.user.notLoggedIn"));
    }
}