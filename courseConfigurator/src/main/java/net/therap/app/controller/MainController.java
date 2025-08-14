package net.therap.app.controller;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.client.FileClient;
import net.therap.app.dto.StoredFileDTO;
import net.therap.app.repository.ModuleRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author gazizafor
 * @since 20/7/25
 */
@RestController
@RequestMapping
@Slf4j
public class MainController {
    
    private final FileClient fileClient;
    private final ModuleRepository moduleRepository;
    
    public MainController(FileClient fileClient, ModuleRepository moduleRepository) {
        this.fileClient = fileClient;
        this.moduleRepository = moduleRepository;
    }
    
    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Welcome to LearnHub!!");
    }
    
    // file upload test -> add fileClient later on
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFileDTO> uploadFile(@RequestPart("file") MultipartFile file) {
        log.info("Uploading file {}", file.getOriginalFilename());
        // Your file handling logic here
        StoredFileDTO storedFileDTO = fileClient.uploadFile(file);
        return ResponseEntity.ok(storedFileDTO);
    }
    
    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") long fileId) {
        return fileClient.downloadFile(fileId);
    }
    
    @GetMapping("/files")
    public ResponseEntity<List<StoredFileDTO>> getFiles() {
        return ResponseEntity.ok(fileClient.getAllFiles());
    }
    
    @GetMapping("/test")
    public ResponseEntity<Long> test() {
        return ResponseEntity.ok(moduleRepository.findMaxOrderIndexOfModules(3));
    }
}