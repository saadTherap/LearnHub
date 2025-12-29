package net.therap.server.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.therap.server.app.dto.JwtResponse;
import net.therap.server.app.entity.User;
import net.therap.server.app.service.UserService;
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

    @PostMapping("/certificate/claim")
    public ResponseEntity<Void> claimCertificate(@RequestParam Long studentId,
                                                 @RequestParam Long courseId,
                                                 HttpServletRequest request) {
        
        log.info("[CourseStudentController] Claim certificate request: studentId={}, courseId={}", studentId, courseId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);

        courseStudentService.issueCertificate(studentId, courseId);

        return ResponseEntity.ok().build();
    }
}
