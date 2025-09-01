package net.therap.auth.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.therap.auth.server.dto.JwtResponse;
import net.therap.auth.server.dto.UpdateUserRequest;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.service.UserService;
import net.therap.auth.server.util.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    
    private void checkAdmin(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (Objects.isNull(userId)) {
            throw new AuthServerException(MessageUtil.getMessage("err.admin.auth"));
        }
        
        User user = userService.findById(userId);
        if (!UserRole.ADMIN.equals(user.getRole())) {
            throw new AuthServerException(MessageUtil.getMessage("err.admin.access"));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getMe(HttpServletRequest request) {
        checkAdmin(request);
        
        return ResponseEntity.ok(userService.findById((Long) request.getAttribute("userId")));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        checkAdmin(request);
        
        return ResponseEntity.ok(userService.findAll());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId, HttpServletRequest request) {
        checkAdmin(request);
        
        return ResponseEntity.ok(userService.findById(userId));
    }
    
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        checkAdmin(request);
        
        userService.deleteById(id);
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/user/{userId}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long userId, HttpServletRequest request) {
        checkAdmin(request);
        
        return ResponseEntity.ok(userService.toggleUserStatus(userId));
    }
    
    @PostMapping("/logout-force")
    public ResponseEntity<JwtResponse> forceLogout(@RequestBody Map<String, Object> data,
                                                   HttpServletRequest request) {
        checkAdmin(request);
        
        Long userId = ((Number) data.get("userId")).longValue();
        
        userService.forceLogout(userId);
        
        return ResponseEntity.ok().build();
    }
}