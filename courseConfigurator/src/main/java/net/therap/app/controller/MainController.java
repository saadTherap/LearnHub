package net.therap.app.controller;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.repository.ModuleRepository;
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
@Slf4j
public class MainController {
    
    private final ModuleRepository moduleRepository;
    
    public MainController(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }
    
    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Welcome to LearnHub!!");
    }
}