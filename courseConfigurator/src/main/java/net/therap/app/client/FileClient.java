package net.therap.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@FeignClient(name = "{file.client.name}", url = "{file.client.url}")
public interface FileClient {
    
    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestBody MultipartFile file);
}