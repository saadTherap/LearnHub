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
public class UserService {
    
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
        if (user.hasId()) {
            throw new UserPersistenceException(SAVE_USER_ERROR);
        }
        
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        if (exists(user)) {
            throw new UserPersistenceException(UPDATE_USER_ERROR);
        }
        
        return userRepository.save(user);
    }
    
    public void deleteById(Long id) {
        if (exists(id)) {
            throw new UserPersistenceException(DELETE_USER_ERROR);
        }
        
        userRepository.deleteById(id);
    }
    
    private Boolean exists(User user) {
        return exists(user.getId());
    }
    
    private Boolean exists(Long id) {
        return Objects.nonNull(id) && userRepository.existsById(id);
    }
}