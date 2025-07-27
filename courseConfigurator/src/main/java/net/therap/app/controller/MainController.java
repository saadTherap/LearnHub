package net.therap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gazizafor
 * @since 20/7/25
 */
@RestController
@RequestMapping
public class MainController {
    
    @Value("${custom.property.1}")
    String string;
    
    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("bye bye");
    }
    
    @GetMapping("/hi")
    public ResponseEntity<String> hi() {
        
        return ResponseEntity.ok(string);
    }
}