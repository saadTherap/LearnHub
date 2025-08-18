package net.therap.auth.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.therap.auth.server.dto.JwtResponse;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    
    @PutMapping("/update-user")
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }
    
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/logout-force")
    public ResponseEntity<JwtResponse> forceLogout() {
//        To Do
        
        return null;
    }
}
