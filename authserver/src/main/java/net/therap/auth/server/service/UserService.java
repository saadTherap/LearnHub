package net.therap.auth.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.respository.UserRepository;
import net.therap.auth.server.util.MessageUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
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
            
            if (existingUser.isEnabled()) {
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
    }

    
    public boolean userExistsById(Long userId) {
        return Objects.nonNull(userId) && userRepository.existsById(userId);
    }
    
    public User toggleUserStatus(Long userId) {
        User user = findById(userId);
        user.setEnabled(!user.isEnabled());
        
        return userRepository.save(user);
    }
}