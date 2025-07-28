package net.therap.service;

import lombok.RequiredArgsConstructor;
import net.therap.entity.User;
import net.therap.exception.UserExistenceException;
import net.therap.exception.UserPersistenceException;
import net.therap.respository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(getMessage("err.user.not.found"));
        }
        
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExistenceException(getMessage("err.user.not.found")));
    }
    
    public User findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new UserExistenceException(getMessage("err.user.not.found")));
    }
    
    public User saveUser(User user) {
        if (userExistsByEmail(user.getEmail())) {
            throw new UserExistenceException(getMessage("err.user.exists"));
        }
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        if (!userExistsById(user.getId())) {
            throw new UserPersistenceException(getMessage("err.user.update.missing_id"));
        }
        if (userExistsByEmail(user.getEmail())) {
            throw new UserExistenceException(getMessage("err.user.exists"));
        }
        return userRepository.save(user);
    }
    
    public void deleteById(Long id) {
        if (!userExistsById(id)) {
            throw new UserPersistenceException(getMessage("err.user.delete.missing_id"));
        }
        userRepository.deleteById(id);
    }
    
    public boolean userExistsById(Long userId) {
        return Objects.nonNull(userId) && userRepository.existsById(userId);
    }
    
    public boolean userExistsByEmail(String email) {
        return Objects.nonNull(email) && userRepository.existsByEmail(email);
    }
    
    private String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}