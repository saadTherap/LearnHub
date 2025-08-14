package net.therap.app.client;

import net.therap.app.config.FeignConfig;
import net.therap.app.dto.StoredFileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@FeignClient(name = "secure-file-server",
        url = "${secure-file-server.url}",
        path = "/api/secure-file-server/files",
        configuration = FeignConfig.class)
public interface FileClient {
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    StoredFileDTO uploadFile(@RequestPart("file") MultipartFile file);
    
    @GetMapping
    List<StoredFileDTO> getAllFiles();
    
    @GetMapping("/{id}")
    ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id);
    
    @DeleteMapping("/{id}")
    void deleteFile(@PathVariable("id") Long id);
}