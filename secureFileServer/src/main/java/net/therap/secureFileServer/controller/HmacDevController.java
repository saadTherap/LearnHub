package net.therap.secureFileServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.therap.secureFileServer.entity.primary.StoredFile;
import net.therap.secureFileServer.service.FileSignatureService;
import net.therap.secureFileServer.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author avidewan
 * @since 8/17/25
 */
@RestController
@RequestMapping("/dev/hmac")
@Tag(name = "Development", description = "HMAC signature generation for testing (remove in production)")
@RequiredArgsConstructor
public class HmacDevController {

    private final FileStorageService fileStorageService;
    private final FileSignatureService fileSignatureService;

    @GetMapping("/generate")
    @Operation(summary = "Generate HMAC signature for a stored file (testing only)")
    public ResponseEntity<String> generateFileSignature(
            @RequestParam String formId
    ) {
        StoredFile file = fileStorageService.getByFormId(formId);

        String signature = fileSignatureService.generateSignature(file);

        return ResponseEntity.ok(signature);
    }
}
