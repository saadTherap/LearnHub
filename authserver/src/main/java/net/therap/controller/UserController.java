package net.therap.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.therap.entity.User;
import net.therap.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final CustomUserDetailsService customUserDetailsService;
    
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return ResponseEntity.ok(customUserDetailsService.updateUser(user));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customUserDetailsService.deleteById(id);
        
        return ResponseEntity.ok().build();
    }
}
