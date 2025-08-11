package net.therap.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.therap.entity.User;
import net.therap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        
        return ResponseEntity.ok().build();
    }
}
