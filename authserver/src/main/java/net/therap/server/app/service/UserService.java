package net.therap.server.app.service;

import lombok.RequiredArgsConstructor;
import net.therap.server.app.entity.User;
import net.therap.server.app.exception.UserExistenceException;
import net.therap.server.app.exception.UserPersistenceException;
import net.therap.server.app.respository.UserRepository;
import net.therap.server.app.util.MessageUtil;
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

    private final MessageUtil messageUtil;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExistenceException(messageUtil.getMessage("err.user.not.found")));
    }

    public User findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new UserExistenceException(messageUtil.getMessage("err.user.not.found")));
    }
    
    public User saveUser(User user) {
        Optional<User> existingUserOptional = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            
            if (existingUser.isEnabled()) {
                throw new UserExistenceException(messageUtil.getMessage("err.user.exists"));
                
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
            throw new UserPersistenceException(messageUtil.getMessage("err.user.update.missing_id"));
        }

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        if (!userExistsById(id)) {
            throw new UserPersistenceException(messageUtil.getMessage("err.user.delete.missing_id"));
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