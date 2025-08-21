package net.therap.auth.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.UserRepository;
import net.therap.auth.server.util.MessageUtil;
import net.therap.cache.support.HazelcastCacheService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final DeletionService deletionService;
    private final HazelcastCacheService hazelcastCacheService;
    
    public List<User> findAll() {
        return userRepository.findAllSorted();
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthServerException(MessageUtil.getMessage("err.user.not.found")));
    }
    
    public User findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new AuthServerException(MessageUtil.getMessage("err.user.not.found")));
    }
    
    public User saveUser(User user) {
        Optional<User> existingUserOptional = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            
            if (existingUser.isDeleted()) {
                existingUser.setPassword(user.getPassword());
                existingUser.setRole(user.getRole());
                existingUser.setEnabled(false);
                existingUser.setDeleted(false);
                
                log.info("Reactivating deleted user account for email: {}", user.getEmail());
                return userRepository.save(existingUser);
                
            } else if (existingUser.isEnabled()) {
                throw new AuthServerException(MessageUtil.getMessage("err.user.exists"));
                
            } else {
                existingUser.setPassword(user.getPassword());
                existingUser.setRole(user.getRole());
                
                return userRepository.save(existingUser);
            }
            
        } else {
            return userRepository.save(user);
        }
    }
    
    public User updateUser(User user) {
        if (!userExistsById(user.getId())) {
            throw new AuthServerException(MessageUtil.getMessage("err.id.missing.update"));
        }
        
        return userRepository.save(user);
    }
    
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthServerException(
                        MessageUtil.getMessage("err.id.missing.delete"))
                );
        
        user.setDeleted(true);
        
        userRepository.save(user);
        log.info("USER with email: {}, deleted (soft) from authentication server.", user.getEmail());
        
        if (user.getRole() == UserRole.STUDENT) {
            log.info("Sending student deletion info for email: {}", user.getEmail());
            deletionService.sendStudentRegistrationInfo(user.getEmail());
            
        } else if (user.getRole() == UserRole.INSTRUCTOR) {
            log.info("Sending instructor registration info for email: {}", user.getEmail());
            deletionService.sendInstructorRegistrationInfo(user.getEmail());
        }
    }

    
    public boolean userExistsById(Long userId) {
        return Objects.nonNull(userId) && userRepository.existsById(userId);
    }
    
    public User toggleUserStatus(Long userId) {
        User user = findById(userId);
        user.setEnabled(!user.isEnabled());
        
        return userRepository.save(user);
    }
    
    public void forceLogout(Long userId) {
        User user = findById(userId);
        
        Long currentVer = hazelcastCacheService.get("userEpoch", user.getId());
        if (Objects.isNull(currentVer)) {
            currentVer = 1L;
        }
        
        hazelcastCacheService.put("userEpoch", user.getId(), currentVer + 1);
    }
}