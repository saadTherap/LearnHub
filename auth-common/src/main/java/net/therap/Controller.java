package net.therap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final EmailService emailService;

    public Controller(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-mail")
    public ResponseEntity<String> mailController(@RequestParam("email") String email) {
        emailService.sendVerificationEmail(email, "Hello from Apurbo!");

        return ResponseEntity.ok("Done - VERIFIED!!");
    }
}
