package net.therap.learningProcessor.client;

import net.therap.learningProcessor.config.FeignMultipartSupportConfig;
import net.therap.learningProcessor.dto.StoredFileDto;
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
        path = "/api/files",
        configuration = FeignMultipartSupportConfig.class)
public interface FileClient {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    StoredFileDto uploadFile(@RequestPart("file") MultipartFile file);

    @GetMapping
    List<StoredFileDto> getAllFiles();

    @GetMapping("/{id}")
    ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id);

    @DeleteMapping("/{id}")
    void deleteFile(@PathVariable("id") Long id);
}