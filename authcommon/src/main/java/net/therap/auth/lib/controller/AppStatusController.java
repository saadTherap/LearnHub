package net.therap.auth.lib.controller;

import net.therap.auth.lib.dto.StatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gazizafor
 * @since 20/8/25
 */
@RestController
@RequestMapping("/appStatus")
public class AppStatusController {
    
    @GetMapping
    public ResponseEntity<StatusDTO> status() {
        return ResponseEntity.ok(new StatusDTO("ok"));
    }
}