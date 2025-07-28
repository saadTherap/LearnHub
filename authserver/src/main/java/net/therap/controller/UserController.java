package net.therap.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import net.therap.entity.User;
import net.therap.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final CustomUserDetailsService customUserDetailsService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(customUserDetailsService.findAll());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(customUserDetailsService.findById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.ok(customUserDetailsService.saveUser(user));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(customUserDetailsService.updateUser(user));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customUserDetailsService.deleteById(id);
        
        return ResponseEntity.ok().build();
    }
}
