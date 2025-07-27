package net.therap.service;

import lombok.RequiredArgsConstructor;
import net.therap.entity.User;
import net.therap.exception.UserExistenceException;
import net.therap.exception.UserPersistenceException;
import net.therap.respository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.therap.util.ErrorMessages.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
public class UserDetailsService {
    
    private final UserRepository userRepository;
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExistenceException(FIND_USER_ERROR));
    }
    
    public User findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new UserExistenceException(FIND_USER_ERROR));
    }
    
    public User saveUser(User user) {
        if (userExistsByEmail(user.getEmail())) {
            throw new UserExistenceException(EXIST_USER_ERROR);
        }
        
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        if (!userExistsById(user.getId())) {
            throw new UserPersistenceException(UPDATE_USER_ERROR);
        }
        
        if (userExistsByEmail(user.getEmail())) {
            throw new UserExistenceException(EXIST_USER_ERROR);
        }
        
        return userRepository.save(user);
    }
    
    public void deleteById(Long id) {
        if (!userExistsById(id)) {
            throw new UserPersistenceException(DELETE_USER_ERROR);
        }
        
        userRepository.deleteById(id);
    }
    
    public boolean userExistsById(Long userId) {
        return Objects.nonNull(userId) && userRepository.existsById(userId);
    }
    
    public boolean userExistsByEmail(String email) {
        return Objects.nonNull(email) && userRepository.existsByEmail(email);
    }
}